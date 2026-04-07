# HU-24 – Tela Dashboard da Biblioteca | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como administrador do tenant, quero visualizar um dashboard executivo com métricas consolidadas da biblioteca, para ter visão completa de acervo, empréstimos, receita e leitores.
- Plataforma: React + TailwindCSS + Recharts
- Página inicial do Backoffice com visão gerencial
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)

### KPIs Principais (8 cards)
1. **RN-1 – Total de Títulos:** Exibir total de títulos no acervo (ativos/inativos).
2. **RN-2 – Total de Exemplares:** Total de exemplares por estado (disponível/emprestado/etc).
3. **RN-3 – Empréstimos Ativos:** Total de empréstimos em aberto.
4. **RN-4 – Empréstimos Hoje:** Total de empréstimos do dia com comparativo vs ontem.
5. **RN-5 – Devoluções Pendentes:** Empréstimos com prazo vencido.
6. **RN-6 – Leitores Ativos:** Total de leitores com status ativo.
7. **RN-7 – Receita do Mês:** Receita de empréstimos tarifados + vendas digitais.
8. **RN-8 – Multas Pendentes:** Total de multas não pagas.

### Gráficos e Análises
9. **RN-9 – Gráfico de Empréstimos:** Barras por dia (últimos 7 dias).
10. **RN-10 – Top 5 Títulos:** Ranking dos títulos mais emprestados.
11. **RN-11 – Categorias Populares:** Pizza com distribuição por categoria.
12. **RN-12 – Taxa de Atraso:** Percentual de devoluções atrasadas.

### Alertas
13. **RN-13 – Alertas Críticos:** Devoluções muito atrasadas, multas altas.
14. **RN-14 – Auto-refresh:** Dados atualizados a cada 60 segundos.

---

## Critérios de Aceite (CA)
1. **CA-1 – 8 Cards KPI:** Linha superior com 8 cards de métricas principais.
2. **CA-2 – Gráfico de Barras:** Empréstimos por dia (7 dias).
3. **CA-3 – Top Títulos:** Lista com os 5 mais emprestados.
4. **CA-4 – Gráfico Pizza:** Distribuição por categoria.
5. **CA-5 – Alertas:** Painel de alertas críticos.
6. **CA-6 – Tooltips:** Hover em gráficos exibe valores detalhados.
7. **CA-7 – Loading:** Skeleton durante carregamento.
8. **CA-8 – Auto-refresh:** Dados atualizados automaticamente.

---

## Cenários de Teste (CT)
1. **CT-1 – Carregamento:** Acessar dashboard → skeleton → dados carregados.
2. **CT-2 – KPIs corretos:** Valores dos 8 cards correspondem aos dados reais.
3. **CT-3 – Gráfico interativo:** Hover nas barras → tooltip com valores.
4. **CT-4 – Top títulos:** Lista ordenada por número de empréstimos.
5. **CT-5 – Auto-refresh:** Aguardar 60s → dados atualizados.
6. **CT-6 – Sem dados:** Biblioteca vazia → mensagem "Nenhum dado disponível".
7. **CT-7 – Alertas destacados:** Devoluções atrasadas → alerta vermelho.
8. **CT-8 – Receita correta:** Soma de taxas + vendas digitais do mês.

---

## Observações
- Pré-requisito: HU-18 (Setup Frontend), HU-16 (Relatórios)
- Dashboard executivo para administradores do tenant
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - MVP Frontend
- Estimativa: 4 dias
- Prioridade: Média
