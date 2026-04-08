package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.ClassApiClient;
import br.tec.llam.biblioteca.client.TransactionApiClient;
import br.tec.llam.biblioteca.dto.ExemplarDTO;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.classification.ClassificationDTO;
import org.springframework.stereotype.Service;

@Service
public class ExemplarService {

    private final ClassApiClient classClient;
    private final TransactionApiClient transactionClient;

    public ExemplarService(ClassApiClient classClient, TransactionApiClient transactionClient) {
        this.classClient = classClient;
        this.transactionClient = transactionClient;
    }

    public ExemplarDTO buscarPorId(Integer classId, Integer classSchemeId) {
        ClassificationDTO classification = classClient.getClassById(classId, classSchemeId);
        if (classification == null) {
            throw new ResourceNotFoundException("Exemplar não encontrado: " + classId);
        }
        return toExemplarDTO(classification);
    }

    public ExemplarDTO criar(ExemplarDTO dto, String tenantId) {
        ClassificationDTO classification = new ClassificationDTO();
        classification.setClassSchemeId(getClassSchemeIdExemplar());
        classification.setUri("urn:llam:biblioteca:" + tenantId + ":exemplar:" + dto.getCodigoBarras());
        classification.setConstant(dto.getCodigoBarras());

        ClassificationDTO created = classClient.createClass(classification);

        dto.setId(created.getClassId());
        dto.setTenantId(tenantId);
        dto.setStatusExemplar("DISPONIVEL");
        return dto;
    }

    // Método atualizarStatus removido. O status agora é derivado dinamicamente das transações.

    private ExemplarDTO toExemplarDTO(ClassificationDTO classification) {
        ExemplarDTO dto = new ExemplarDTO();
        dto.setId(classification.getClassId());
        dto.setCodigoBarras(classification.getConstant());
        
        if (classification.getUri() != null && classification.getUri().contains(":exemplar:")) {
            String[] parts = classification.getUri().split(":");
            if (parts.length >= 4) {
                dto.setTenantId(parts[3]);
            }
        }
        
        Double balance = transactionClient.getBalanceByExemplar(classification.getConstant());
        if (balance != null && balance > 0) {
            dto.setStatusExemplar("EMPRESTADO");
        } else {
            dto.setStatusExemplar("DISPONIVEL");
        }
        
        return dto;
    }

    private Integer getClassSchemeIdExemplar() {
        // TODO: Buscar do class-backend pelo constant LIB_EXEMPLAR
        return 1;
    }
}
