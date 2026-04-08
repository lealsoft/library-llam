package br.tec.llam.biblioteca.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeitorDTO {
    private String id;
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    @Email(message = "Email inválido")
    private String email;
    private String telefone;
    private String cpf;
    private String documento;
    private String endereco;
    private LocalDate dataNascimento;
    private String statusLeitor;
    private String keycloakId;
    private String tenantId;
    private Integer limiteEmprestimos;
    private Integer emprestimosAtivos;
}
