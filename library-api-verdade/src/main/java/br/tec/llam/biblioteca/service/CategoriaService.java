package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.ClassApiClient;
import br.tec.llam.biblioteca.dto.CategoriaDTO;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.classification.ClassificationDTO;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final ClassApiClient classClient;

    public CategoriaService(ClassApiClient classClient) {
        this.classClient = classClient;
    }

    public CategoriaDTO buscarPorId(Integer classId, Integer classSchemeId) {
        ClassificationDTO classification = classClient.getClassById(classId, classSchemeId);
        if (classification == null) {
            throw new ResourceNotFoundException("Categoria não encontrada: " + classId);
        }
        return toCategoriaDTO(classification);
    }

    public CategoriaDTO criar(CategoriaDTO dto, String tenantId) {
        ClassificationDTO classification = new ClassificationDTO();
        classification.setClassSchemeId(getClassSchemeIdCategoria());
        classification.setUri("urn:llam:biblioteca:" + tenantId + ":categoria:" + dto.getNome().toLowerCase().replace(" ", "_"));
        classification.setConstant(dto.getNome().toUpperCase().replace(" ", "_"));

        ClassificationDTO created = classClient.createClass(classification);

        dto.setId(created.getClassId());
        dto.setTenantId(tenantId);
        return dto;
    }

    private CategoriaDTO toCategoriaDTO(ClassificationDTO classification) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(classification.getClassId());
        dto.setNome(classification.getConstant());
        
        if (classification.getUri() != null && classification.getUri().contains(":categoria:")) {
            String[] parts = classification.getUri().split(":");
            if (parts.length >= 4) {
                dto.setTenantId(parts[3]);
            }
        }
        
        return dto;
    }

    private Integer getClassSchemeIdCategoria() {
        return 4;
    }
}
