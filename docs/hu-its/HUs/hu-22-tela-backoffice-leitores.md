# HU-22 – Tela Backoffice de Gestão de Leitores | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como bibliotecário ou administrador, quero gerenciar o cadastro de leitores da biblioteca, para manter os dados atualizados e controlar o acesso ao sistema.
- Plataforma: React + TailwindCSS + shadcn/ui
- Interface administrativa para gestão do Módulo 4 (Gestão de Usuários)
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – CRUD de leitores:** Criar, editar, visualizar e alterar status de leitores.
2. **RN-2 – Busca avançada:** Buscar por nome, login, email ou vínculo.
3. **RN-3 – Vincular plano:** Associar leitor a plano de assinatura.
4. **RN-4 – Histórico completo:** Visualizar histórico de empréstimos e compras.
5. **RN-5 – Pendências:** Visualizar e gerenciar multas pendentes.
6. **RN-6 – Suspensão:** Suspender ou bloquear leitor com justificativa.
7. **RN-7 – Reset de senha:** Enviar link de reset de senha para o leitor.
8. **RN-8 – Importação:** Importar leitores via arquivo CSV.

---

## Critérios de Aceite (CA)
1. **CA-1 – Listar leitores:** Tabela com leitores, busca e paginação.
2. **CA-2 – Criar leitor:** Formulário cria leitor com validação.
3. **CA-3 – Editar leitor:** Formulário edita dados do leitor.
4. **CA-4 – Alterar status:** Dropdown permite alterar status (ativo/suspenso/bloqueado).
5. **CA-5 – Vincular plano:** Dropdown de planos disponíveis funciona.
6. **CA-6 – Ver histórico:** Aba "Histórico" mostra empréstimos e compras.
7. **CA-7 – Ver pendências:** Aba "Pendências" mostra multas com opção de isentar.
8. **CA-8 – Reset senha:** Botão envia email de reset.

---

## Cenários de Teste (CT)
1. **CT-1 – Listar leitores:** Acessar tela → leitores listados com paginação.
2. **CT-2 – Buscar leitor:** Digitar email → leitor encontrado.
3. **CT-3 – Criar leitor:** Preencher formulário → leitor criado + usuário Keycloak.
4. **CT-4 – Login duplicado:** Criar com login existente → erro exibido.
5. **CT-5 – Editar leitor:** Alterar telefone → dados atualizados.
6. **CT-6 – Suspender leitor:** Alterar para SUSPENSO → leitor não pode emprestar.
7. **CT-7 – Vincular plano:** Selecionar "Premium" → plano vinculado.
8. **CT-8 – Ver histórico:** Clicar aba → 20 empréstimos listados.
9. **CT-9 – Isentar multa:** Clicar "Isentar" → multa marcada como isenta.
10. **CT-10 – Reset senha:** Clicar "Reset" → email enviado.
11. **CT-11 – Importar CSV:** Upload de 50 leitores → todos importados.

---

## Observações
- Pré-requisito: HU-18 (Setup Frontend), HU-05 (Cadastro de Leitores)
- Interface administrativa do Módulo 4 do DVN
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - MVP Frontend
- Estimativa: 4 dias
- Prioridade: Alta
