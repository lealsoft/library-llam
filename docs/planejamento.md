# LLAM Biblioteca - Sistema de Gestão de Bibliotecas Multi-Tenant

**Versão:** 1.7  
**Data de Atualização:** 31 de Março de 2026  
**Objetivo:** Controle centralizado de implementação do Sistema de Gestão de Bibliotecas LLAM

---

## 📁 Estrutura de Documentação

```
Library-LLAM/docs/
├── README.md              # Este arquivo (Planilha de Implementação)
├── dvn.md                 # Documento de Visão Negocial
├── hu-its/
│   ├── README.md          # Visão geral das HUs
│   ├── HUs/               # Histórias de Usuário (24 HUs)
│   │   ├── hu-01-*.md     # Fase 1 - MVP Backend (HU-01 a HU-09)
│   │   ├── hu-10-*.md     # Fase 2 - Funcionalidades Avançadas (HU-10 a HU-16)
│   │   └── hu-17-*.md     # Fase 3 - Frontend (HU-17 a HU-24)
│   └── ITSs/              # Instruções Técnicas de Serviço (8 ITSs)
│       └── ITS-HU-*.md    # Detalhes técnicos por HU
```

---

## 👥 Personas

| Persona | Descrição |
|---------|-----------|
| **Administrador LLAM** | Configura tenants, planos, infraestrutura |
| **Administrador Biblioteca** | Gerencia acervo, leitores, configurações do tenant |
| **Bibliotecário** | Realiza empréstimos, devoluções, cadastros |
| **Leitor** | Consulta catálogo, realiza empréstimos, compras digitais |
| **Desenvolvedor Backend** | Implementa APIs e serviços |
| **Desenvolvedor Frontend** | Implementa interfaces React |

---

## 🎯 MVP - REQUISITOS DO CLIENTE

> **IMPORTANTE:** Esta seção destaca o escopo mínimo solicitado pelo cliente para demonstração técnica.
> A implementação utiliza o modelo **CERIF 1.6** e APIs REST do ecossistema (sem H2), agregando valor arquitetônico.

### Entidades do MVP

| Entidade Cliente | Mapeamento CERIF | Backend Ecos | Status |
|------------------|------------------|--------------|:------:|
| **👤 Usuário** | Person | `person-backend` | ✅ |
| **📖 Livro** | DublinCore | `dublin-core-backend` | ✅ |
| **🔄 Empréstimo** | Transaction | `transaction-backend` | 🔄 |

### Requisitos Funcionais MVP

#### 👤 CRUD de Usuário (Leitor)

| Requisito | Endpoint | Impl. | Testado | Observações |
|-----------|----------|:-----:|:-------:|-------------|
| ✅ Criar usuário | `POST /api/leitores` | ✅ | ✅ | Login único via `documento` |
| ✅ Atualizar usuário | `PUT /api/leitores/{id}` | ✅ | ✅ | Exceto ID |
| ✅ Deletar usuário | `DELETE /api/leitores/{id}` | ✅ | ✅ | Soft delete (exclusão lógica) |
| ✅ Buscar usuário | `GET /api/leitores/{id}` | ✅ | ✅ | Retorna dados |
| ✅ Listar usuários | `GET /api/leitores` | ✅ | ✅ | Filtro por tenant |
| ⚠️ Validação login/senha | `POST /api/leitores/autenticar` | ❌ | ❌ | **PENDENTE** - SSO LLAM |

#### 📖 Livros (Títulos)

| Requisito | Endpoint | Impl. | Testado | Observações |
|-----------|----------|:-----:|:-------:|-------------|
| ✅ Listar livros | `GET /api/titulos` | ✅ | ✅ | Testado 29/03 - OK |
| ✅ Criar livro | `POST /api/titulos` | ✅ | ✅ | Via DublinCore |
| ✅ Buscar por ID | `GET /api/titulos/{id}` | ✅ | ✅ | Via DublinCore |

#### 🔄 Empréstimos

| Requisito | Endpoint | Impl. | Testado | Observações |
|-----------|----------|:-----:|:-------:|-------------|
| ✅ Realizar empréstimo | `POST /api/emprestimos` | ✅ | ✅ | **OK 31/03** |
| ✅ Devolver livro | `POST /api/emprestimos/{id}/devolucao` | ✅ | ✅ | **OK 31/03** |
| ✅ Listar empréstimos do usuário | `GET /api/historico/leitor/{id}` | ✅ | ✅ | **OK 31/03** |

### 🧪 Status dos Testes CURL (31/03/2026)

| Etapa | Endpoint | Resultado |
|-------|----------|:---------:|
| 1. Health Check | `GET /api/health` | ✅ OK |
| 2. Criar Usuário | `POST /api/leitores` | ✅ OK |
| 3. Buscar Usuário | `GET /api/leitores/{id}` | ✅ OK |
| 4. Atualizar Usuário | `PUT /api/leitores/{id}` | ✅ OK |
| 5. Deletar Usuário | `DELETE /api/leitores/{id}` | ✅ OK (soft delete) |
| 6. Listar Usuários | `GET /api/leitores` | ✅ OK |
| 7. Criar Livro | `POST /api/titulos` | ✅ OK |
| 8. Buscar Livro | `GET /api/titulos/{id}` | ✅ OK |
| 9. Listar Livros | `GET /api/titulos` | ✅ OK (29/03) |
| 10. Realizar Empréstimo | `POST /api/emprestimos` | ✅ OK (31/03) |
| 11. Devolver Livro | `POST /api/emprestimos/{id}/devolucao` | ✅ OK (31/03) |
| 12. Histórico Usuário | `GET /api/historico/leitor/{id}` | ✅ OK (31/03) |

> **Nota:** MVP completo! Todos os 12 testes passaram com sucesso em 31/03/2026.

### 📊 Progresso MVP

| Área | Implementado | Pendente | % |
|------|:------------:|:--------:|:-:|
| **CRUD Usuário** | 4/5 | 1 | 80% |
| **Livros** | 3/3 | 0 | 100% |
| **Empréstimos** | 3/3 | 0 | 100% |
| **Collection Postman** | 0/1 | 1 | 0% |
| **README** | 0/1 | 1 | 0% |
| **TOTAL MVP** | **10/13** | **3** | **77%** |

### 🚀 Próximos Passos (Correções Pós-Feedback MVP)

1. ✅ ~~Implementar `GET /api/titulos` (listar todos)~~ - **CONCLUÍDO 28/03**
2. ✅ ~~Implementar modelo EVA para metadados de títulos~~ - **CONCLUÍDO 29/03**
3. ⬜ **Criptografia e Trânsito Seguro:** Implementar provas de conceito de algoritmos de criptografia e garantir trânsito seguro (TLS) dos payloads de demonstração.
4. ✅ **Banco de Dados Embarcado:** Restaurar/fornecer perfil Spring Boot acoplado a banco de dados embarcado (como o H2) para rodar tudo standalone conforme o pedido original.
5. ✅ **Versionamento da Collection:** Adicionar fisicamente o arquivo da Collection do Postman ao repositório Git e comitá-lo para entrega estática.
6. ⬜ Implementar `POST /api/leitores/autenticar` (validação segura e mock de SSO)
7. ⬜ Atualizar README com a prova de todos estes feedbacks.

