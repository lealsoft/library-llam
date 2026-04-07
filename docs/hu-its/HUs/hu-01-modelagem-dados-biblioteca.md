# HU-01 – Modelagem de Dados LLAM Biblioteca (CERIF) | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como desenvolvedor backend, quero criar os ClassSchemes e Classes específicos do LLAM Biblioteca sobre a estrutura CERIF existente, para mapear títulos, exemplares, leitores, transações e configurações de tenant.
- Plataforma: MySQL + Spring Data JPA
- Reutiliza entidades CERIF existentes (Class, ClassScheme, ClassClass, Person, OrganizationUnit)
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Reutilização CERIF:** Deve usar entidades CERIF existentes (Class, ClassScheme, Person, OrganizationUnit).
2. **RN-2 – ClassSchemes Biblioteca:** Criar ClassSchemes específicos (TITULO_CATEGORIA, EXEMPLAR_ESTADO, EXEMPLAR_LOCALIZACAO, TRANSACAO_TIPO, LEITOR_STATUS, TENANT_CONFIG, MONETIZACAO_MODELO).
3. **RN-3 – Tenant como OrganizationUnit:** Cada biblioteca contratante é uma OrganizationUnit com ClassScheme BIBLIOTECA_TENANT.
4. **RN-4 – Leitor como Person:** Cada leitor é uma Person vinculada ao tenant via PersonOrganizationUnit.
5. **RN-5 – Título como Class:** Cada título bibliográfico é uma Class com ClassScheme TITULO.
6. **RN-6 – Exemplar como Class:** Cada exemplar físico é uma Class vinculada ao título via ClassClass.
7. **RN-7 – Migrations:** Alterações devem ser versionadas via Flyway.
8. **RN-8 – Seed data:** Categorias básicas e estados de exemplar devem ser criados automaticamente.

---

## Critérios de Aceite (CA)
1. **CA-1 – ClassScheme TITULO:** Criado com constant `LIB_TITULO` e uri corretos.
2. **CA-2 – ClassScheme TITULO_CATEGORIA:** Criado com constant `LIB_TITULO_CATEGORIA`.
3. **CA-3 – ClassScheme EXEMPLAR:** Criado com constant `LIB_EXEMPLAR`.
4. **CA-4 – ClassScheme EXEMPLAR_ESTADO:** Criado com constant `LIB_EXEMPLAR_ESTADO`.
5. **CA-5 – ClassScheme TRANSACAO_TIPO:** Criado com constant `LIB_TRANSACAO_TIPO`.
6. **CA-6 – ClassScheme LEITOR_STATUS:** Criado com constant `LIB_LEITOR_STATUS`.
7. **CA-7 – ClassScheme MONETIZACAO:** Criado com constant `LIB_MONETIZACAO_MODELO`.
8. **CA-8 – Estados seedados:** DISPONIVEL, EMPRESTADO, EM_PROCESSAMENTO, CONSERVACAO, BAIXADO criados.
9. **CA-9 – Tipos de transação seedados:** EMPRESTIMO, DEVOLUCAO, RENOVACAO, VENDA_DIGITAL criados.
10. **CA-10 – Migrations aplicadas:** V1 e V2 executadas sem erros.

---

## Cenários de Teste (CT)
1. **CT-1 – ClassSchemes existem:** Query retorna todos os ClassSchemes LIB_*.
2. **CT-2 – Estados existem:** Query retorna estados de exemplar seedados.
3. **CT-3 – Tipos de transação existem:** Query retorna tipos de transação seedados.
4. **CT-4 – Relacionamentos:** ClassClass permite relacionar exemplar com título.
5. **CT-5 – Tenant como OU:** OrganizationUnit pode ser criada como tenant de biblioteca.
6. **CT-6 – Migrations idempotentes:** Executar migrations múltiplas vezes não causa erro.

---

## Observações
- Não cria novas tabelas, apenas usa estrutura CERIF existente
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 3 dias
- Prioridade: Alta
- Pré-requisito para todas as demais HUs do sistema
