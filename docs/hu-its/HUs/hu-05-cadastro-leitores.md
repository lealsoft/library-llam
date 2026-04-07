# HU-05 – Cadastro de Leitores | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como bibliotecário, quero cadastrar e gerenciar leitores da biblioteca, para controlar quem pode realizar empréstimos e compras digitais.
- Plataforma: Spring Boot + MySQL + Keycloak
- Leitor é uma Person vinculada ao tenant via PersonOrganizationUnit
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Campos obrigatórios:** Nome completo, login único, email.
2. **RN-2 – Senha segura:** Senha nunca armazenada ou trafegada em texto puro (RN-07 DVN).
3. **RN-3 – Status do leitor:** ATIVO, SUSPENSO, BLOQUEADO.
4. **RN-4 – Vínculo institucional:** Leitor pode ter vínculo (aluno, professor, funcionário, externo).
5. **RN-5 – Login único por tenant:** Mesmo login não pode existir duas vezes no tenant.
6. **RN-6 – Isolamento por tenant:** Leitor pertence exclusivamente ao tenant que o cadastrou.
7. **RN-7 – Plano de assinatura:** Leitor pode estar vinculado a um plano (quando tenant usa modelo assinatura).
8. **RN-8 – Autenticação via Keycloak:** Credenciais gerenciadas pelo Keycloak.

---

## Critérios de Aceite (CA)
1. **CA-1 – Criar leitor:** POST /api/leitores cria leitor com sucesso.
2. **CA-2 – Criar usuário Keycloak:** Leitor criado → usuário correspondente no Keycloak.
3. **CA-3 – Listar leitores:** GET /api/leitores retorna leitores do tenant com paginação.
4. **CA-4 – Buscar leitor:** GET /api/leitores?q=termo busca por nome, login ou email.
5. **CA-5 – Editar leitor:** PUT /api/leitores/{id} atualiza dados do leitor.
6. **CA-6 – Alterar status:** PATCH /api/leitores/{id}/status altera status do leitor.
7. **CA-7 – Login único:** Leitor com login duplicado no tenant → erro 409.
8. **CA-8 – Vincular plano:** POST /api/leitores/{id}/plano vincula leitor a plano de assinatura.

---

## Cenários de Teste (CT)
1. **CT-1 – Criar leitor válido:** Preencher campos obrigatórios → leitor criado.
2. **CT-2 – Criar sem email:** Omitir email → erro de validação.
3. **CT-3 – Login duplicado:** Cadastrar login existente → erro 409.
4. **CT-4 – Buscar por nome:** GET /api/leitores?q=Silva → retorna leitores com "Silva".
5. **CT-5 – Suspender leitor:** Alterar status para SUSPENSO → leitor não pode emprestar.
6. **CT-6 – Bloquear leitor:** Alterar status para BLOQUEADO → leitor não pode acessar.
7. **CT-7 – Vincular plano:** Vincular plano Premium → limites do plano aplicados.
8. **CT-8 – Isolamento:** Leitor de tenant A não aparece em busca de tenant B.
9. **CT-9 – Autenticação:** Leitor faz login via Keycloak → acesso liberado.

---

## Observações
- Pré-requisito: HU-01 (Modelagem), HU-02 (Multi-Tenant)
- Integração com Keycloak para autenticação
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 4 dias
- Prioridade: Alta