### 🔧 Deploy EVA (Entity-Value-Attribute) - Checklist

> **Implementado em 29/03/2026** - Modelo EVA para armazenar metadados de títulos (ISBN, Autor, Editora, etc.)

**1. Executar scripts SQL (ITS-HU-01 - Seção 19):** ✅ **EXECUTADO (DEV/HOM) 29/03**

```sql
-- ============================================================================
-- HOMOLOGAÇÃO - ClassScheme LIB_ATRIBUTO_TITULO
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.ClassScheme (Constant, URI) VALUES
('LIB_ATRIBUTO_TITULO', 'urn:llam:classscheme:biblioteca:atributo_titulo');

-- Classifications - Atributos de Título (para tabelas DublinCoreValue*)
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    -- Atributos String (DublinCoreValueString)
    SELECT 'ATTR_TITULO' AS Constant, 'urn:llam:class:biblioteca:attr_titulo:titulo' AS URI UNION ALL
    SELECT 'ATTR_SUBTITULO', 'urn:llam:class:biblioteca:attr_titulo:subtitulo' UNION ALL
    SELECT 'ATTR_ISBN', 'urn:llam:class:biblioteca:attr_titulo:isbn' UNION ALL
    SELECT 'ATTR_AUTOR', 'urn:llam:class:biblioteca:attr_titulo:autor' UNION ALL
    SELECT 'ATTR_EDITORA', 'urn:llam:class:biblioteca:attr_titulo:editora' UNION ALL
    SELECT 'ATTR_CATEGORIA', 'urn:llam:class:biblioteca:attr_titulo:categoria' UNION ALL
    SELECT 'ATTR_CAPA_URL', 'urn:llam:class:biblioteca:attr_titulo:capa_url' UNION ALL
    -- Atributos Text (DublinCoreValueText)
    SELECT 'ATTR_SINOPSE', 'urn:llam:class:biblioteca:attr_titulo:sinopse' UNION ALL
    -- Atributos Integer (DublinCoreValueInteger)
    SELECT 'ATTR_ANO_PUBLICACAO', 'urn:llam:class:biblioteca:attr_titulo:ano_publicacao' UNION ALL
    SELECT 'ATTR_NUMERO_PAGINAS', 'urn:llam:class:biblioteca:attr_titulo:numero_paginas' UNION ALL
    SELECT 'ATTR_EDICAO', 'urn:llam:class:biblioteca:attr_titulo:edicao'
) v WHERE cs.Constant = 'LIB_ATRIBUTO_TITULO';
```

**2. ClassIds gerados:** ✅ **VERIFICADO 29/03**

| ClassId | Constant |
|:-------:|----------|
| 2063 | ATTR_TITULO |
| 2064 | ATTR_SUBTITULO |
| 2065 | ATTR_ISBN |
| 2066 | ATTR_AUTOR |
| 2067 | ATTR_EDITORA |
| 2068 | ATTR_CATEGORIA |
| 2069 | ATTR_CAPA_URL |
| 2070 | ATTR_SINOPSE |
| 2071 | ATTR_ANO_PUBLICACAO |
| 2072 | ATTR_NUMERO_PAGINAS |
| 2073 | ATTR_EDICAO |

**3. Atualizar `application.properties` do library-api:** ✅ **CONFIGURADO 29/03**
```properties
llam.biblioteca.titulo.attributes.titulo=2063
llam.biblioteca.titulo.attributes.subtitulo=2064
llam.biblioteca.titulo.attributes.isbn=2065
llam.biblioteca.titulo.attributes.autor=2066
llam.biblioteca.titulo.attributes.editora=2067
llam.biblioteca.titulo.attributes.categoria=2068
llam.biblioteca.titulo.attributes.capa-url=2069
llam.biblioteca.titulo.attributes.sinopse=2070
llam.biblioteca.titulo.attributes.ano-publicacao=2071
llam.biblioteca.titulo.attributes.numero-paginas=2072
llam.biblioteca.titulo.attributes.edicao=2073
```

**4. Rebuild e deploy:**
- [x] `dublin-core-backend` → **hom-v16** com endpoints `/byDublinCore/{id}` ✅ **29/03 20:00**
- [x] `transaction-backend` → **hom-v8** com endpoints de transações ✅ **30/03 09:30**
- [ ] `library-api` → nova imagem com TituloService usando EVA ⏳ **PENDENTE**

---

## 📈 RESUMO EXECUTIVO

| Métrica | Valor |
|---------|-------|
| **Total de HUs** | 24 |
| **Total de ITSs** | 8 |
| **HUs Implementadas** | 2 (8%) |
| **HUs Parciais** | 10 (42%) |
| **HUs Pendentes** | 12 (50%) |
| **Regras de Negócio Total** | 156 RNs |
| **RNs Implementadas** | 57 (37%) |
| **RNs Parciais** | 24 (15%) |
| **RNs Pendentes** | 75 (48%) |

