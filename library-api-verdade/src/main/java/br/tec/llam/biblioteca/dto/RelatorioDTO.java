package br.tec.llam.biblioteca.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioDTO {
    private String tipo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String tenantId;
    private Integer totalRegistros;
    private Map<String, Object> resumo;
    private List<Map<String, Object>> dados;
}
