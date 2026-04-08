package br.tec.llam.biblioteca.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacaoDTO {
    private String id;
    private String leitorId;
    private String tipo;
    private String titulo;
    private String mensagem;
    private LocalDateTime dataEnvio;
    private Boolean lida;
    private String tenantId;
}