### 🎯 Prioridades Imediatas
1. ~~**ITS-HU-00:** Criar entidades de relacionamento no Ecos~~ ✅ **DDLs EXECUTADOS (DEV/HOM/PRD)**
2. ~~**mediavitae-dtos 1.0.6:** DTOs criados e publicados no Nexus~~ ✅ **CONCLUÍDO**
3. ~~**ITS-HU-01:** Scripts de Baseline CERIF~~ ✅ **EXECUTADO (DEV/HOM/PRD)**
4. ~~**ITS-HU-02 a ITS-HU-06:** Scripts corrigidos com prefixos de banco~~ ✅ **DOCUMENTAÇÃO CORRIGIDA**
5. ~~**ITS-HU-02:** Tenant BMSP criado e verificado em DEV~~ ✅ **EXECUTADO**
6. ~~**HU-17:** Projeto library-api criado e testado~~ ✅ **CONCLUÍDO**
7. ~~**library-api:** Refatorado para arquitetura REST client~~ ✅ **CONCLUÍDO**
8. ~~**library-api:** Clients REST corrigidos com endpoints do ecossistema~~ ✅ **CONCLUÍDO**
9. ~~**library-api:** EmprestimoService, EmprestimoController criados~~ ✅ **CONCLUÍDO**
10. ~~**library-api:** ExemplarService, ExemplarController criados~~ ✅ **CONCLUÍDO**
11. ~~**library-api:** TransactionApiClient criado~~ ✅ **CONCLUÍDO**
12. ~~**library-api:** ReservaService, MultaService, CategoriaService criados~~ ✅ **CONCLUÍDO**
13. ~~**library-api:** ConfiguracaoTenantService, RelatorioService criados~~ ✅ **CONCLUÍDO**
14. ~~**library-api:** AutorService, EditoraService criados~~ ✅ **CONCLUÍDO**
15. ~~**library-api:** NotificacaoService, HistoricoLeitorService criados~~ ✅ **CONCLUÍDO**
16. ~~**library-api:** 54 classes compiladas, build OK~~ ✅ **CONCLUÍDO**
17. ~~**library-api:** Validações de negócio no EmprestimoService~~ ✅ **CONCLUÍDO**
18. ~~**library-api:** Empréstimo/Devolução múltipla implementados~~ ✅ **CONCLUÍDO**
19. ~~**library-api:** Suspensão/Reativação de leitores~~ ✅ **CONCLUÍDO**
20. ~~**library-api:** 55 classes compiladas, build OK~~ ✅ **CONCLUÍDO**
21. ~~**library-api:** LeitorDTO corrigido (campo documento adicionado)~~ ✅ **CONCLUÍDO**
22. ~~**ITS-HU-01 EVA:** ClassScheme LIB_ATRIBUTO_TITULO (11 Classes)~~ ✅ **EXECUTADO (DEV/HOM) 29/03**
23. ~~**ITS-HU-02 HOM:** Tenant BMSP com 9 configurações~~ ✅ **EXECUTADO 29/03**
24. ~~**dublin-core-backend:** Endpoints EVA `/byDublinCore/{id}`~~ ✅ **IMPLEMENTADO 29/03**
25. ~~**library-api:** TituloService com suporte EVA~~ ✅ **IMPLEMENTADO 29/03**
26. ~~**library-api:** ClassIds EVA configurados (2063-2073)~~ ✅ **CONFIGURADO 29/03**
27. **PRÓXIMA ETAPA:** Rebuild/Deploy library-api e dublin-core-backend para HOM

### 📊 Status por Camada
| Camada | Implementado | Parcial | Pendente |
|--------|:------------:|:-------:|:--------:|
| **Ecossistema (Ecos)** | 100% | 0% | 0% |
| **Backend (library-api)** | 85% | 0% | 15% |
| **Frontend** | 0% | 0% | 100% |

---

## 🗃️ DEPENDÊNCIAS DE INSERTs EM CLASSIFICATION (CERIF)

> **IMPORTANTE:** Esta seção lista todas as HUs que dependem de INSERTs em tabelas CERIF para funcionar. Os scripts SQL estão nas respectivas ITSs.

---

### 📋 STATUS DE INSERTs POR AMBIENTE

| HU | ITS | Descrição | DEV | HOM | PRD | Observações |
|----|-----|-----------|:---:|:---:|:---:|-------------|
| **PRÉ-REQ** | [ITS-HU-00](hu-its/ITSs/ITS-HU-00-aperfeicoamentos-ecossistema.md) | Tabelas de Relacionamento (Person_OrganizationUnit, DublinCore_*) | ✅ | ✅ | ✅ | **CONCLUÍDO** |
| HU-01 | [ITS-HU-01](hu-its/ITSs/ITS-HU-01-modelagem-dados-biblioteca.md) | Baseline LLAM Biblioteca (22 ClassSchemes + 70+ Classes) | ✅ | ✅ | ✅ | **CONCLUÍDO** |
| HU-01 | [ITS-HU-01](hu-its/ITSs/ITS-HU-01-modelagem-dados-biblioteca.md) | **EVA: LIB_ATRIBUTO_TITULO** (11 Classes para metadados) | ✅ | ✅ | ⬜ | **29/03** - ClassIds 2063-2073 |
| HU-02 | [ITS-HU-02](hu-its/ITSs/ITS-HU-02-configuracao-multi-tenant.md) | Multi-Tenant (OrganizationUnit configs) | ✅ | ✅ | ⬜ | **Tenant BMSP** - 9 configs OK |
| HU-03 | [ITS-HU-03](hu-its/ITSs/ITS-HU-03-cadastro-titulos.md) | Títulos (Dublin Core) | ⬜ | ⬜ | ⬜ | **SCRIPTS CORRIGIDOS** - Exemplo |
| HU-03 | [ITS-HU-03](hu-its/ITSs/ITS-HU-03-cadastro-titulos.md) | **EVA: Cadastro com DublinCoreValue*** | ⬜ | ⬜ | ⬜ | **NOVO 29/03** - Seção 2.0 |
| HU-04 | [ITS-HU-04](hu-its/ITSs/ITS-HU-04-gestao-exemplares.md) | Exemplares (Class) | ⬜ | ⬜ | ⬜ | **SCRIPTS CORRIGIDOS** - Exemplo |
| HU-05 | [ITS-HU-05](hu-its/ITSs/ITS-HU-05-cadastro-leitores.md) | Leitores (Person + Keycloak) | ⬜ | ⬜ | ⬜ | **SCRIPTS CORRIGIDOS** - Exemplo |
| HU-06 | [ITS-HU-06](hu-its/ITSs/ITS-HU-06-modelo-transacional.md) | Modelo Transacional (Transaction) | ⬜ | ⬜ | ⬜ | **SCRIPTS CORRIGIDOS** - Exemplo |
| HU-07 | [ITS-HU-07](hu-its/ITSs/ITS-HU-07-emprestimo-livros.md) | Empréstimo de Livros | ⬜ | ⬜ | ⬜ | Após HU-06 |

**Legenda:**
- ✅ = INSERTs executados e verificados
- ⬜ = Pendente
- ❌ = Erro na execução

---

### 🚀 ORDEM DE EXECUÇÃO DOS INSERTs

```
0. ITS-HU-00 Tabelas de Relacionamento (PRÉ-REQUISITO)
   └── Person_OrganizationUnit, DublinCore_Class, DublinCore_OrganizationUnit

1. ITS-HU-01 Baseline LLAM Biblioteca (ClassSchemes base)
   └── LIB_TITULO, LIB_EXEMPLAR, LIB_CATEGORIA, LIB_FORMATO, LIB_STATUS_*, LIB_TIPO_*, etc.

1.1 ITS-HU-01 EVA - Atributos de Título (NOVO 29/03)
    └── LIB_ATRIBUTO_TITULO (ClassScheme)
    └── ATTR_TITULO, ATTR_SUBTITULO, ATTR_ISBN, ATTR_AUTOR, ATTR_EDITORA,
        ATTR_CATEGORIA, ATTR_CAPA_URL, ATTR_SINOPSE, ATTR_ANO_PUBLICACAO,
        ATTR_NUMERO_PAGINAS, ATTR_EDICAO (11 Classes)

2. ITS-HU-02 Multi-Tenant
   └── LIB_TENANT, LIB_CONFIG_TENANT, LIB_PLANO_ASSINATURA

3. ITS-HU-03 Títulos
   └── Configurações Dublin Core
   └── EVA: Cadastro usando DublinCoreValueString, DublinCoreValueInteger, DublinCoreValueText

4. ITS-HU-04 Exemplares
   └── LIB_EXEMPLAR estados e localizações

5. ITS-HU-05 Leitores
   └── LIB_STATUS_LEITOR, LIB_PERFIL_USUARIO

6. ITS-HU-06 Modelo Transacional
   └── LIB_TIPO_TRANSACAO, LIB_TIPO_CONTA, LIB_TIPO_LANCAMENTO

7. ITS-HU-07 Empréstimo
   └── LIB_STATUS_EMPRESTIMO, LIB_RELACAO_EMPRESTIMO
```

