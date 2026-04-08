package br.tec.llam.biblioteca.service;

import br.tec.llam.biblioteca.dto.NotificacaoDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificacaoService {

    public NotificacaoDTO enviarNotificacaoVencimento(String leitorId, String emprestimoId, String tenantId) {
        return NotificacaoDTO.builder()
            .id(UUID.randomUUID().toString())
            .leitorId(leitorId)
            .tipo("VENCIMENTO")
            .titulo("Empréstimo próximo do vencimento")
            .mensagem("Seu empréstimo " + emprestimoId + " está próximo do vencimento.")
            .dataEnvio(LocalDateTime.now())
            .lida(false)
            .tenantId(tenantId)
            .build();
    }

    public NotificacaoDTO enviarNotificacaoReservaDisponivel(String leitorId, String tituloId, String tenantId) {
        return NotificacaoDTO.builder()
            .id(UUID.randomUUID().toString())
            .leitorId(leitorId)
            .tipo("RESERVA_DISPONIVEL")
            .titulo("Reserva disponível para retirada")
            .mensagem("O título " + tituloId + " está disponível para retirada.")
            .dataEnvio(LocalDateTime.now())
            .lida(false)
            .tenantId(tenantId)
            .build();
    }

    public NotificacaoDTO enviarNotificacaoMulta(String leitorId, Double valor, String tenantId) {
        return NotificacaoDTO.builder()
            .id(UUID.randomUUID().toString())
            .leitorId(leitorId)
            .tipo("MULTA")
            .titulo("Multa gerada")
            .mensagem("Uma multa de R$ " + String.format("%.2f", valor) + " foi gerada.")
            .dataEnvio(LocalDateTime.now())
            .lida(false)
            .tenantId(tenantId)
            .build();
    }
}
