# HU-09 – Renovação e Reserva de Livros | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como leitor, quero renovar empréstimos ativos e reservar livros emprestados, para estender meu prazo de leitura ou garantir acesso a títulos desejados.
- Plataforma: Spring Boot + MySQL
- Utiliza o modelo transacional (HU-06) para registrar movimentações
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)

### Renovação
1. **RN-1 – Limite de renovações:** Número máximo de renovações configurável por tenant.
2. **RN-2 – Sem reserva pendente:** Não permitir renovação se exemplar tem reserva ativa.
3. **RN-3 – Sem atraso:** Não permitir renovação se empréstimo está atrasado.
4. **RN-4 – Novo prazo:** Renovação estende prazo a partir da data atual.
5. **RN-5 – Registro transacional:** Renovação é registrada como transação tipo RENOVACAO.

### Reserva
6. **RN-6 – Reserva de emprestado:** Apenas exemplares emprestados podem ser reservados.
7. **RN-7 – Fila de reserva:** Múltiplas reservas formam fila por ordem de solicitação.
8. **RN-8 – Prazo de retirada:** Reserva tem prazo configurável para retirada após devolução.
9. **RN-9 – Bloqueio automático:** Exemplar devolvido com reserva fica bloqueado para o reservante.
10. **RN-10 – Expiração:** Reserva não retirada no prazo é cancelada automaticamente.

---

## Critérios de Aceite (CA)

### Renovação
1. **CA-1 – Renovar empréstimo:** POST /api/emprestimos/{id}/renovar renova com sucesso.
2. **CA-2 – Limite respeitado:** Tentar renovar além do limite → erro 403.
3. **CA-3 – Reserva bloqueia:** Tentar renovar com reserva ativa → erro 409.
4. **CA-4 – Atraso bloqueia:** Tentar renovar empréstimo atrasado → erro 403.
5. **CA-5 – Novo prazo:** Renovação → nova data de devolução calculada.

### Reserva
6. **CA-6 – Criar reserva:** POST /api/reservas cria reserva com sucesso.
7. **CA-7 – Fila ordenada:** GET /api/reservas/exemplar/{id} retorna fila ordenada.
8. **CA-8 – Notificação:** Devolução de exemplar reservado → notificação enviada.
9. **CA-9 – Bloqueio:** Exemplar devolvido com reserva → estado RESERVADO.
10. **CA-10 – Expiração:** Reserva não retirada → cancelada automaticamente.

---

## Cenários de Teste (CT)

### Renovação
1. **CT-1 – Renovação válida:** Empréstimo ativo + sem reserva + dentro do limite → renovado.
2. **CT-2 – Limite atingido:** 3 renovações (limite 3) → erro 403.
3. **CT-3 – Com reserva:** Exemplar com reserva → erro 409.
4. **CT-4 – Atrasado:** Empréstimo vencido → erro 403.
5. **CT-5 – Novo prazo:** Renovar em 10/03 com prazo 14 dias → vencimento 24/03.

### Reserva
6. **CT-6 – Reservar emprestado:** Exemplar emprestado → reserva criada.
7. **CT-7 – Reservar disponível:** Exemplar disponível → erro 409 (não precisa reservar).
8. **CT-8 – Fila de reservas:** 3 reservas → fila ordenada por data.
9. **CT-9 – Notificação:** Devolução → email para primeiro da fila.
10. **CT-10 – Expiração:** Reserva com 3 dias → não retirada → cancelada.
11. **CT-11 – Cancelar reserva:** DELETE /api/reservas/{id} → reserva cancelada.

---

## Observações
- Pré-requisito: HU-07 (Empréstimo), HU-08 (Devolução)
- Fluxo de renovação e reserva do Módulo 2 do DVN (seção 6.3)
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 3 dias
- Prioridade: Média