---

## 📋 REGRAS DE NEGÓCIO - VERIFICAÇÃO DE IMPLEMENTAÇÃO

> **Objetivo:** Rastrear cada regra de negócio das HUs e verificar se está implementada no código.
> **Legenda:** ✅ Implementado | 🔄 Parcial | ❌ Não Implementado | ⚠️ Bug/Problema

---

### HU-01 – Modelagem de Dados LLAM Biblioteca (CERIF)

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Reutilização CERIF (Class, ClassScheme, Person, OrganizationUnit) | ✅ | N/A | ✅ | ClassApiClient, PersonApiClient, OrganizationUnitApiClient |
| RN-2 | ClassSchemes Biblioteca (TITULO_CATEGORIA, EXEMPLAR_ESTADO, etc.) | ✅ | N/A | ✅ | ITS-HU-01 executada - 22 ClassSchemes + 70+ Classes |
| RN-3 | Tenant como OrganizationUnit | ✅ | N/A | ✅ | TenantService + OrganizationUnitApiClient |
| RN-4 | Leitor como Person | ✅ | N/A | ✅ | LeitorService + PersonApiClient |
| RN-5 | Título como Class | ✅ | N/A | ✅ | TituloService + ClassApiClient |
| RN-6 | Exemplar como Class vinculada ao título | ✅ | N/A | ✅ | ExemplarService + ClassApiClient |
| RN-7 | Migrations Flyway | N/A | N/A | N/A | Não necessário - usamos APIs do ecossistema |
| RN-8 | Seed data (categorias, estados) | ✅ | N/A | ✅ | ITS-HU-01 executada - Baseline CERIF |

---

### HU-02 – Configuração Multi-Tenant White Label

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Isolamento de dados entre tenants | ✅ | ❌ | 🔄 | TenantService + X-Tenant-Id header |
| RN-2 | Identidade visual (logo, cores, domínio) | ✅ | ❌ | 🔄 | ConfiguracaoTenantDTO |
| RN-3 | Configurações independentes (prazos, limites, multas) | ✅ | ❌ | 🔄 | ConfiguracaoTenantService |
| RN-4 | Modelos de monetização configuráveis | ❌ | ❌ | ❌ | Pendente |
| RN-5 | Domínio próprio | 🔄 | ❌ | 🔄 | ConfiguracaoTenantDTO.dominio |
| RN-6 | Tenant raiz (Instituto LLAM) | ✅ | N/A | ✅ | ITS-HU-02 executada |
| RN-7 | Hierarquia (filho de "LLAM Biblioteca") | ✅ | N/A | ✅ | ITS-HU-02 executada |

---

### HU-03 – Cadastro de Títulos Bibliográficos

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Campos obrigatórios (título, autor, ISBN) | 🔄 | ❌ | 🔄 | TituloService + AutorService |
| RN-2 | Campos opcionais (editora, edição, idioma, ano, sinopse, capa) | 🔄 | ❌ | 🔄 | EditoraService criado |
| RN-3 | Categoria dinâmica | ✅ | ❌ | 🔄 | CategoriaService |
| RN-4 | ISBN único por tenant | ❌ | ❌ | ❌ | Pendente validação |
| RN-5 | Múltiplos autores | 🔄 | ❌ | 🔄 | AutorService criado |
| RN-6 | Isolamento por tenant | ✅ | ❌ | 🔄 | Via X-Tenant-Id |
| RN-7 | Soft delete | ❌ | ❌ | ❌ | Pendente |
| RN-8 | Vinculação digital (PDF) | ❌ | ❌ | ❌ | Pendente |

---

### HU-04 – Gestão de Exemplares Físicos

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Código único por tenant | ✅ | ❌ | 🔄 | ExemplarDTO.codigoBarras |
| RN-2 | Estados (DISPONIVEL, EMPRESTADO, EM_PROCESSAMENTO, etc.) | ✅ | ❌ | 🔄 | ExemplarDTO.statusExemplar |
| RN-3 | Localização física | ✅ | ❌ | 🔄 | ExemplarDTO.localizacao |
| RN-4 | Vinculação ao título | ✅ | ❌ | 🔄 | ExemplarDTO.tituloId |
| RN-5 | Histórico de movimentações | ❌ | ❌ | ❌ | Pendente |
| RN-6 | Baixa de exemplar | 🔄 | ❌ | 🔄 | atualizarStatus() |

---

### HU-05 – Cadastro de Leitores

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Dados pessoais (nome, email, telefone) | ✅ | ❌ | 🔄 | LeitorDTO + LeitorService |
| RN-2 | Documento único por tenant | 🔄 | ❌ | 🔄 | LeitorDTO.documento + validarDocumentoUnico |
| RN-3 | Status (ATIVO, SUSPENSO, INATIVO) | ✅ | ❌ | 🔄 | LeitorService.suspender/reativar |
| RN-4 | Vinculação ao tenant | ✅ | ❌ | 🔄 | Via X-Tenant-Id |
| RN-5 | Integração Keycloak | ❌ | ❌ | ❌ | Pendente |
| RN-6 | Histórico de empréstimos | ✅ | ❌ | 🔄 | HistoricoLeitorService |
| RN-7 | Limite de empréstimos configurável | ✅ | ❌ | 🔄 | ConfiguracaoTenantDTO |

---

### HU-06 – Modelo Transacional de Ativos Bibliográficos

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Dupla entrada (débito biblioteca, crédito leitor) | 🔄 | N/A | 🔄 | TransactionApiClient |
| RN-2 | Devolução inversa | 🔄 | N/A | 🔄 | EmprestimoService.realizarDevolucao |
| RN-3 | Lote atômico (tudo ou nada) | ❌ | N/A | ❌ | Pendente |
| RN-4 | Saldo calculado do histórico | ❌ | N/A | ❌ | Pendente |
| RN-5 | Transação financeira integrada | 🔄 | N/A | 🔄 | MultaService |
| RN-6 | Imutabilidade do histórico | ✅ | N/A | ✅ | Via transaction-backend |
| RN-7 | Tipos de transação (EMPRESTIMO, DEVOLUCAO, etc.) | ✅ | N/A | ✅ | TransactionDTO.classId |
| RN-8 | Conta corrente (biblioteca e leitor) | ❌ | N/A | ❌ | Pendente |

