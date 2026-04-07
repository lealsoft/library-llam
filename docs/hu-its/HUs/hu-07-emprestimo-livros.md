# HU-07 – Empréstimo de Livros Físicos | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como bibliotecário ou leitor, quero realizar empréstimos de livros físicos, para que o leitor possa levar exemplares para leitura com prazo definido.
- Plataforma: Spring Boot + MySQL
- Utiliza o modelo transacional (HU-06) para registrar movimentações
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Verificação de disponibilidade:** Sistema verifica se exemplar está disponível.
2. **RN-2 – Verificação do leitor:** Sistema verifica situação cadastral do leitor (pendências, limite).
3. **RN-3 – Leitor em atraso:** Leitor com empréstimos em atraso não pode realizar novos (RN-08 DVN).
4. **RN-4 – Pendência financeira:** Leitor com pendência financeira não pode emprestar (RN-09 DVN).
5. **RN-5 – Limite de empréstimos:** Respeitar limite configurado pelo tenant.
6. **RN-6 – Prazo configurável:** Prazo de devolução conforme configuração do tenant/categoria.
7. **RN-7 – Empréstimo tarifado:** Se tenant usa modelo de taxa, gerar lançamento financeiro.
8. **RN-8 – Empréstimo múltiplo:** Permitir emprestar vários exemplares em uma única operação.
9. **RN-9 – Registro transacional:** Empréstimo registrado via modelo de dupla entrada.

---

## Critérios de Aceite (CA)
1. **CA-1 – Realizar empréstimo:** POST /api/emprestimos cria empréstimo com sucesso.
2. **CA-2 – Verificar disponibilidade:** Exemplar indisponível → erro 409.
3. **CA-3 – Verificar leitor:** Leitor suspenso → erro 403.
4. **CA-4 – Verificar atraso:** Leitor com atraso → erro 403 com mensagem específica.
5. **CA-5 – Verificar limite:** Leitor no limite → erro 403.
6. **CA-6 – Prazo calculado:** Empréstimo criado com data de devolução correta.
7. **CA-7 – Empréstimo múltiplo:** POST com lista de exemplares → todos emprestados atomicamente.
8. **CA-8 – Taxa registrada:** Empréstimo tarifado → lançamento financeiro criado.
9. **CA-9 – Estado atualizado:** Após empréstimo → exemplar com estado EMPRESTADO.

---

## Cenários de Teste (CT)
1. **CT-1 – Empréstimo válido:** Leitor ativo + exemplar disponível → empréstimo realizado.
2. **CT-2 – Exemplar indisponível:** Tentar emprestar exemplar emprestado → erro 409.
3. **CT-3 – Leitor suspenso:** Leitor com status SUSPENSO → erro 403.
4. **CT-4 – Leitor em atraso:** Leitor com devolução atrasada → erro 403.
5. **CT-5 – Limite atingido:** Leitor com 5 livros (limite 5) → erro 403.
6. **CT-6 – Prazo por categoria:** Livro de referência → prazo 7 dias; romance → prazo 14 dias.
7. **CT-7 – Empréstimo múltiplo:** Emprestar 3 livros → 3 transações atômicas.
8. **CT-8 – Falha parcial:** 2 disponíveis + 1 indisponível → nenhum emprestado.
9. **CT-9 – Taxa cobrada:** Tenant com taxa R$2 → lançamento de R$2 criado.
10. **CT-10 – Listar empréstimos:** GET /api/emprestimos/ativos → lista empréstimos em aberto.

---

## Observações
- Pré-requisito: HU-06 (Modelo Transacional)
- Fluxo principal do Módulo 2 do DVN
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 4 dias
- Prioridade: Alta
