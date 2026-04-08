package br.tec.llam.biblioteca.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracaoTenantDTO {
    private String tenantId;
    private Integer prazoEmprestimoDias;
    private Integer limiteEmprestimosPorLeitor;
    private Integer limiteRenovacoes;
    private Double valorMultaPorDia;
    private Integer prazoRetiradaReservaDias;
    private Boolean permitirEmprestimoComPendencia;
    private Boolean permitirRenovacaoComReserva;
    private String corPrimaria;
    private String corSecundaria;
    private String logoUrl;
    private String dominio;
}
