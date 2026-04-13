package com.capgemini.biblioapi.dto;

import com.capgemini.biblioapi.entity.StatusLivro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivroResponseDTO {

    private Long id;
    private String titulo;
    private String autor;
    private String isbn;
    private StatusLivro status;
    private LocalDateTime dataCadastro;
    private Integer totalEmprestimos;
}
