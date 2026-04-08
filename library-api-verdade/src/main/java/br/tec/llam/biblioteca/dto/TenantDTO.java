package br.tec.llam.biblioteca.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantDTO {
    private String id;
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    private String cnpj;
    private String dominio;
    private String email;
    private String telefone;
    private String endereco;
    private String planoAssinatura;
    private Integer prazoEmprestimoDias;
    private Integer limiteEmprestimosPorLeitor;
    private Double valorMultaDia;
    private Boolean ativo;
}
