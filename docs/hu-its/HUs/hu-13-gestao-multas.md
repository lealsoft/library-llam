# HU-13 – Gestão de Multas e Pendências Financeiras | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como bibliotecário, quero gerenciar multas por atraso e pendências financeiras dos leitores, para controlar a regularização e manter a saúde financeira da biblioteca.
- Plataforma: Spring Boot + MySQL
- Multas são transações financeiras vinculadas a devoluções atrasadas
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Cálculo automático:** Multa calculada automaticamente na devolução atrasada.
2. **RN-2 – Valor configurável:** Valor da multa por dia configurável pelo tenant.
3. **RN-3 – Bloqueio por pendência:** Leitor com pendência não pode emprestar nem comprar (RN-09 DVN).
4. **RN-4 – Pagamento registrado:** Pagamento de multa é transação financeira.
5. **RN-5 – Histórico preservado:** Multas pagas permanecem no histórico.
6. **RN-6 – Isenção autorizada:** Administrador pode isentar multa com justificativa.
7. **RN-7 – Notificação:** Leitor notificado sobre multas pendentes.

---

## Critérios de Aceite (CA)
1. **CA-1 – Multa gerada:** Devolução atrasada → multa criada automaticamente.
2. **CA-2 – Listar pendências:** GET /api/leitores/{id}/pendencias lista multas pendentes.
3. **CA-3 – Pagar multa:** POST /api/multas/{id}/pagar registra pagamento.
4. **CA-4 – Isentar multa:** POST /api/multas/{id}/isentar registra isenção com justificativa.
5. **CA-5 – Bloqueio funcional:** Leitor com multa → erro 403 ao tentar emprestar.
6. **CA-6 – Histórico completo:** GET /api/leitores/{id}/multas lista todas (pagas e pendentes).
7. **CA-7 – Configurar valor:** PUT /api/tenant/config/multa atualiza valor por dia.
8. **CA-8 – Relatório de multas:** GET /api/relatorios/multas retorna resumo financeiro.

---

## Cenários de Teste (CT)
1. **CT-1 – Multa calculada:** Atraso 5 dias × R$1/dia = R$5 de multa.
2. **CT-2 – Bloqueio ativo:** Leitor com multa R$10 → tentar emprestar → erro 403.
3. **CT-3 – Pagar multa:** Pagar R$10 → pendência zerada → empréstimo liberado.
4. **CT-4 – Isenção:** Isentar multa com justificativa → multa marcada como isenta.
5. **CT-5 – Histórico:** Leitor com 3 multas pagas + 1 pendente → histórico mostra todas.
6. **CT-6 – Configurar valor:** Alterar para R$2/dia → novas multas usam novo valor.
7. **CT-7 – Notificação:** Multa gerada → email enviado ao leitor.
8. **CT-8 – Relatório mensal:** GET /api/relatorios/multas?mes=03 → total arrecadado.
9. **CT-9 – Múltiplas multas:** Leitor com 3 multas → soma total exibida.

---

## Observações
- Pré-requisito: HU-08 (Devolução de Livros)
- Política de multas conforme seção 3.3 do DVN
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 3 dias
- Prioridade: Alta