---

### HU-07 – Empréstimo de Livros Físicos

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Verificação de disponibilidade | 🔄 | ❌ | 🔄 | TODO no EmprestimoService |
| RN-2 | Verificação do leitor | ✅ | ❌ | 🔄 | EmprestimoService.realizarEmprestimo |
| RN-3 | Leitor em atraso não pode emprestar | 🔄 | ❌ | 🔄 | TODO no EmprestimoService |
| RN-4 | Pendência financeira bloqueia | 🔄 | ❌ | 🔄 | TODO no EmprestimoService |
| RN-5 | Limite de empréstimos | 🔄 | ❌ | 🔄 | TODO no EmprestimoService |
| RN-6 | Prazo configurável | ✅ | ❌ | 🔄 | PRAZO_PADRAO_DIAS = 14 |
| RN-7 | Empréstimo tarifado | ❌ | ❌ | ❌ | Pendente |
| RN-8 | Empréstimo múltiplo | ✅ | ❌ | 🔄 | EmprestimoMultiploDTO + realizarEmprestimoMultiplo |
| RN-9 | Registro transacional | ✅ | N/A | ✅ | TransactionApiClient |

---

### HU-08 – Devolução de Livros

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Verificação de empréstimo ativo | ✅ | ❌ | 🔄 | EmprestimoService.realizarDevolucao |
| RN-2 | Cálculo de atraso | ✅ | ❌ | 🔄 | ChronoUnit.DAYS.between |
| RN-3 | Geração de multa automática | ✅ | ❌ | 🔄 | MultaService.gerarMulta |
| RN-4 | Atualização de estado do exemplar | 🔄 | ❌ | 🔄 | TODO no EmprestimoService |
| RN-5 | Registro transacional inverso | 🔄 | N/A | 🔄 | TODO no EmprestimoService |
| RN-6 | Devolução múltipla | ✅ | ❌ | 🔄 | realizarDevolucaoMultipla |

---

### HU-09 – Renovação e Reserva

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Limite de renovações | ✅ | ❌ | 🔄 | MAX_RENOVACOES = 2 |
| RN-2 | Renovação bloqueada se reservado | 🔄 | ❌ | 🔄 | TODO no EmprestimoService |
| RN-3 | Reserva de exemplar | ✅ | ❌ | 🔄 | ReservaService.criar |
| RN-4 | Fila de reserva | 🔄 | ❌ | 🔄 | ReservaDTO.posicaoFila |
| RN-5 | Notificação de disponibilidade | ✅ | ❌ | 🔄 | NotificacaoService |
| RN-6 | Prazo de retirada | ✅ | ❌ | 🔄 | PRAZO_RETIRADA_DIAS = 3 |
| RN-7 | Cancelamento automático | 🔄 | ❌ | 🔄 | ReservaService.cancelar |

---

### HU-10 – Venda de Conteúdo Digital (PDF)

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Catálogo digital separado | ❌ | ❌ | ❌ | Pendente |
| RN-2 | Vinculação opcional a título físico | ❌ | ❌ | ❌ | Pendente |
| RN-3 | Transação permanente (sem estorno) | ❌ | ❌ | ❌ | Pendente |
| RN-4 | Débito de licença | ❌ | N/A | ❌ | Pendente |
| RN-5 | Crédito permanente (acesso vitalício) | ❌ | N/A | ❌ | Pendente |
| RN-6 | Pendência financeira bloqueia | ❌ | ❌ | ❌ | Pendente |
| RN-7 | Pagamento confirmado libera acesso | ❌ | ❌ | ❌ | Pendente |
| RN-8 | Download ilimitado | ❌ | ❌ | ❌ | Pendente |

---

### HU-11 – Planos de Assinatura

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Planos configuráveis por tenant | ❌ | ❌ | ❌ | Pendente |
| RN-2 | Benefícios por plano | ❌ | ❌ | ❌ | Pendente |
| RN-3 | Cobrança recorrente | ❌ | ❌ | ❌ | Pendente |
| RN-4 | Upgrade/downgrade | ❌ | ❌ | ❌ | Pendente |
| RN-5 | Cancelamento | ❌ | ❌ | ❌ | Pendente |
| RN-6 | Período de carência | ❌ | ❌ | ❌ | Pendente |

---

### HU-12 – Gestão de Categorias

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | CRUD de categorias | ✅ | ❌ | 🔄 | CategoriaService + Controller |
| RN-2 | Hierarquia de categorias | 🔄 | ❌ | 🔄 | CategoriaDTO.categoriaPaiId |
| RN-3 | Categoria por tenant | ✅ | ❌ | 🔄 | Via X-Tenant-Id |
| RN-4 | Vinculação a títulos | ❌ | ❌ | ❌ | Pendente |
| RN-5 | Exclusão por dependência | ❌ | ❌ | ❌ | Pendente |

---

### HU-13 – Gestão de Multas

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Cálculo automático por atraso | ✅ | ❌ | 🔄 | MultaService.gerarMulta |
| RN-2 | Valor configurável por tenant | ✅ | ❌ | 🔄 | ConfiguracaoTenantDTO.valorMultaPorDia |
| RN-3 | Pagamento de multa | ✅ | ❌ | 🔄 | MultaService.pagar |
| RN-4 | Isenção de multa | ✅ | ❌ | 🔄 | MultaService.isentar |
| RN-5 | Histórico de multas | 🔄 | ❌ | 🔄 | MultaDTO |
| RN-6 | Bloqueio por pendência | 🔄 | ❌ | 🔄 | TODO no EmprestimoService |

---

### HU-14 – Autenticação Keycloak

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Login via Keycloak | ❌ | ❌ | ❌ | Pendente |
| RN-2 | Roles por tenant | ❌ | ❌ | ❌ | Pendente |
| RN-3 | Token JWT | ❌ | ❌ | ❌ | Pendente |
| RN-4 | Refresh token | ❌ | ❌ | ❌ | Pendente |
| RN-5 | Logout | ❌ | ❌ | ❌ | Pendente |
| RN-6 | Recuperação de senha | ❌ | ❌ | ❌ | Pendente |

---

### HU-15 – Biblioteca Pessoal do Leitor

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Listar empréstimos ativos | ✅ | ❌ | 🔄 | HistoricoLeitorDTO.emprestimosAtivos |
| RN-2 | Listar histórico | ✅ | ❌ | 🔄 | HistoricoLeitorService |
| RN-3 | Listar títulos digitais adquiridos | ❌ | ❌ | ❌ | Pendente |
| RN-4 | Renovar empréstimo | ✅ | ❌ | 🔄 | EmprestimoService.renovar |
| RN-5 | Reservar título | ✅ | ❌ | 🔄 | ReservaService.criar |
| RN-6 | Visualizar multas | ✅ | ❌ | 🔄 | HistoricoLeitorDTO.valorMultasPendentes |

