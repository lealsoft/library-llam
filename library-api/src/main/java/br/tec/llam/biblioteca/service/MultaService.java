package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.TransactionApiClient;
import br.tec.llam.biblioteca.dto.MultaDTO;
import br.tec.llam.biblioteca.exception.BusinessException;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.transaction.TransactionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class MultaService {

    private final TransactionApiClient transactionClient;

    private static final double VALOR_MULTA_POR_DIA = 1.0;

    public MultaService(TransactionApiClient transactionClient) {
        this.transactionClient = transactionClient;
    }

    public MultaDTO buscarPorId(String id) {
        TransactionDTO transaction = transactionClient.getTransactionById(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Multa não encontrada: " + id);
        }
        return toMultaDTO(transaction);
    }

    public MultaDTO gerarMulta(String leitorId, String emprestimoId, int diasAtraso, String tenantId) {
        if (diasAtraso <= 0) {
            throw new BusinessException("Dias de atraso deve ser maior que zero");
        }

        String id = UUID.randomUUID().toString();
        double valor = diasAtraso * VALOR_MULTA_POR_DIA;

        TransactionDTO transaction = new TransactionDTO(
            id,
            leitorId,
            getClassIdMulta(),
            (float) valor,
            "urn:llam:biblioteca:" + tenantId + ":multa:" + id + ":emprestimo:" + emprestimoId,
            java.time.LocalDateTime.now()
        );

        transactionClient.createTransaction(transaction);

        return MultaDTO.builder()
            .id(id)
            .leitorId(leitorId)
            .emprestimoId(emprestimoId)
            .valor(valor)
            .dataGeracao(LocalDate.now())
            .statusMulta("PENDENTE")
            .motivoMulta("Atraso de " + diasAtraso + " dias")
            .tenantId(tenantId)
            .build();
    }

    public MultaDTO pagar(String id) {
        TransactionDTO transaction = transactionClient.getTransactionById(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Multa não encontrada: " + id);
        }

        MultaDTO dto = toMultaDTO(transaction);
        dto.setStatusMulta("PAGA");
        dto.setDataPagamento(LocalDate.now());

        return dto;
    }

    public MultaDTO isentar(String id, String motivo) {
        TransactionDTO transaction = transactionClient.getTransactionById(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Multa não encontrada: " + id);
        }

        MultaDTO dto = toMultaDTO(transaction);
        dto.setStatusMulta("ISENTA");
        dto.setMotivoMulta(motivo);

        return dto;
    }

    private MultaDTO toMultaDTO(TransactionDTO transaction) {
        MultaDTO dto = new MultaDTO();
        dto.setId(transaction.getTransactionId());
        dto.setLeitorId(transaction.getFederatedIdentifierId());
        dto.setValor((double) transaction.getQuantity());
        
        if (transaction.getDocReference() != null && transaction.getDocReference().contains(":multa:")) {
            String[] parts = transaction.getDocReference().split(":");
            if (parts.length >= 4) {
                dto.setTenantId(parts[3]);
            }
        }
        
        dto.setStatusMulta("PENDENTE");
        return dto;
    }

    private Integer getClassIdMulta() {
        return 3;
    }
}
