# HU-08 – Devolução de Livros | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como bibliotecário, quero registrar a devolução de livros emprestados, para que os exemplares retornem ao acervo e eventuais multas sejam calculadas.
- Plataforma: Spring Boot + MySQL
- Utiliza o modelo transacional (HU-06) para registrar movimentações
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Verificação de prazo:** Sistema verifica se devolução está no prazo ou atrasada.
2. **RN-2 – Cálculo de multa:** Atrasos geram cálculo automático conforme regras do tenant (RN do DVN 6.2).
3. **RN-3 – Lançamento de devolução:** Débito no leitor, crédito na biblioteca (RN-02 DVN).
4. **RN-4 – Estado em processamento:** Exemplar devolvido vai para EM_PROCESSAMENTO até inspeção.
5. **RN-5 – Devolução múltipla:** Permitir devolver vários exemplares em uma única operação.
6. **RN-6 – Multa como transação:** Multa é registrada como transação financeira vinculada.
7. **RN-7 – Notificação de reserva:** Se exemplar tinha reserva, notificar leitor reservante.

---

## Critérios de Aceite (CA)
1. **CA-1 – Registrar devolução:** POST /api/devolucoes registra devolução com sucesso.
2. **CA-2 – Verificar prazo:** Devolução no prazo → sem multa.
3. **CA-3 – Calcular multa:** Devolução atrasada → multa calculada automaticamente.
4. **CA-4 – Estado atualizado:** Após devolução → exemplar com estado EM_PROCESSAMENTO.
5. **CA-5 – Devolução múltipla:** POST com lista de exemplares → todos devolvidos.
6. **CA-6 – Multa registrada:** Atraso → lançamento financeiro de multa criado.
7. **CA-7 – Reserva notificada:** Exemplar com reserva → leitor reservante notificado.
8. **CA-8 – Histórico atualizado:** Transação de devolução registrada no histórico.

---

## Cenários de Teste (CT)
1. **CT-1 – Devolução no prazo:** Devolver antes do vencimento → sem multa.
2. **CT-2 – Devolução atrasada:** Devolver 3 dias após vencimento → multa de 3 dias.
3. **CT-3 – Cálculo de multa:** Multa R$1/dia × 5 dias = R$5.
4. **CT-4 – Estado em processamento:** Após devolução → estado = EM_PROCESSAMENTO.
5. **CT-5 – Devolução múltipla:** Devolver 3 livros → 3 transações registradas.
6. **CT-6 – Exemplar não emprestado:** Tentar devolver exemplar disponível → erro 409.
7. **CT-7 – Exemplar de outro leitor:** Tentar devolver exemplar de outro leitor → erro 403.
8. **CT-8 – Reserva ativa:** Devolver exemplar reservado → email enviado ao reservante.
9. **CT-9 – Multa pendente:** Multa gerada → aparece em pendências do leitor.

---

## Observações
- Pré-requisito: HU-07 (Empréstimo de Livros)
- Fluxo de devolução do Módulo 2 do DVN
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 3 dias
- Prioridade: Alta
