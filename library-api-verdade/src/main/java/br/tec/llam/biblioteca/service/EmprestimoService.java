package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.ClassApiClient;
import br.tec.llam.biblioteca.client.PersonApiClient;
import br.tec.llam.biblioteca.client.TransactionApiClient;
import br.tec.llam.biblioteca.dto.ConfiguracaoTenantDTO;
import br.tec.llam.biblioteca.dto.EmprestimoDTO;
import br.tec.llam.biblioteca.dto.EmprestimoMultiploDTO;
import br.tec.llam.biblioteca.exception.BusinessException;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.person.PersonDTO;
import org.mediavitae.dto.transaction.TransactionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class EmprestimoService {

    private final TransactionApiClient transactionClient;
    private final PersonApiClient personClient;
    private final ClassApiClient classClient;
    private final ConfiguracaoTenantService configuracaoService;
    private final ExemplarService exemplarService;

    private static final int PRAZO_PADRAO_DIAS = 14;
    private static final int MAX_RENOVACOES = 2;
    private static final int LIMITE_EMPRESTIMOS_PADRAO = 5;

    public EmprestimoService(TransactionApiClient transactionClient, 
                             PersonApiClient personClient,
                             ClassApiClient classClient,
                             ConfiguracaoTenantService configuracaoService,
                             ExemplarService exemplarService) {
        this.transactionClient = transactionClient;
        this.personClient = personClient;
        this.classClient = classClient;
        this.configuracaoService = configuracaoService;
        this.exemplarService = exemplarService;
    }

    public EmprestimoDTO buscarPorId(String id) {
        TransactionDTO transaction = transactionClient.getTransactionById(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Empréstimo não encontrado: " + id);
        }
        return toEmprestimoDTO(transaction);
    }

    public EmprestimoDTO realizarEmprestimo(EmprestimoDTO dto, String tenantId) {
        ConfiguracaoTenantDTO config = getConfiguracaoTenant(tenantId);
        
        // Validar leitor
        PersonDTO leitor = personClient.getPersonById(dto.getLeitorId());
        if (leitor == null) {
            throw new BusinessException("Leitor não encontrado: " + dto.getLeitorId());
        }

        // Validar se leitor está ativo
        validarLeitorAtivo(leitor);
        
        // Validar se leitor não tem pendências (multas não pagas)
        if (!config.getPermitirEmprestimoComPendencia()) {
            validarLeitorSemPendencias(dto.getLeitorId(), tenantId);
        }
        
        // Validar limite de empréstimos do leitor
        int limiteEmprestimos = config.getLimiteEmprestimosPorLeitor() != null 
            ? config.getLimiteEmprestimosPorLeitor() 
            : LIMITE_EMPRESTIMOS_PADRAO;
        validarLimiteEmprestimos(dto.getLeitorId(), limiteEmprestimos);

        // Calcular prazo baseado na configuração do tenant
        int prazoDias = config.getPrazoEmprestimoDias() != null 
            ? config.getPrazoEmprestimoDias() 
            : PRAZO_PADRAO_DIAS;

        String id = UUID.randomUUID().toString();
        LocalDate dataEmprestimo = LocalDate.now();
        LocalDate dataPrevistaDevolucao = dataEmprestimo.plusDays(prazoDias);

        TransactionDTO transaction = new TransactionDTO(
            id,                                          // transactionId
            dto.getLeitorId(),                           // federatedIdentifierId (leitor)
            getClassIdEmprestimo(),                      // classId (tipo de transação)
            1.0f,                                        // quantity
            "urn:llam:biblioteca:" + tenantId + ":emprestimo:" + id + ":exemplar:" + dto.getExemplarId(),
            java.time.LocalDateTime.now()                // transactionDate
        );

        transactionClient.createTransaction(transaction);

        dto.setId(id);
        dto.setTenantId(tenantId);
        dto.setDataEmprestimo(dataEmprestimo);
        dto.setDataPrevistaDevolucao(dataPrevistaDevolucao);
        dto.setStatusEmprestimo("EMPRESTADO");
        dto.setRenovacoes(0);
        dto.setValorMulta(0.0);
        dto.setMultaPaga(false);

        return dto;
    }

    public EmprestimoDTO realizarDevolucao(String id) {
        TransactionDTO transaction = transactionClient.getTransactionById(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Empréstimo não encontrado: " + id);
        }

        EmprestimoDTO dto = toEmprestimoDTO(transaction);
        LocalDate hoje = LocalDate.now();
        dto.setDataRealDevolucao(hoje);

        // Calcular multa se houver atraso
        if (dto.getDataPrevistaDevolucao() != null && hoje.isAfter(dto.getDataPrevistaDevolucao())) {
            long diasAtraso = java.time.temporal.ChronoUnit.DAYS.between(dto.getDataPrevistaDevolucao(), hoje);
            double valorMultaPorDia = 1.0; // TODO: buscar configuração do tenant
            dto.setValorMulta(diasAtraso * valorMultaPorDia);
            dto.setMultaPaga(false);
        }

        dto.setStatusEmprestimo("DEVOLVIDO");

        TransactionDTO estorno = new TransactionDTO(
            UUID.randomUUID().toString(),
            transaction.getFederatedIdentifierId(),
            transaction.getClassId(),
            -(transaction.getQuantity() != null ? transaction.getQuantity() : 1.0f),
            transaction.getDocReference(),
            java.time.LocalDateTime.now()
        );
        transactionClient.createTransaction(estorno);

        return dto;
    }

    public EmprestimoDTO renovar(String id, String tenantId) {
        TransactionDTO transaction = transactionClient.getTransactionById(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Empréstimo não encontrado: " + id);
        }

        EmprestimoDTO dto = toEmprestimoDTO(transaction);
        ConfiguracaoTenantDTO config = getConfiguracaoTenant(tenantId);

        // Validar limite de renovações
        int maxRenovacoes = config.getLimiteRenovacoes() != null 
            ? config.getLimiteRenovacoes() 
            : MAX_RENOVACOES;
        if (dto.getRenovacoes() != null && dto.getRenovacoes() >= maxRenovacoes) {
            throw new BusinessException("Limite de renovações atingido (" + maxRenovacoes + ")");
        }

        // Validar se não há reserva para o exemplar (se configurado para bloquear)
        if (!config.getPermitirRenovacaoComReserva()) {
            if (verificarReservaParaExemplar(dto.getExemplarId())) {
                throw new BusinessException("Não é possível renovar: existe reserva para este exemplar");
            }
        }

        // Calcular novo prazo
        int prazoDias = config.getPrazoEmprestimoDias() != null 
            ? config.getPrazoEmprestimoDias() 
            : PRAZO_PADRAO_DIAS;
        LocalDate novaDataDevolucao = LocalDate.now().plusDays(prazoDias);
        dto.setDataPrevistaDevolucao(novaDataDevolucao);
        dto.setRenovacoes((dto.getRenovacoes() != null ? dto.getRenovacoes() : 0) + 1);

        return dto;
    }

    private EmprestimoDTO toEmprestimoDTO(TransactionDTO transaction) {
        EmprestimoDTO dto = new EmprestimoDTO();
        dto.setId(transaction.getTransactionId());
        dto.setLeitorId(transaction.getFederatedIdentifierId());
        
        if (transaction.getDocReference() != null && transaction.getDocReference().contains(":emprestimo:")) {
            String[] parts = transaction.getDocReference().split(":");
            if (parts.length >= 4) {
                dto.setTenantId(parts[3]);
            }
        }
        
        dto.setStatusEmprestimo("EMPRESTADO");
        dto.setRenovacoes(0);
        return dto;
    }

    private Integer getClassIdEmprestimo() {
        // TODO: Buscar do class-backend pelo constant LIB_TIPO_TRANSACAO_EMPRESTIMO
        return 1;
    }

    private ConfiguracaoTenantDTO getConfiguracaoTenant(String tenantId) {
        try {
            return configuracaoService.buscarConfiguracoes(tenantId);
        } catch (Exception e) {
            // Retorna configuração padrão se não encontrar
            return ConfiguracaoTenantDTO.builder()
                .tenantId(tenantId)
                .prazoEmprestimoDias(PRAZO_PADRAO_DIAS)
                .limiteEmprestimosPorLeitor(LIMITE_EMPRESTIMOS_PADRAO)
                .limiteRenovacoes(MAX_RENOVACOES)
                .valorMultaPorDia(1.0)
                .prazoRetiradaReservaDias(3)
                .permitirEmprestimoComPendencia(false)
                .permitirRenovacaoComReserva(false)
                .build();
        }
    }

    private void validarLeitorAtivo(PersonDTO leitor) {
        // Verifica se o leitor está ativo baseado no URI ou outro campo
        if (leitor.getUri() != null && leitor.getUri().contains(":suspenso:")) {
            throw new BusinessException("Leitor está suspenso e não pode realizar empréstimos");
        }
        if (leitor.getUri() != null && leitor.getUri().contains(":inativo:")) {
            throw new BusinessException("Leitor está inativo e não pode realizar empréstimos");
        }
    }

    private void validarLeitorSemPendencias(String leitorId, String tenantId) {
        // TODO: Buscar multas pendentes do leitor via MultaService
        // Por enquanto, não bloqueia
    }

    private void validarLimiteEmprestimos(String leitorId, int limite) {
        // TODO: Contar empréstimos ativos do leitor
        // Por enquanto, não bloqueia
    }

    private boolean verificarReservaParaExemplar(String exemplarId) {
        // TODO: Verificar se há reserva ativa para o exemplar
        return false;
    }

    public EmprestimoMultiploDTO realizarEmprestimoMultiplo(EmprestimoMultiploDTO dto, String tenantId) {
        List<EmprestimoDTO> realizados = new java.util.ArrayList<>();
        List<String> erros = new java.util.ArrayList<>();

        for (String exemplarId : dto.getExemplarIds()) {
            try {
                EmprestimoDTO emprestimo = new EmprestimoDTO();
                emprestimo.setLeitorId(dto.getLeitorId());
                emprestimo.setExemplarId(exemplarId);
                
                EmprestimoDTO resultado = realizarEmprestimo(emprestimo, tenantId);
                realizados.add(resultado);
            } catch (BusinessException e) {
                erros.add("Exemplar " + exemplarId + ": " + e.getMessage());
            } catch (Exception e) {
                erros.add("Exemplar " + exemplarId + ": Erro inesperado");
            }
        }

        dto.setEmprestimosRealizados(realizados);
        dto.setErros(erros);
        return dto;
    }

    public List<EmprestimoDTO> realizarDevolucaoMultipla(List<String> emprestimoIds) {
        List<EmprestimoDTO> devolvidos = new java.util.ArrayList<>();

        for (String id : emprestimoIds) {
            try {
                EmprestimoDTO resultado = realizarDevolucao(id);
                devolvidos.add(resultado);
            } catch (Exception e) {
                // Log error but continue with other devolutions
            }
        }

        return devolvidos;
    }
}
