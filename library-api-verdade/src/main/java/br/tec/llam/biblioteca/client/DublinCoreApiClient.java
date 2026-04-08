package br.tec.llam.biblioteca.client;

import br.tec.llam.biblioteca.config.LlamApiProperties;
import org.mediavitae.dto.dublincore.DublinCoreDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class DublinCoreApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public DublinCoreApiClient(RestTemplate restTemplate, LlamApiProperties properties) {
        this.restTemplate = restTemplate;
        this.baseUrl = properties.getDublincoreUrl();
    }

    @SuppressWarnings("unchecked")
    public List<DublinCoreDTO> getAllDublinCore() {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                    baseUrl + "/dublinCore",
                    Map.class
            );
            
            if (response != null && response.containsKey("_embedded")) {
                Map<String, Object> embedded = (Map<String, Object>) response.get("_embedded");
                if (embedded.containsKey("dublinCoreDTOList")) {
                    List<Map<String, Object>> dcList = (List<Map<String, Object>>) embedded.get("dublinCoreDTOList");
                    return dcList.stream()
                            .map(this::mapToDublinCoreDTO)
                            .toList();
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private DublinCoreDTO mapToDublinCoreDTO(Map<String, Object> map) {
        DublinCoreDTO dto = new DublinCoreDTO();
        dto.setDublinCoreId((String) map.get("dublinCoreId"));
        dto.setClassSchemeId((String) map.get("classSchemeId"));
        dto.setUri((String) map.get("uri"));
        dto.setLanguageId(map.get("languageId") != null ? ((Number) map.get("languageId")).intValue() : null);
        dto.setTranslator((String) map.get("translator"));
        return dto;
    }

    public DublinCoreDTO getDublinCoreById(String id) {
        return restTemplate.getForObject(
                baseUrl + "/dublinCore/{id}",
                DublinCoreDTO.class,
                id
        );
    }

    public DublinCoreDTO createDublinCore(DublinCoreDTO dto) {
        return restTemplate.postForObject(
                baseUrl + "/dublinCore",
                dto,
                DublinCoreDTO.class
        );
    }

    public void updateDublinCore(DublinCoreDTO dto) {
        restTemplate.put(baseUrl + "/dublinCore", dto);
    }

    public void deleteDublinCore(String id) {
        restTemplate.delete(baseUrl + "/dublinCore/{id}", id);
    }

    // ==================== EVA (Entity-Value-Attribute) Methods ====================

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getValueStringsByDublinCoreId(String dublinCoreId) {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                    baseUrl + "/dublinCoreValueString/byDublinCore/{dublinCoreId}",
                    Map.class,
                    dublinCoreId
            );
            if (response != null && response.containsKey("_embedded")) {
                Map<String, Object> embedded = (Map<String, Object>) response.get("_embedded");
                if (embedded.containsKey("dublinCoreValueStringDTOList")) {
                    return (List<Map<String, Object>>) embedded.get("dublinCoreValueStringDTOList");
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getValueIntegersByDublinCoreId(String dublinCoreId) {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                    baseUrl + "/dublinCoreValueInteger/byDublinCore/{dublinCoreId}",
                    Map.class,
                    dublinCoreId
            );
            if (response != null && response.containsKey("_embedded")) {
                Map<String, Object> embedded = (Map<String, Object>) response.get("_embedded");
                if (embedded.containsKey("dublinCoreValueIntegerDTOList")) {
                    return (List<Map<String, Object>>) embedded.get("dublinCoreValueIntegerDTOList");
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getValueTextsByDublinCoreId(String dublinCoreId) {
        try {
            Map<String, Object> response = restTemplate.getForObject(
                    baseUrl + "/dublinCoreValueText/byDublinCore/{dublinCoreId}",
                    Map.class,
                    dublinCoreId
            );
            if (response != null && response.containsKey("_embedded")) {
                Map<String, Object> embedded = (Map<String, Object>) response.get("_embedded");
                if (embedded.containsKey("dublinCoreValueTextDTOList")) {
                    return (List<Map<String, Object>>) embedded.get("dublinCoreValueTextDTOList");
                }
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void createValueString(String dublinCoreId, Integer classId, String value) {
        Map<String, Object> dto = Map.of(
                "dublinCoreId", dublinCoreId,
                "classId", classId,
                "valueString", value
        );
        restTemplate.postForObject(baseUrl + "/dublinCoreValueString", dto, Map.class);
    }

    public void createValueInteger(String dublinCoreId, Integer classId, Integer value) {
        Map<String, Object> dto = Map.of(
                "dublinCoreId", dublinCoreId,
                "classId", classId,
                "valueInteger", value
        );
        restTemplate.postForObject(baseUrl + "/dublinCoreValueInteger", dto, Map.class);
    }

    public void createValueText(String dublinCoreId, Integer classId, String value) {
        Map<String, Object> dto = Map.of(
                "dublinCoreId", dublinCoreId,
                "classId", classId,
                "valueText", value
        );
        restTemplate.postForObject(baseUrl + "/dublinCoreValueText", dto, Map.class);
    }
}
