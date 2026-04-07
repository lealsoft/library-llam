# HU-11 – Planos de Assinatura do Leitor | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como administrador do tenant, quero configurar planos de assinatura para leitores, para oferecer modelos de monetização recorrente com benefícios diferenciados.
- Plataforma: Spring Boot + MySQL
- Modelo de monetização por mensalidade (seção 2.3 do DVN)
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Planos configuráveis:** Cada tenant define seus próprios planos (básico, padrão, premium).
2. **RN-2 – Limite de empréstimos:** Cada plano define quantidade máxima de empréstimos simultâneos.
3. **RN-3 – Volume mensal:** Cada plano pode definir volume máximo de empréstimos por mês.
4. **RN-4 – Prazo diferenciado:** Planos podem ter prazos de devolução diferenciados.
5. **RN-5 – Renovações:** Planos podem ter número diferente de renovações permitidas.
6. **RN-6 – Benefícios digitais:** Planos podem incluir descontos em compras digitais.
7. **RN-7 – Vigência:** Assinatura tem data de início e fim, renovável.
8. **RN-8 – Suspensão automática:** Assinatura vencida suspende benefícios automaticamente.

---

## Critérios de Aceite (CA)
1. **CA-1 – Criar plano:** POST /api/planos cria plano com sucesso.
2. **CA-2 – Listar planos:** GET /api/planos lista planos do tenant.
3. **CA-3 – Editar plano:** PUT /api/planos/{id} atualiza configurações.
4. **CA-4 – Vincular leitor:** POST /api/leitores/{id}/assinatura vincula leitor ao plano.
5. **CA-5 – Verificar vigência:** Sistema verifica se assinatura está ativa.
6. **CA-6 – Aplicar limites:** Empréstimo respeita limites do plano do leitor.
7. **CA-7 – Suspensão automática:** Assinatura vencida → benefícios suspensos.
8. **CA-8 – Renovar assinatura:** POST /api/assinaturas/{id}/renovar estende vigência.

---

## Cenários de Teste (CT)
1. **CT-1 – Criar plano básico:** Criar plano com 3 livros simultâneos → plano criado.
2. **CT-2 – Criar plano premium:** Criar plano com 10 livros + 20% desconto digital → plano criado.
3. **CT-3 – Vincular leitor:** Vincular leitor ao plano premium → benefícios aplicados.
4. **CT-4 – Limite respeitado:** Leitor plano básico (3 livros) → tentar 4º → erro 403.
5. **CT-5 – Prazo diferenciado:** Plano premium 21 dias → empréstimo com prazo 21 dias.
6. **CT-6 – Assinatura vencida:** Assinatura expirada → leitor volta ao plano gratuito.
7. **CT-7 – Renovar assinatura:** Renovar por mais 30 dias → nova data de vigência.
8. **CT-8 – Desconto digital:** Compra digital com plano premium → desconto aplicado.
9. **CT-9 – Listar assinaturas:** GET /api/assinaturas → lista assinaturas ativas.

---

## Observações
- Pré-requisito: HU-05 (Cadastro de Leitores), HU-02 (Multi-Tenant)
- Modelo de monetização por mensalidade (seção 2.3 do DVN)
- Integração com gateway de pagamento na Fase 3
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - Funcionalidades Avançadas
- Estimativa: 4 dias
- Prioridade: Média