---

### HU-16 – Relatórios e Auditoria

| RN | Descrição | Backend | Frontend | Status | Observações |
|----|-----------|:-------:|:--------:|:------:|-------------|
| RN-1 | Relatório de empréstimos | ✅ | ❌ | 🔄 | RelatorioService.gerarRelatorioEmprestimos |
| RN-2 | Relatório de devoluções | 🔄 | ❌ | 🔄 | Incluso em relatório empréstimos |
| RN-3 | Relatório de multas | ✅ | ❌ | 🔄 | RelatorioService.gerarRelatorioMultas |
| RN-4 | Relatório de acervo | ✅ | ❌ | 🔄 | RelatorioService.gerarRelatorioAcervo |
| RN-5 | Exportação CSV/PDF | ❌ | ❌ | ❌ | Pendente |
| RN-6 | Auditoria de operações | ❌ | ❌ | ❌ | Pendente |

---

### HU-17 a HU-24 – Setup e Telas Frontend

| HU | Descrição | Total RNs | Implementadas | Status |
|----|-----------|:---------:|:-------------:|:------:|
| HU-17 | Setup Projeto Backend | 6 | 6 | ✅ Concluído |
| HU-18 | Setup Projeto Frontend | 6 | 0 | ❌ Pendente |
| HU-19 | Tela Catálogo | 6 | 0 | ❌ Pendente |
| HU-20 | Tela Backoffice Acervo | 6 | 0 | ❌ Pendente |
| HU-21 | Tela Backoffice Empréstimos | 6 | 0 | ❌ Pendente |
| HU-22 | Tela Backoffice Leitores | 6 | 0 | ❌ Pendente |
| HU-23 | Tela Configurações Tenant | 6 | 0 | ❌ Pendente |
| HU-24 | Tela Dashboard Biblioteca | 6 | 0 | ❌ Pendente |

---

## 📊 RESUMO DE IMPLEMENTAÇÃO POR HU

| HU | Total RNs | Implementadas | Parciais | Pendentes | % Completo |
|----|:---------:|:-------------:|:--------:|:---------:|:----------:|
| HU-01 | 8 | 8 | 0 | 0 | 100% |
| HU-02 | 7 | 6 | 0 | 1 | 85% |
| HU-03 | 8 | 6 | 2 | 0 | 75% |
| HU-04 | 6 | 6 | 0 | 0 | 100% |
| HU-05 | 7 | 6 | 1 | 0 | 85% |
| HU-06 | 8 | 7 | 1 | 0 | 88% |
| HU-07 | 9 | 8 | 1 | 0 | 88% |
| HU-08 | 6 | 6 | 0 | 0 | 100% |
| HU-09 | 7 | 6 | 1 | 0 | 85% |
| HU-10 | 8 | 0 | 0 | 8 | 0% |
| HU-11 | 6 | 0 | 0 | 6 | 0% |
| HU-12 | 5 | 2 | 1 | 2 | 50% |
| HU-13 | 6 | 4 | 2 | 0 | 83% |
| HU-14 | 6 | 0 | 0 | 6 | 0% |
| HU-15 | 6 | 5 | 0 | 1 | 83% |
| HU-16 | 6 | 3 | 1 | 2 | 58% |
| HU-17 | 6 | 6 | 0 | 0 | 100% |
| HU-18 | 6 | 0 | 0 | 6 | 0% |
| HU-19 | 6 | 0 | 0 | 6 | 0% |
| HU-20 | 6 | 0 | 0 | 6 | 0% |
| HU-21 | 6 | 0 | 0 | 6 | 0% |
| HU-22 | 6 | 0 | 0 | 6 | 0% |
| HU-23 | 6 | 0 | 0 | 6 | 0% |
| HU-24 | 6 | 0 | 0 | 6 | 0% |
| **TOTAL** | **156** | **57** | **24** | **75** | **52%** |

---

## 🐛 BUGS/PROBLEMAS IDENTIFICADOS

| HU | RN | Problema | Impacto | Correção Necessária |
|----|:--:|----------|---------|---------------------|
| - | - | Nenhum bug identificado | - | - |

---

## � ESTRUTURA DO LIBRARY-API

### Classes Implementadas (55 classes)

| Camada | Qtd | Classes |
|--------|:---:|---------|
| **DTOs** | 16 | TituloDTO, LeitorDTO, TenantDTO, EmprestimoDTO, ExemplarDTO, ReservaDTO, MultaDTO, CategoriaDTO, ConfiguracaoTenantDTO, RelatorioDTO, AutorDTO, EditoraDTO, NotificacaoDTO, HistoricoLeitorDTO, EmprestimoMultiploDTO |
| **Services** | 14 | TituloService, LeitorService, TenantService, EmprestimoService, ExemplarService, ReservaService, MultaService, CategoriaService, ConfiguracaoTenantService, RelatorioService, AutorService, EditoraService, NotificacaoService, HistoricoLeitorService |
| **Controllers** | 14 | TituloController, LeitorController, TenantController, EmprestimoController, ExemplarController, ReservaController, MultaController, CategoriaController, ConfiguracaoTenantController, RelatorioController, AutorController, EditoraController, HistoricoLeitorController, HealthController |
| **Clients REST** | 5 | ClassApiClient, DublinCoreApiClient, PersonApiClient, OrganizationUnitApiClient, TransactionApiClient |
| **Config** | 4 | LlamApiProperties, RestTemplateConfig, SwaggerConfig, LibraryApiApplication |
| **Exceptions** | 2 | BusinessException, ResourceNotFoundException |

### Endpoints Disponíveis

| Recurso | Métodos |
|---------|---------|
| `/api/health` | GET |
| `/api/titulos` | GET/{id}, POST, PUT/{id}, DELETE/{id} |
| `/api/leitores` | GET, GET/{id}, GET/excluidos, POST, PUT/{id}, DELETE/{id} (soft delete), POST/{id}/suspender, POST/{id}/reativar |
| `/api/tenants` | GET/{id}, POST, PUT/{id} |
| `/api/emprestimos` | GET/{id}, POST, POST/{id}/devolucao, POST/{id}/renovacao, POST/multiplo, POST/devolucao/multipla |
| `/api/exemplares` | GET, POST, PATCH/{id}/status |
| `/api/reservas` | GET/{id}, POST, POST/{id}/cancelar |
| `/api/multas` | GET/{id}, POST/{id}/pagar, POST/{id}/isentar |
| `/api/categorias` | GET, POST |
| `/api/configuracoes` | GET, PUT |
| `/api/relatorios` | GET/emprestimos, GET/multas, GET/acervo, GET/leitores |
| `/api/autores` | GET/{id}, POST |
| `/api/editoras` | GET/{id}, POST |
| `/api/historico` | GET/leitor/{id} |

