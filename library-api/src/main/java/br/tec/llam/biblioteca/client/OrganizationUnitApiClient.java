package br.tec.llam.biblioteca.client;

import br.tec.llam.biblioteca.config.LlamApiProperties;
import org.mediavitae.dto.organizationunit.OrganizationUnitDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationUnitApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public OrganizationUnitApiClient(RestTemplate restTemplate, LlamApiProperties properties) {
        this.restTemplate = restTemplate;
        this.baseUrl = properties.getOrganizationunitUrl();
    }

    public OrganizationUnitDTO getOrganizationUnitById(String id) {
        return restTemplate.getForObject(
                baseUrl + "/orgunit/{id}",
                OrganizationUnitDTO.class,
                id
        );
    }

    public OrganizationUnitDTO createOrganizationUnit(OrganizationUnitDTO dto) {
        return restTemplate.postForObject(
                baseUrl + "/orgunit",
                dto,
                OrganizationUnitDTO.class
        );
    }

    public void updateOrganizationUnit(OrganizationUnitDTO dto) {
        restTemplate.put(baseUrl + "/orgunit", dto);
    }
}
