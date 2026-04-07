# HU-15 – Biblioteca Pessoal do Leitor | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como leitor, quero acessar minha biblioteca pessoal com títulos digitais adquiridos e histórico de empréstimos, para ter visão completa da minha relação com a biblioteca.
- Plataforma: Spring Boot + MySQL
- Área "Minha Biblioteca" conforme seção 7.3 do DVN
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Títulos digitais:** Exibir títulos digitais adquiridos com acesso permanente.
2. **RN-2 – Histórico de empréstimos:** Exibir histórico completo de empréstimos físicos.
3. **RN-3 – Empréstimos ativos:** Destacar empréstimos em aberto com prazo de devolução.
4. **RN-4 – Saldo de empréstimos:** Exibir saldo disponível conforme plano ativo.
5. **RN-5 – Reservas ativas:** Listar reservas pendentes do leitor.
6. **RN-6 – Pendências:** Exibir multas e pendências financeiras.
7. **RN-7 – Download digital:** Permitir download de títulos adquiridos.
8. **RN-8 – Autenticação obrigatória:** Acesso exige login válido.

---

## Critérios de Aceite (CA)
1. **CA-1 – Títulos digitais:** GET /api/leitores/me/biblioteca/digital lista títulos adquiridos.
2. **CA-2 – Histórico:** GET /api/leitores/me/historico lista empréstimos passados.
3. **CA-3 – Empréstimos ativos:** GET /api/leitores/me/emprestimos lista empréstimos em aberto.
4. **CA-4 – Saldo:** GET /api/leitores/me/saldo retorna saldo de empréstimos disponível.
5. **CA-5 – Reservas:** GET /api/leitores/me/reservas lista reservas ativas.
6. **CA-6 – Pendências:** GET /api/leitores/me/pendencias lista multas pendentes.
7. **CA-7 – Download:** GET /api/leitores/me/downloads/{tituloId} retorna URL de download.
8. **CA-8 – Dashboard:** GET /api/leitores/me/dashboard retorna resumo consolidado.

---

## Cenários de Teste (CT)
1. **CT-1 – Títulos digitais:** Leitor com 3 títulos comprados → lista 3 títulos.
2. **CT-2 – Histórico completo:** Leitor com 20 empréstimos → histórico paginado.
3. **CT-3 – Empréstimos ativos:** 2 livros emprestados → lista com prazos.
4. **CT-4 – Saldo correto:** Plano com 5 livros, 2 emprestados → saldo = 3.
5. **CT-5 – Reserva ativa:** 1 reserva pendente → aparece na lista.
6. **CT-6 – Multa pendente:** Multa R$5 → aparece em pendências.
7. **CT-7 – Download funcional:** Clicar download → PDF baixado.
8. **CT-8 – Sem autenticação:** GET /api/leitores/me sem token → erro 401.
9. **CT-9 – Dashboard completo:** Resumo com totais de cada seção.

---

## Observações
- Pré-requisito: HU-14 (Autenticação), HU-10 (Venda Digital)
- Área "Minha Biblioteca" conforme seção 7.3 do DVN
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - MVP Frontend
- Estimativa: 3 dias
- Prioridade: Média
