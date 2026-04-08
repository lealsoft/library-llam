package br.tec.llam.biblioteca.client;

import br.tec.llam.biblioteca.config.LlamApiProperties;
import org.mediavitae.dto.classification.ClassificationDTO;
import org.mediavitae.dto.classification.ClassSchemeDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ClassApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ClassApiClient(RestTemplate restTemplate, LlamApiProperties properties) {
        this.restTemplate = restTemplate;
        this.baseUrl = properties.getClassUrl();
    }

    public ClassSchemeDTO getClassSchemeByConstant(String constant) {
        return restTemplate.getForObject(
                baseUrl + "/classScheme/byConstant/{constant}",
                ClassSchemeDTO.class,
                constant
        );
    }

    public ClassificationDTO getClassByConstant(String constant) {
        return restTemplate.getForObject(
                baseUrl + "/class/byConstant/{constant}",
                ClassificationDTO.class,
                constant
        );
    }

    public ClassificationDTO createClass(ClassificationDTO dto) {
        return restTemplate.postForObject(
                baseUrl + "/class",
                dto,
                ClassificationDTO.class
        );
    }

    public ClassificationDTO getClassById(Integer classId, Integer classSchemeId) {
        return restTemplate.getForObject(
                baseUrl + "/class/id/{classId}/{classSchemeId}",
                ClassificationDTO.class,
                classId,
                classSchemeId
        );
    }
}
