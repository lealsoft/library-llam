# HU-06 – Modelo Transacional de Ativos Bibliográficos | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como sistema, quero registrar todas as movimentações de exemplares como transações de dupla entrada, para garantir rastreabilidade completa, auditoria nativa e consistência de saldos.
- Core do sistema — todas as operações de empréstimo, devolução e venda passam por este módulo
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Dupla entrada:** Todo empréstimo gera débito na biblioteca e crédito no leitor (RN-01 DVN).
2. **RN-2 – Devolução inversa:** Toda devolução gera débito no leitor e crédito na biblioteca (RN-02 DVN).
3. **RN-3 – Lote atômico:** Empréstimo de múltiplos exemplares é atômico — tudo ou nada (RN-03 DVN).
4. **RN-4 – Saldo calculado:** Saldo disponível é sempre calculado do histórico, não editável (RN-04 DVN).
5. **RN-5 – Transação financeira integrada:** Empréstimo tarifado inclui lançamento financeiro na mesma transação (RN-05 DVN).
6. **RN-6 – Imutabilidade:** Histórico de transações é imutável — registros não podem ser deletados (RN-10 DVN).
7. **RN-7 – Tipos de transação:** EMPRESTIMO, DEVOLUCAO, RENOVACAO, VENDA_DIGITAL, MULTA, RESERVA.
8. **RN-8 – Conta corrente:** Biblioteca e cada leitor possuem conta corrente de ativos.

---

## Critérios de Aceite (CA)
1. **CA-1 – Registrar transação:** POST /api/transacoes registra transação com débito e crédito.
2. **CA-2 – Lote atômico:** Transação com múltiplos exemplares → todos confirmados ou nenhum.
3. **CA-3 – Saldo biblioteca:** GET /api/biblioteca/saldo retorna saldo calculado de exemplares.
4. **CA-4 – Saldo leitor:** GET /api/leitores/{id}/saldo retorna exemplares em posse do leitor.
5. **CA-5 – Histórico exemplar:** GET /api/exemplares/{id}/historico retorna todas as transações.
6. **CA-6 – Histórico leitor:** GET /api/leitores/{id}/historico retorna todas as transações.
7. **CA-7 – Imutabilidade:** DELETE /api/transacoes/{id} → erro 405 (não permitido).
8. **CA-8 – Transação financeira:** Empréstimo tarifado → lançamento financeiro criado junto.

---

## Cenários de Teste (CT)
1. **CT-1 – Empréstimo simples:** Emprestar 1 exemplar → débito biblioteca, crédito leitor.
2. **CT-2 – Empréstimo múltiplo:** Emprestar 3 exemplares → 3 pares de lançamentos atômicos.
3. **CT-3 – Devolução:** Devolver exemplar → débito leitor, crédito biblioteca.
4. **CT-4 – Saldo correto:** Após empréstimo → saldo biblioteca diminui, saldo leitor aumenta.
5. **CT-5 – Falha atômica:** Erro no 2º exemplar → nenhum empréstimo registrado.
6. **CT-6 – Histórico completo:** Exemplar com 5 movimentações → histórico retorna todas.
7. **CT-7 – Tentativa de delete:** DELETE transação → erro 405.
8. **CT-8 – Empréstimo tarifado:** Emprestar com taxa → transação bibliográfica + financeira.
9. **CT-9 – Consistência:** Soma de saldos (biblioteca + leitores) = total de exemplares ativos.

---

## Observações
- Este é o módulo mais crítico do sistema — base para todas as operações
- Pré-requisito: HU-04 (Gestão de Exemplares), HU-05 (Cadastro de Leitores)
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 5 dias
- Prioridade: Crítica
