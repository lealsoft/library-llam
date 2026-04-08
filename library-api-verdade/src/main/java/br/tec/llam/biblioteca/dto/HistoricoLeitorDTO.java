package br.tec.llam.biblioteca.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoLeitorDTO {
    private String leitorId;
    private String nomeLeitor;
    private Integer totalEmprestimos;
    private Integer emprestimosAtivos;
    private Integer totalMultas;
    private Double valorMultasPendentes;
    private Integer totalReservas;
    private LocalDate ultimoEmprestimo;
    private List<EmprestimoDTO> emprestimosRecentes;
    private String tenantId;
}
