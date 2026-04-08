package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.ClassApiClient;
import br.tec.llam.biblioteca.client.OrganizationUnitApiClient;
import br.tec.llam.biblioteca.dto.ConfiguracaoTenantDTO;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.organizationunit.OrganizationUnitDTO;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracaoTenantService {

    private final OrganizationUnitApiClient organizationUnitClient;
    private final ClassApiClient classClient;

    public ConfiguracaoTenantService(OrganizationUnitApiClient organizationUnitClient, ClassApiClient classClient) {
        this.organizationUnitClient = organizationUnitClient;
        this.classClient = classClient;
    }

    public ConfiguracaoTenantDTO buscarConfiguracoes(String tenantId) {
        OrganizationUnitDTO ou = organizationUnitClient.getOrganizationUnitById(tenantId);
        if (ou == null) {
            throw new ResourceNotFoundException("Tenant não encontrado: " + tenantId);
        }

        return ConfiguracaoTenantDTO.builder()
            .tenantId(tenantId)
            .prazoEmprestimoDias(14)
            .limiteEmprestimosPorLeitor(5)
            .limiteRenovacoes(2)
            .valorMultaPorDia(1.0)
            .prazoRetiradaReservaDias(3)
            .permitirEmprestimoComPendencia(false)
            .permitirRenovacaoComReserva(false)
            .build();
    }

    public ConfiguracaoTenantDTO atualizarConfiguracoes(String tenantId, ConfiguracaoTenantDTO dto) {
        OrganizationUnitDTO ou = organizationUnitClient.getOrganizationUnitById(tenantId);
        if (ou == null) {
            throw new ResourceNotFoundException("Tenant não encontrado: " + tenantId);
        }

        dto.setTenantId(tenantId);
        return dto;
    }
}
