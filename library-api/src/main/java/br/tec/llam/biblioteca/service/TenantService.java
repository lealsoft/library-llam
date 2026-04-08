package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.OrganizationUnitApiClient;
import br.tec.llam.biblioteca.dto.TenantDTO;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.organizationunit.OrganizationUnitDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TenantService {

    private final OrganizationUnitApiClient organizationUnitClient;

    public TenantService(OrganizationUnitApiClient organizationUnitClient) {
        this.organizationUnitClient = organizationUnitClient;
    }

    public TenantDTO buscarPorId(String id) {
        OrganizationUnitDTO ou = organizationUnitClient.getOrganizationUnitById(id);
        if (ou == null) {
            throw new ResourceNotFoundException("Tenant não encontrado: " + id);
        }
        return toTenantDTO(ou);
    }

    public TenantDTO criar(TenantDTO dto) {
        String id = UUID.randomUUID().toString();
        
        OrganizationUnitDTO ou = new OrganizationUnitDTO();
        ou.setOrganizationUnitId(id);
        ou.setUri("urn:llam:biblioteca:tenant:" + id);

        organizationUnitClient.createOrganizationUnit(ou);

        dto.setId(id);
        dto.setAtivo(true);
        return dto;
    }

    public TenantDTO atualizar(String id, TenantDTO dto) {
        OrganizationUnitDTO existing = organizationUnitClient.getOrganizationUnitById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Tenant não encontrado: " + id);
        }
        
        organizationUnitClient.updateOrganizationUnit(existing);
        dto.setId(id);
        return dto;
    }

    private TenantDTO toTenantDTO(OrganizationUnitDTO ou) {
        TenantDTO dto = new TenantDTO();
        dto.setId(ou.getOrganizationUnitId());
        dto.setAtivo(true);
        return dto;
    }
}