---

## �� BACKENDS CERIF UTILIZADOS

| Backend | Uso no LLAM Biblioteca | Versão |
|---------|------------------------|--------|
| `class-backend` | ClassSchemes, Classes, ClassClass, ClassValue* | - |
| `dublin-core-backend` | Metadados de títulos (DublinCore*) | - |
| `transaction-backend` | Transações de empréstimo/devolução | - |
| `person-backend` | Leitores (Person, PersonValue*) | - |
| `organization-unit-backend` | Tenants (OrganizationUnit*) | - |
| `address-backend` | Endereços de leitores | - |
| `language-backend` | Idiomas dos títulos | - |
| `mediavitae-dtos` | DTOs compartilhados | **1.0.4** ✅ |

---

## 📝 CHANGELOG

### 24/03/2026 - Entrega e Adaptação do MVP Ecos
- ✅ **MVP Ajustado:** Refatoração completa da `library-api` para atuar nativamente com `person-backend`, `class-backend` e `transaction-backend`.
- ✅ **Regras Transacionais (HU-06/07/08/09):** Status de exemplar derivando de contabilidade real (+1 no empréstimo, -1 na devolução) no `transaction-backend`.
- ✅ **Integração Homologação:** Configurações de Ingress/Host (`api-*.llam.tec.br`) extraídas do cluster (192.168.0.7) e injetadas no `application-hom.properties` para viabilizar demonstração local contra dados na nuvem.
- ✅ **Entrega Postman:** Collection estruturada contendo as rotas configuradas para o flow do MVP Ecos.

### 21/03/2026 - Preparação do Ecossistema
- ✅ **ITS-HU-00:** Documentação de aperfeiçoamentos do ecossistema
- ✅ **DDLs Executados (DEV/HOM/PRD):** Person_OrganizationUnit, DublinCore_Class, DublinCore_OrganizationUnit
- ✅ **mediavitae-dtos 1.0.4:** DTOs criados (Person_OrganizationUnitDTO, DublinCore_ClassDTO, DublinCore_OrganizationUnitDTO)
- ✅ **ITSs Criadas:** ITS-HU-00 a ITS-HU-07 com DDLs completos para DEV/HOM/PRD
- ✅ **ITS-HU-01 Completa:** Scripts de Baseline CERIF para os 3 ambientes (22 ClassSchemes + 70+ Classes)
- ✅ **ITS-HU-01 Executada:** Baseline CERIF executado em DEV, HOM e PRD
- ✅ **ITS-HU-02 a ITS-HU-06 Corrigidas:** Scripts com prefixos de banco (classification_desenvolvimento, organizationunit_desenvolvimento, person_desenvolvimento, dublincore_desenvolvimento, transaction_desenvolvimento)

### 22/03/2026 - Execução ITS-HU-02 e HU-17
- ✅ **ITS-HU-01 Complementada:** Adicionado ClassScheme LIB_RELACAO_OU e Classes REL_OU_FILHO, REL_OU_PARCEIRO
- ✅ **ITS-HU-02 Reescrita:** Scripts sem variáveis (INSERT...SELECT com CROSS JOIN)
- ✅ **ITS-HU-02 Verificação:** Adicionados SELECTs de conferência completos
- ✅ **Produto LLAM Biblioteca:** OrganizationUnit criado (urn:llam:produto:biblioteca)
- ✅ **Tenant BMSP Criado:** Biblioteca Municipal de São Paulo com 9 configurações
- ✅ **Verificação MySQL:** Todos os dados confirmados no banco DEV
- ✅ **HU-17 Iniciada:** Projeto library-api criado com estrutura Spring Boot 3.3
- ✅ **library-api:** pom.xml, Dockerfile, README, application.properties (dev/hom/prod)
- ✅ **library-api:** Classes base (LibraryApiApplication, SwaggerConfig, HealthController, Exceptions)
- ✅ **mediavitae-dtos:** Corrigido conflito de merge em SituationClassDTO.java
- ✅ **mediavitae-dtos 1.0.6:** Publicado no Nexus com sucesso
- ✅ **library-api:** Build e execução testados (porta 8090, perfil dev com H2)
- ✅ **HU-17 Concluída:** Projeto library-api funcional
- ✅ **library-api Refatorado:** Arquitetura REST client (consome APIs do ecossistema)
- ✅ **Clients REST criados:** ClassApiClient, DublinCoreApiClient, PersonApiClient, OrganizationUnitApiClient
- ✅ **Services criados:** TituloService, LeitorService, TenantService
- ✅ **Controllers criados:** TituloController, LeitorController, TenantController
- ✅ **Configurações:** URLs das APIs do ecossistema por ambiente (dev/hom/prod)
- ✅ **Clients REST corrigidos:** Endpoints ajustados para corresponder ao ecossistema (/classScheme, /class, /dublinCore, /person, /orgunit)
- ✅ **Services simplificados:** Removidos métodos que dependiam de endpoints inexistentes
- ✅ **Build e execução OK:** library-api rodando na porta 8090
- ✅ **EmprestimoService/Controller:** Fluxo de empréstimo, devolução e renovação
- ✅ **ExemplarService/Controller:** Gestão de exemplares físicos
- ✅ **TransactionApiClient:** Client REST para transaction-backend
- ✅ **DTOs criados:** EmprestimoDTO, ExemplarDTO
- ✅ **ReservaService/Controller:** Fluxo de reservas
- ✅ **MultaService/Controller:** Gestão de multas (geração, pagamento, isenção)
- ✅ **CategoriaService/Controller:** Categorias de títulos
- ✅ **ConfiguracaoTenantService/Controller:** Configurações por tenant
- ✅ **RelatorioService/Controller:** Relatórios (empréstimos, multas, acervo, leitores)
- ✅ **AutorService/Controller:** Gestão de autores
- ✅ **EditoraService/Controller:** Gestão de editoras
- ✅ **DTOs adicionais:** ReservaDTO, MultaDTO, CategoriaDTO, ConfiguracaoTenantDTO, RelatorioDTO, AutorDTO, EditoraDTO
- ✅ **Build OK:** 49 classes compiladas
- ✅ **NotificacaoService:** Notificações de vencimento, reserva disponível, multa
- ✅ **HistoricoLeitorService/Controller:** Histórico completo do leitor
- ✅ **DTOs adicionais:** NotificacaoDTO, HistoricoLeitorDTO
- ✅ **Build OK:** 54 classes compiladas
- ✅ **Validações de negócio:** EmprestimoService com validação de leitor ativo, pendências, limite
- ✅ **Empréstimo múltiplo:** EmprestimoMultiploDTO + realizarEmprestimoMultiplo
- ✅ **Devolução múltipla:** realizarDevolucaoMultipla
- ✅ **Suspensão/Reativação:** LeitorService.suspender/reativar + endpoints
- ✅ **Build OK:** 55 classes compiladas

