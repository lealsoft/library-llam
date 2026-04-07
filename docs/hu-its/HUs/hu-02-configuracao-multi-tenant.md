# HU-02 – Configuração Multi-Tenant White Label | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como administrador da plataforma LLAM, quero configurar bibliotecas contratantes como tenants isolados, para que cada uma opere com sua própria identidade visual, acervo e regras de negócio.
- Plataforma: Spring Boot + MySQL + Keycloak
- Cada tenant é uma OrganizationUnit com configurações específicas
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Isolamento de dados:** Dados de um tenant não são visíveis por outro tenant.
2. **RN-2 – Identidade visual:** Cada tenant configura logotipo, paleta de cores e domínio próprio.
3. **RN-3 – Configurações independentes:** Cada tenant define suas próprias regras (prazos, limites, multas).
4. **RN-4 – Modelos de monetização:** Cada tenant ativa os modelos desejados (gratuito, taxa, assinatura, venda digital).
5. **RN-5 – Domínio próprio:** Tenant acessado via domínio customizado (ex: biblioteca.instituicao.edu.br).
6. **RN-6 – Tenant raiz:** Instituto LLAM é o tenant fundador e ambiente de validação.
7. **RN-7 – Hierarquia:** Tenant é OrganizationUnit filho de "LLAM Biblioteca" (produto).

---

## Critérios de Aceite (CA)
1. **CA-1 – Criar tenant:** API permite criar novo tenant com dados básicos.
2. **CA-2 – Configurar identidade:** Tenant pode definir logo, cores e domínio.
3. **CA-3 – Configurar regras:** Tenant pode definir prazos, limites e multas.
4. **CA-4 – Ativar monetização:** Tenant pode ativar/desativar modelos de monetização.
5. **CA-5 – Isolamento funcional:** Leitor de tenant A não vê acervo de tenant B.
6. **CA-6 – Instituto LLAM criado:** Tenant fundador existe e está ativo.
7. **CA-7 – Domínio resolvido:** Acesso via domínio customizado redireciona para tenant correto.

---

## Cenários de Teste (CT)
1. **CT-1 – Criar tenant:** POST /api/tenants → tenant criado com sucesso.
2. **CT-2 – Configurar logo:** PUT /api/tenants/{id}/branding → logo atualizado.
3. **CT-3 – Configurar regras:** PUT /api/tenants/{id}/config → regras salvas.
4. **CT-4 – Isolamento:** GET /api/acervo (tenant A) → não retorna livros de tenant B.
5. **CT-5 – Domínio inválido:** Acessar domínio não configurado → erro 404.
6. **CT-6 – Tenant inativo:** Acessar tenant desativado → erro 403.
7. **CT-7 – Listar tenants:** GET /api/tenants (admin LLAM) → lista todos os tenants.

---

## Observações
- Pré-requisito: HU-01 (Modelagem de Dados)
- Instituto LLAM é criado automaticamente via seed data
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 4 dias
- Prioridade: Alta
