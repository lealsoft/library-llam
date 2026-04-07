# HU-16 – Relatórios e Auditoria | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como administrador do tenant, quero acessar relatórios gerenciais e logs de auditoria, para acompanhar a operação da biblioteca e responder perguntas de negócio.
- Plataforma: Spring Boot + MySQL + MongoDB
- Rastreabilidade completa conforme seção 4.2 do DVN
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Histórico de exemplar:** Quem esteve com o exemplar nos últimos N meses (seção 1.1 DVN).
2. **RN-2 – Movimentação de título:** Histórico completo de movimentação do título X.
3. **RN-3 – Circulação por período:** Quantas unidades circularam em determinado período.
4. **RN-4 – Receita de empréstimos:** Receita gerada por empréstimos tarifados no período.
5. **RN-5 – Vendas digitais:** Licenças digitais vendidas por categoria.
6. **RN-6 – Auditoria imutável:** Logs de auditoria são imutáveis (RN-10 DVN).
7. **RN-7 – Filtros por período:** Todos os relatórios permitem filtro por data.
8. **RN-8 – Exportação:** Relatórios exportáveis em CSV e PDF.

---

## Critérios de Aceite (CA)
1. **CA-1 – Histórico exemplar:** GET /api/relatorios/exemplar/{id}/historico retorna movimentações.
2. **CA-2 – Movimentação título:** GET /api/relatorios/titulo/{id}/movimentacao retorna histórico.
3. **CA-3 – Circulação:** GET /api/relatorios/circulacao?inicio=X&fim=Y retorna totais.
4. **CA-4 – Receita empréstimos:** GET /api/relatorios/receita/emprestimos retorna valores.
5. **CA-5 – Vendas digitais:** GET /api/relatorios/vendas/digital retorna por categoria.
6. **CA-6 – Auditoria:** GET /api/auditoria lista logs de operações.
7. **CA-7 – Exportar CSV:** GET /api/relatorios/X?formato=csv retorna arquivo CSV.
8. **CA-8 – Exportar PDF:** GET /api/relatorios/X?formato=pdf retorna arquivo PDF.

---

## Cenários de Teste (CT)
1. **CT-1 – Histórico exemplar:** Exemplar com 10 movimentações → lista completa.
2. **CT-2 – Filtro por período:** Circulação março/2026 → apenas dados do período.
3. **CT-3 – Receita mensal:** Receita março = soma de taxas de empréstimo.
4. **CT-4 – Vendas por categoria:** Tecnologia: 50 vendas, Romance: 30 vendas.
5. **CT-5 – Auditoria imutável:** Tentar deletar log → erro 405.
6. **CT-6 – Exportar CSV:** Download CSV → arquivo válido com dados.
7. **CT-7 – Exportar PDF:** Download PDF → documento formatado.
8. **CT-8 – Sem permissão:** Leitor tentar acessar relatórios → erro 403.
9. **CT-9 – Perguntas do DVN:** Todas as perguntas da seção 1.1 respondidas pelos relatórios.

---

## Observações
- Pré-requisito: HU-06 (Modelo Transacional)
- Responde às perguntas de negócio da seção 1.1 do DVN
- Auditoria armazenada no MongoDB para performance
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - Funcionalidades Avançadas
- Estimativa: 5 dias
- Prioridade: Média
