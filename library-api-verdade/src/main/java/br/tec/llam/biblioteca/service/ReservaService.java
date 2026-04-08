package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.PersonApiClient;
import br.tec.llam.biblioteca.client.TransactionApiClient;
import br.tec.llam.biblioteca.dto.ReservaDTO;
import br.tec.llam.biblioteca.exception.BusinessException;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.person.PersonDTO;
import org.mediavitae.dto.transaction.TransactionDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class ReservaService {

    private final TransactionApiClient transactionClient;
    private final PersonApiClient personClient;

    private static final int PRAZO_RETIRADA_DIAS = 3;

    public ReservaService(TransactionApiClient transactionClient, PersonApiClient personClient) {
        this.transactionClient = transactionClient;
        this.personClient = personClient;
    }

    public ReservaDTO buscarPorId(String id) {
        TransactionDTO transaction = transactionClient.getTransactionById(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Reserva não encontrada: " + id);
        }
        return toReservaDTO(transaction);
    }

    public ReservaDTO criar(ReservaDTO dto, String tenantId) {
        PersonDTO leitor = personClient.getPersonById(dto.getLeitorId());
        if (leitor == null) {
            throw new BusinessException("Leitor não encontrado: " + dto.getLeitorId());
        }

        String id = UUID.randomUUID().toString();
        LocalDate dataReserva = LocalDate.now();
        LocalDate dataExpiracao = dataReserva.plusDays(PRAZO_RETIRADA_DIAS);

        TransactionDTO transaction = new TransactionDTO(
            id,
            dto.getLeitorId(),
            getClassIdReserva(),
            1.0f,
            "urn:llam:biblioteca:" + tenantId + ":reserva:" + id,
            java.time.LocalDateTime.now()
        );

        transactionClient.createTransaction(transaction);

        dto.setId(id);
        dto.setTenantId(tenantId);
        dto.setDataReserva(dataReserva);
        dto.setDataExpiracao(dataExpiracao);
        dto.setStatusReserva("ATIVA");
        dto.setPosicaoFila(1);

        return dto;
    }

    public ReservaDTO cancelar(String id) {
        TransactionDTO transaction = transactionClient.getTransactionById(id);
        if (transaction == null) {
            throw new ResourceNotFoundException("Reserva não encontrada: " + id);
        }

        ReservaDTO dto = toReservaDTO(transaction);
        dto.setStatusReserva("CANCELADA");

        return dto;
    }

    private ReservaDTO toReservaDTO(TransactionDTO transaction) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(transaction.getTransactionId());
        dto.setLeitorId(transaction.getFederatedIdentifierId());
        
        if (transaction.getDocReference() != null && transaction.getDocReference().contains(":reserva:")) {
            String[] parts = transaction.getDocReference().split(":");
            if (parts.length >= 4) {
                dto.setTenantId(parts[3]);
            }
        }
        
        dto.setStatusReserva("ATIVA");
        dto.setPosicaoFila(1);
        return dto;
    }

    private Integer getClassIdReserva() {
        return 2;
    }
}
