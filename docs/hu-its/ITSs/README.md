# LLAM Biblioteca — Instruções Técnicas de Serviço (ITSs)

## Visão Geral

Este diretório contém as **Instruções Técnicas de Serviço (ITSs)** do projeto LLAM Biblioteca. As ITSs complementam as HUs com detalhes técnicos de implementação.

---

## Estrutura

```
ITSs/
├── ITS-HU-00-aperfeicoamentos-ecossistema.md # Entidades a criar no Ecos
├── ITS-HU-01-modelagem-dados-biblioteca.md   # Baseline CERIF (ClassSchemes e Classes)
├── ITS-HU-02-configuracao-multi-tenant.md    # Multi-Tenant White Label
├── ITS-HU-03-cadastro-titulos.md             # Dublin Core para títulos
├── ITS-HU-04-gestao-exemplares.md            # Exemplares físicos
├── ITS-HU-05-cadastro-leitores.md            # Leitores (Person + Keycloak)
├── ITS-HU-06-modelo-transacional.md          # Transações de dupla entrada
├── ITS-HU-07-emprestimo-livros.md            # Fluxo de empréstimo
└── README.md                                  # Este arquivo
```

---

## Índice de ITSs

| ITS | HU Relacionada | Escopo |
|-----|----------------|--------|
| [ITS-HU-00](ITS-HU-00-aperfeicoamentos-ecossistema.md) | Pré-requisito | Entidades a criar no Ecos (Person_OrganizationUnit, etc.) |
| [ITS-HU-01](ITS-HU-01-modelagem-dados-biblioteca.md) | HU-01 | Baseline CERIF: 22 ClassSchemes + 70+ Classes |
| [ITS-HU-02](ITS-HU-02-configuracao-multi-tenant.md) | HU-02 | OrganizationUnit como tenant, configurações |
| [ITS-HU-03](ITS-HU-03-cadastro-titulos.md) | HU-03 | Dublin Core para metadados bibliográficos |
| [ITS-HU-04](ITS-HU-04-gestao-exemplares.md) | HU-04 | Class como exemplar, vinculação a títulos |
| [ITS-HU-05](ITS-HU-05-cadastro-leitores.md) | HU-05 | Person como leitor, integração Keycloak |
| [ITS-HU-06](ITS-HU-06-modelo-transacional.md) | HU-06 | Transações de dupla entrada, Kafka |
| [ITS-HU-07](ITS-HU-07-emprestimo-livros.md) | HU-07 | Fluxo completo de empréstimo |

---

## ClassSchemes do LLAM Biblioteca

### Faixa Reservada: 400-499

| ClassScheme | Constant | Descrição |
|-------------|----------|-----------|
| Estrutura do Acervo | `LIB_TITULO` | Títulos bibliográficos |
| | `LIB_EXEMPLAR` | Exemplares físicos |
| | `LIB_CATEGORIA` | Categorias dinâmicas |
| | `LIB_FORMATO` | Formatos (livro, ebook, PDF) |
| Estados | `LIB_STATUS_TITULO` | Status do título |
| | `LIB_STATUS_EXEMPLAR` | Status do exemplar |
| | `LIB_STATUS_LEITOR` | Status do leitor |
| | `LIB_STATUS_EMPRESTIMO` | Status do empréstimo |
| Transações | `LIB_TIPO_TRANSACAO` | Tipos de transação |
| | `LIB_TIPO_CONTA` | Tipos de conta |
| | `LIB_TIPO_LANCAMENTO` | Débito/Crédito |
| Relações | `LIB_RELACAO_TITULO_EXEMPLAR` | Título → Exemplar |
| | `LIB_RELACAO_TITULO_CATEGORIA` | Título → Categoria |
| | `LIB_RELACAO_LEITOR_TENANT` | Leitor → Tenant |
| | `LIB_RELACAO_EMPRESTIMO` | Empréstimo → Atores |
| Multi-Tenant | `LIB_TENANT` | Tenants (bibliotecas) |
| | `LIB_CONFIG_TENANT` | Configurações por tenant |
| | `LIB_PLANO_ASSINATURA` | Planos de assinatura |
| Financeiro | `LIB_TIPO_MULTA` | Tipos de multa |
| | `LIB_STATUS_MULTA` | Status da multa |
| Permissões | `LIB_PERFIL_USUARIO` | Perfis de usuário |
| | `LIB_ACAO` | Ações permitidas |

**Total: 22 ClassSchemes**

---

## Backends CERIF Utilizados

| Backend | Uso no LLAM Biblioteca |
|---------|------------------------|
| `class-backend` | ClassSchemes, Classes, ClassClass, ClassValue* |
| `dublin-core-backend` | Metadados de títulos (DublinCore*) |
| `transaction-backend` | Transações de empréstimo/devolução |
| `person-backend` | Leitores (Person, PersonValue*) |
| `organization-unit-backend` | Tenants (OrganizationUnit*) |
| `address-backend` | Endereços de leitores |
| `language-backend` | Idiomas dos títulos |

---

## Ordem de Execução dos Scripts

### 1. Baseline CERIF (ITS-HU-01)

```bash
# Executar primeiro - cria todos os ClassSchemes e Classes
mysql -u root -p < ITS-HU-01-baseline-desenvolvimento.sql
mysql -u root -p < ITS-HU-01-baseline-homologacao.sql
mysql -u root -p < ITS-HU-01-baseline-producao.sql
```

### 2. Verificação

```sql
-- Verificar ClassSchemes criados
SELECT COUNT(*) FROM ClassScheme WHERE Constant LIKE 'LIB_%';
-- Esperado: 22

-- Verificar Classes criadas
SELECT COUNT(*) FROM Class c
JOIN ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId
WHERE cs.Constant LIKE 'LIB_%';
-- Esperado: 70+
```

---

## Padrão de IDs

Seguindo o padrão LLAM (como no SCPA):

- **ClassScheme e Class usam auto-increment** (não especificar IDs)
- **Resolver IDs via SELECT usando Constant**
- **Usar variáveis @id_* para referenciar nos INSERTs**

```sql
-- Exemplo de resolução de ID
SET @id_transacao_emprestimo := (
    SELECT c.ClassId FROM Class c 
    JOIN ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_TIPO_TRANSACAO' 
    AND c.Constant = 'TRANSACAO_EMPRESTIMO' LIMIT 1
);
```

---

## Referências

- [HUs do projeto](../HUs/)
- [DVN - Documento de Visão Negocial](../../dvn.md)
- [ITS-HU-05 do SCPA](../../../../SCPA/docs/hu_its/ITSs/ITS-HU-05-modelagem-dados-scpa.md) (modelo de referência)

---

*Documento gerado em Março/2026 — LLAM Tecnologia do Brasil Ltda*
