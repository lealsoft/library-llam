package br.tec.llam.biblioteca.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MultaDTO {
    private String id;
    private String leitorId;
    private String emprestimoId;
    private Double valor;
    private LocalDate dataGeracao;
    private LocalDate dataPagamento;
    private String statusMulta;
    private String motivoMulta;
    private String tenantId;
}
