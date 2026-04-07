# HU-14 – Autenticação Segura via Keycloak | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como leitor ou bibliotecário, quero me autenticar de forma segura no sistema, para acessar funcionalidades conforme meu perfil e garantir que minhas credenciais estejam protegidas.
- Plataforma: Spring Boot + Keycloak
- Senha nunca armazenada ou trafegada em texto puro (RN-07 DVN)
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Senha segura:** Senha nunca armazenada ou trafegada em texto puro (RN-07 DVN).
2. **RN-2 – Autenticação obrigatória:** Acesso a dados do leitor exige validação de credenciais (seção 8.2 DVN).
3. **RN-3 – Erro sem exposição:** Credenciais incorretas retornam erro sem expor informações do cadastro.
4. **RN-4 – Realm por tenant:** Cada tenant pode ter realm próprio ou compartilhar realm LLAM.
5. **RN-5 – Perfis de acesso:** LEITOR, BIBLIOTECARIO, ADMIN_TENANT, ADMIN_PLATAFORMA.
6. **RN-6 – Sessão configurável:** Tempo de sessão configurável por tenant.
7. **RN-7 – Token JWT:** Autenticação via tokens JWT com refresh token.
8. **RN-8 – Logout seguro:** Logout invalida tokens ativos.

---

## Critérios de Aceite (CA)
1. **CA-1 – Login funcional:** POST /api/auth/login retorna tokens JWT.
2. **CA-2 – Refresh token:** POST /api/auth/refresh renova access token.
3. **CA-3 – Logout:** POST /api/auth/logout invalida sessão.
4. **CA-4 – Credencial inválida:** Login com senha errada → erro 401 genérico.
5. **CA-5 – Perfil no token:** Token JWT contém perfil do usuário.
6. **CA-6 – Tenant no token:** Token JWT contém identificação do tenant.
7. **CA-7 – Endpoint protegido:** Acesso sem token → erro 401.
8. **CA-8 – Token expirado:** Access token expirado → erro 401.

---

## Cenários de Teste (CT)
1. **CT-1 – Login válido:** Credenciais corretas → tokens retornados.
2. **CT-2 – Senha incorreta:** Senha errada → erro 401 sem detalhes.
3. **CT-3 – Usuário inexistente:** Login inexistente → erro 401 sem detalhes.
4. **CT-4 – Refresh token:** Access token expirado → refresh → novo access token.
5. **CT-5 – Logout:** Fazer logout → tentar usar token → erro 401.
6. **CT-6 – Perfil leitor:** Login como leitor → token com role LEITOR.
7. **CT-7 – Perfil bibliotecário:** Login como bibliotecário → token com role BIBLIOTECARIO.
8. **CT-8 – Acesso protegido:** GET /api/leitores/me sem token → erro 401.
9. **CT-9 – Tenant correto:** Login em tenant A → token com tenantId = A.

---

## Observações
- Pré-requisito: HU-02 (Multi-Tenant)
- Autenticação segura conforme seção 8.2 do DVN
- Integração com Keycloak existente da plataforma LLAM
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 3 dias
- Prioridade: Alta
