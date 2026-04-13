package com.capgemini.biblioapi.dto;

import com.capgemini.biblioapi.entity.StatusEmprestimo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoResponseDTO {

    private Long id;
    private LivroResponseDTO livro;
    private LocalDateTime dataEmprestimo;
    private LocalDateTime dataDevolucao;
    private StatusEmprestimo status;
}
