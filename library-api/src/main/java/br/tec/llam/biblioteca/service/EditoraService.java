package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.OrganizationUnitApiClient;
import br.tec.llam.biblioteca.dto.EditoraDTO;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.organizationunit.OrganizationUnitDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EditoraService {

    private final OrganizationUnitApiClient organizationUnitClient;

    public EditoraService(OrganizationUnitApiClient organizationUnitClient) {
        this.organizationUnitClient = organizationUnitClient;
    }

    public EditoraDTO buscarPorId(String id) {
        OrganizationUnitDTO ou = organizationUnitClient.getOrganizationUnitById(id);
        if (ou == null) {
            throw new ResourceNotFoundException("Editora não encontrada: " + id);
        }
        return toEditoraDTO(ou);
    }

    public EditoraDTO criar(EditoraDTO dto, String tenantId) {
        String id = UUID.randomUUID().toString();
        
        OrganizationUnitDTO ou = new OrganizationUnitDTO();
        ou.setOrganizationUnitId(id);
        ou.setUri("urn:llam:biblioteca:" + tenantId + ":editora:" + id);

        organizationUnitClient.createOrganizationUnit(ou);

        dto.setId(id);
        dto.setTenantId(tenantId);
        return dto;
    }

    private EditoraDTO toEditoraDTO(OrganizationUnitDTO ou) {
        EditoraDTO dto = new EditoraDTO();
        dto.setId(ou.getOrganizationUnitId());
        
        if (ou.getUri() != null && ou.getUri().contains(":editora:")) {
            String[] parts = ou.getUri().split(":");
            if (parts.length >= 4) {
                dto.setTenantId(parts[3]);
            }
        }
        
        return dto;
    }
}
