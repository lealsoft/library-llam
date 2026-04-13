package com.capgemini.biblioapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String login;
    private LocalDateTime dataCriacao;
    private List<EmprestimoResponseDTO> emprestimosEmAndamento;
    private List<EmprestimoResponseDTO> emprestimosFinalizados;
}
