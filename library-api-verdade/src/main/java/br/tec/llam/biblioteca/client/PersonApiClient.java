package br.tec.llam.biblioteca.client;

import br.tec.llam.biblioteca.config.LlamApiProperties;
import org.mediavitae.dto.person.PersonDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class PersonApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PersonApiClient(RestTemplate restTemplate, LlamApiProperties properties) {
        this.restTemplate = restTemplate;
        this.baseUrl = properties.getPersonUrl();
    }

    public PersonDTO getPersonById(String id) {
        return restTemplate.getForObject(
                baseUrl + "/person/{id}",
                PersonDTO.class,
                id
        );
    }

    public PersonDTO createPerson(PersonDTO dto) {
        return restTemplate.postForObject(
                baseUrl + "/person",
                dto,
                PersonDTO.class
        );
    }

    public void updatePerson(PersonDTO dto) {
        restTemplate.put(baseUrl + "/person", dto);
    }

    public void deletePerson(String id) {
        restTemplate.delete(baseUrl + "/person/{id}", id);
    }

    @SuppressWarnings("unchecked")
    public List<PersonDTO> getAllPersons() {
        try {
            // Tenta buscar lista de persons (Spring HATEOAS format)
            Map<String, Object> response = restTemplate.getForObject(
                    baseUrl + "/person",
                    Map.class
            );
            
            if (response != null && response.containsKey("_embedded")) {
                Map<String, Object> embedded = (Map<String, Object>) response.get("_embedded");
                // person-backend retorna "personDTOList" no formato HATEOAS
                if (embedded.containsKey("personDTOList")) {
                    List<Map<String, Object>> personList = (List<Map<String, Object>>) embedded.get("personDTOList");
                    return personList.stream()
                            .map(this::mapToPersonDTO)
                            .toList();
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private PersonDTO mapToPersonDTO(Map<String, Object> map) {
        String personId = (String) map.get("personId");
        String uri = (String) map.get("uri");
        String motherName = (String) map.get("motherName");
        java.time.LocalDate birthDate = null;
        if (map.get("birthDate") != null) {
            birthDate = java.time.LocalDate.parse((String) map.get("birthDate"));
        }
        
        // PersonDTO requer todos os campos no construtor
        return new PersonDTO(
            personId,                                    // personId
            java.time.LocalTime.MIDNIGHT,                // birthHour
            "",                                          // birthHourSHA1
            birthDate != null ? birthDate : java.time.LocalDate.of(1900, 1, 1), // birthDate
            "",                                          // birthDateSHA1
            "",                                          // birthCity
            "",                                          // birthCitySHA1
            "",                                          // birthState
            "",                                          // birthStateSHA1
            "",                                          // birthCountry
            "",                                          // birthCountrySHA1
            motherName != null ? motherName : "",        // motherName
            "",                                          // motherNameSHA1
            "",                                          // sha1
            uri != null ? uri : ""                       // uri
        );
    }
}
