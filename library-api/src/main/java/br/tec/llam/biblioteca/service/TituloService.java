package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.client.DublinCoreApiClient;
import br.tec.llam.biblioteca.config.TituloAttributeConfig;
import br.tec.llam.biblioteca.dto.TituloDTO;
import br.tec.llam.biblioteca.exception.ResourceNotFoundException;
import org.mediavitae.dto.dublincore.DublinCoreDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TituloService {

    private final DublinCoreApiClient dublinCoreClient;
    private final TituloAttributeConfig attributeConfig;

    public TituloService(DublinCoreApiClient dublinCoreClient, TituloAttributeConfig attributeConfig) {
        this.dublinCoreClient = dublinCoreClient;
        this.attributeConfig = attributeConfig;
    }

    public List<TituloDTO> listarTodos(String tenantId) {
        List<DublinCoreDTO> allDublinCore = dublinCoreClient.getAllDublinCore();
        
        return allDublinCore.stream()
                .filter(dc -> dc.getUri() != null && dc.getUri().contains(":titulo:"))
                .filter(dc -> tenantId == null || dc.getUri().contains(":" + tenantId + ":"))
                .map(this::toTituloDTO)
                .collect(Collectors.toList());
    }

    public TituloDTO buscarPorId(String id) {
        DublinCoreDTO dc = dublinCoreClient.getDublinCoreById(id);
        if (dc == null) {
            throw new ResourceNotFoundException("Título não encontrado: " + id);
        }
        return toTituloDTO(dc);
    }

    // ClassSchemeId para títulos de biblioteca (ID numérico como String)
    private static final String CLASS_SCHEME_ID_TITULO = "1";

    public TituloDTO criar(TituloDTO dto, String tenantId) {
        String id = UUID.randomUUID().toString();
        
        // 1. Criar DublinCore principal
        DublinCoreDTO dublinCore = new DublinCoreDTO();
        dublinCore.setDublinCoreId(id);
        dublinCore.setClassSchemeId(CLASS_SCHEME_ID_TITULO);
        dublinCore.setUri("urn:llam:biblioteca:" + tenantId + ":titulo:" + id);
        dublinCore.setLanguageId(1);
        dublinCore.setTranslator("N");
        dublinCoreClient.createDublinCore(dublinCore);

        // 2. Criar atributos EVA (String)
        if (dto.getTitulo() != null && attributeConfig.getTitulo() != null) {
            dublinCoreClient.createValueString(id, attributeConfig.getTitulo(), dto.getTitulo());
        }
        if (dto.getSubtitulo() != null && attributeConfig.getSubtitulo() != null) {
            dublinCoreClient.createValueString(id, attributeConfig.getSubtitulo(), dto.getSubtitulo());
        }
        if (dto.getIsbn() != null && attributeConfig.getIsbn() != null) {
            dublinCoreClient.createValueString(id, attributeConfig.getIsbn(), dto.getIsbn());
        }
        if (dto.getAutor() != null && attributeConfig.getAutor() != null) {
            dublinCoreClient.createValueString(id, attributeConfig.getAutor(), dto.getAutor());
        }
        if (dto.getEditora() != null && attributeConfig.getEditora() != null) {
            dublinCoreClient.createValueString(id, attributeConfig.getEditora(), dto.getEditora());
        }
        if (dto.getCategoria() != null && attributeConfig.getCategoria() != null) {
            dublinCoreClient.createValueString(id, attributeConfig.getCategoria(), dto.getCategoria());
        }

        // 3. Criar atributos EVA (Integer)
        if (dto.getAnoPublicacao() != null && attributeConfig.getAnoPublicacao() != null) {
            dublinCoreClient.createValueInteger(id, attributeConfig.getAnoPublicacao(), dto.getAnoPublicacao());
        }

        // 4. Criar atributos EVA (Text)
        if (dto.getSinopse() != null && attributeConfig.getSinopse() != null) {
            dublinCoreClient.createValueText(id, attributeConfig.getSinopse(), dto.getSinopse());
        }

        dto.setId(id);
        dto.setTenantId(tenantId);
        return dto;
    }

    public TituloDTO atualizar(String id, TituloDTO dto) {
        DublinCoreDTO existing = dublinCoreClient.getDublinCoreById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Título não encontrado: " + id);
        }
        existing.setTranslator(dto.getTitulo() != null ? dto.getTitulo() : existing.getTranslator());
        dublinCoreClient.updateDublinCore(existing);
        dto.setId(id);
        return dto;
    }

    public void excluir(String id) {
        DublinCoreDTO existing = dublinCoreClient.getDublinCoreById(id);
        if (existing == null) {
            throw new ResourceNotFoundException("Título não encontrado: " + id);
        }
        dublinCoreClient.deleteDublinCore(id);
    }

    private TituloDTO toTituloDTO(DublinCoreDTO dc) {
        TituloDTO dto = new TituloDTO();
        dto.setId(dc.getDublinCoreId());
        
        // Extrair tenantId da URI
        if (dc.getUri() != null && dc.getUri().contains(":titulo:")) {
            String[] parts = dc.getUri().split(":");
            if (parts.length >= 4) {
                dto.setTenantId(parts[3]);
            }
        }
        
        // Buscar atributos EVA (String)
        List<Map<String, Object>> stringValues = dublinCoreClient.getValueStringsByDublinCoreId(dc.getDublinCoreId());
        for (Map<String, Object> value : stringValues) {
            Integer classId = value.get("classId") != null ? ((Number) value.get("classId")).intValue() : null;
            String valueString = (String) value.get("valueString");
            
            if (classId != null && valueString != null) {
                if (classId.equals(attributeConfig.getTitulo())) {
                    dto.setTitulo(valueString);
                } else if (classId.equals(attributeConfig.getSubtitulo())) {
                    dto.setSubtitulo(valueString);
                } else if (classId.equals(attributeConfig.getIsbn())) {
                    dto.setIsbn(valueString);
                } else if (classId.equals(attributeConfig.getAutor())) {
                    dto.setAutor(valueString);
                } else if (classId.equals(attributeConfig.getEditora())) {
                    dto.setEditora(valueString);
                } else if (classId.equals(attributeConfig.getCategoria())) {
                    dto.setCategoria(valueString);
                }
            }
        }
        
        // Buscar atributos EVA (Integer)
        List<Map<String, Object>> integerValues = dublinCoreClient.getValueIntegersByDublinCoreId(dc.getDublinCoreId());
        for (Map<String, Object> value : integerValues) {
            Integer classId = value.get("classId") != null ? ((Number) value.get("classId")).intValue() : null;
            Integer valueInteger = value.get("valueInteger") != null ? ((Number) value.get("valueInteger")).intValue() : null;
            
            if (classId != null && valueInteger != null) {
                if (classId.equals(attributeConfig.getAnoPublicacao())) {
                    dto.setAnoPublicacao(valueInteger);
                }
            }
        }
        
        // Buscar atributos EVA (Text)
        List<Map<String, Object>> textValues = dublinCoreClient.getValueTextsByDublinCoreId(dc.getDublinCoreId());
        for (Map<String, Object> value : textValues) {
            Integer classId = value.get("classId") != null ? ((Number) value.get("classId")).intValue() : null;
            String valueText = (String) value.get("valueText");
            
            if (classId != null && valueText != null) {
                if (classId.equals(attributeConfig.getSinopse())) {
                    dto.setSinopse(valueText);
                }
            }
        }
        
        return dto;
    }
}