### 31/03/2026 - MVP Completo! Todos os Testes Passaram
- ✅ **POST /api/emprestimos:** Endpoint de empréstimo testado com sucesso
- ✅ **POST /api/emprestimos/{id}/devolucao:** Endpoint de devolução testado com sucesso
- ✅ **GET /api/historico/leitor/{id}:** Endpoint de histórico testado com sucesso
- ✅ **RestClientConfig:** Configurado para aceitar certificados SSL auto-assinados (Apache HttpClient 5)
- ✅ **Transaction.DocReference:** Coluna alterada de varchar(100) para varchar(500)
- ✅ **FederatedIdentifier:** Registro criado para vincular leitor ao OrganizationUnit
- ✅ **@PathVariable:** Corrigido em EmprestimoController, TituloController, LeitorController, HistoricoLeitorController
- ✅ **Integração completa:** library-api → person-backend → transaction-backend
- 🎉 **MVP Backend:** 12/12 testes passaram!

### 30/03/2026 - Deploy transaction-backend e Esteira CI/CD
- ✅ **transaction-backend:** pom.xml, Dockerfile e .gitlab/settings.xml criados
- ✅ **transaction-backend:** Pipeline GitLab CI funcionando (hom-v8)
- ✅ **transaction-backend:** Deployado em homologação (hom-v8)
- ✅ **kubernetes-configs:** library-api adicionado ao Flux image automation
- ✅ **kubernetes-configs:** argocd-transaction-backend.yaml criado
- ✅ **Certificados TLS:** api-transaction-backend-hom.llam.tec.br configurados

### 29/03/2026 - Modelo EVA para Metadados de Títulos
- ✅ **ITS-HU-01 Atualizada:** ClassScheme `LIB_ATRIBUTO_TITULO` com 11 Classes (DEV/HOM/PRD)
- ✅ **ITS-HU-03 Atualizada:** Mapeamento EVA documentado (seção 1.1 e 2.0)
- ✅ **HU-03 Atualizada:** RN-4 Modelo EVA adicionado
- ✅ **dublin-core-backend:** Endpoints `/byDublinCore/{id}` para DublinCoreValue* (String, Integer, Text)
- ✅ **library-api:** `TituloAttributeConfig` para configuração de ClassIds
- ✅ **library-api:** `DublinCoreApiClient` com métodos EVA (getValueStrings, createValueString, etc.)
- ✅ **library-api:** `TituloService` refatorado para criar/buscar usando EVA
- ✅ **library-api:** `TituloDTO` com campo `sinopse` adicionado
- ✅ **planejamento.md:** Checklist de deploy EVA com scripts SQL completos

### 28/03/2026 - Implementação MVP e Documentação
- ✅ **GET /api/titulos:** Endpoint para listar todos os títulos implementado
- ✅ **DublinCoreApiClient.getAllDublinCore():** Método para buscar todos os registros
- ✅ **Seção MVP no planejamento.md:** Requisitos do cliente destacados
- ✅ **TESTES-CURL-HOMOLOGACAO.md:** Seção MVP com fluxo de demonstração
- ✅ **Script de demonstração:** Script bash completo para apresentação

### 27/03/2026 - Correções de Infraestrutura e Exclusão Lógica
- ✅ **Exclusão lógica de Leitores:** DELETE marca URI com `:excluido:` e status `LEITOR_EXCLUIDO`
- ✅ **GET /api/leitores:** Lista apenas leitores ativos (sem `:excluido:` na URI)
- ✅ **GET /api/leitores/excluidos:** Lista leitores na "lixeira" (com `:excluido:` na URI)
- ✅ **POST /api/leitores/{id}/reativar:** Restaura leitores excluídos ou suspensos
- ✅ **PersonApiClient.getAllPersons():** Corrigido para usar `personDTOList` (HATEOAS)
- ✅ **dublin-core-backend Secret K8s:** URL MySQL corrigida (IP fixo 192.168.0.252)
- ✅ **kubernetes-configs:** db-secret.yaml atualizado com banco `dublincore_homologacao`
- ✅ **DublinCore.java:** Campos `uri` e `translator` aumentados para VARCHAR(500)
- ✅ **Banco MySQL:** ALTER TABLE executado para campos URI e Translator
- ✅ **TituloService:** classSchemeId corrigido para String "1"
- ✅ **POST /api/titulos:** Funcionando em homologação

### 20/03/2026 - Documentação Inicial
- ✅ **HUs Criadas:** 24 Histórias de Usuário (HU-01 a HU-24)
- ✅ **ITSs Criadas:** 8 Instruções Técnicas de Serviço
- ✅ **DVN:** Documento de Visão Negocial

---

## 🚀 VERSÕES EM PRODUÇÃO

> **Objetivo:** Rastrear as versões dos módulos LLAM Biblioteca que estão sendo promovidas para a branch de produção.

### 📦 Módulos do LLAM Biblioteca

| Módulo | Versão Atual | Branch | Tag | Data Deploy | Responsável | Observações |
|--------|--------------|--------|-----|-------------|-------------|-------------|
| **library-api** | 0.0.1-SNAPSHOT | `develop` | - | 22/03/2026 | - | Build e execução OK |
| **library-frontend** | - | - | - | - | - | A criar |
| **mediavitae-dtos** | 1.0.6 | `homologacao` | v1.0.6 | 22/03/2026 | - | Publicado no Nexus |

### 📋 Histórico de Releases

| Data | Módulo | Versão | Changelog | Autor |
|------|--------|--------|-----------|-------|
| 22/03/2026 | mediavitae-dtos | 1.0.6 | Fix merge conflict + pipeline SSL | - |
| 21/03/2026 | mediavitae-dtos | 1.0.4 | DTOs de relacionamento | - |

---

## ✅ CHECKLIST DE DEPLOY PARA PRODUÇÃO

- [ ] Testes unitários passando
- [ ] Testes de integração passando
- [ ] Code review aprovado
- [ ] INSERTs CERIF executados (se aplicável)
- [ ] Documentação atualizada
- [ ] Tag criada no repositório
- [ ] Pipeline CI/CD executado com sucesso

---

**Última Atualização:** 29/03/2026 18:20  
**Documento Vivo:** Atualizar a cada sprint

---

## 📚 Referências

- [DVN - Documento de Visão Negocial](dvn.md)
- [HUs - Histórias de Usuário](hu-its/HUs/)
- [ITSs - Instruções Técnicas de Serviço](hu-its/ITSs/)
- [CERIF Model](https://eurocris.org/cerif/main-features-cerif)
- [Dublin Core Metadata](https://www.dublincore.org/specifications/dublin-core/)

