# ITS-HU-01 — Modelagem de Dados LLAM Biblioteca (Baseline CERIF)

Escopo: especificação técnica da HU-01 cobrindo o baseline CERIF do LLAM Biblioteca - todos os ClassSchemes e Classifications necessários para o sistema funcionar.

Relacionado:
- HU: `../HUs/hu-01-modelagem-dados-biblioteca.md`
- HU: `../HUs/hu-03-cadastro-titulos.md`
- HU: `../HUs/hu-06-modelo-transacional.md`

---

## 1. Arquitetura de Classificação Multi-Sistema LLAM

### 1.1 Faixas de ClassSchemeId por Sistema

| Sistema | Faixa ClassSchemeId | Prefixo URI |
|---------|---------------------|-------------|
| Core CERIF | 1-99 | `urn:cerif:classscheme:*` |
| MediaVitae | 100-199 | `urn:llam:classscheme:mediavitae:*` |
| SCPA | 200-299 | `urn:llam:classscheme:scpa:*` |
| LIMS | 300-399 | `urn:llam:classscheme:lims:*` |
| **Biblioteca** | **400-499** | `urn:llam:classscheme:biblioteca:*` |
| Sign | 500-599 | `urn:llam:classscheme:sign:*` |
| Reserva | 600+ | - |

---

## 2. Pré-requisitos CERIF (DML) - Baseline LLAM Biblioteca

> **IMPORTANTE:** Os INSERTs abaixo devem ser executados **ANTES** de usar qualquer funcionalidade do LLAM Biblioteca. Este é o baseline obrigatório. Execute no ambiente correspondente.

---

### 2.1 DESENVOLVIMENTO

```sql
-- ============================================================================
-- BASELINE LLAM BIBLIOTECA - DESENVOLVIMENTO
-- Executar em: classification_desenvolvimento
-- ============================================================================

-- 1. Registrar sistema BIBLIOTECA no ClassScheme raiz
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, 'BIBLIOTECA', 'urn:llam:class:sistema:biblioteca'
FROM classification_desenvolvimento.ClassScheme cs
WHERE cs.Constant = 'SISTEMA_LLAM';

-- ============================================================================
-- 2. ClassSchemes principais do LLAM Biblioteca
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.ClassScheme (Constant, URI) VALUES
-- Estrutura do Acervo
('LIB_TITULO', 'urn:llam:classscheme:biblioteca:titulo'),
('LIB_EXEMPLAR', 'urn:llam:classscheme:biblioteca:exemplar'),
('LIB_CATEGORIA', 'urn:llam:classscheme:biblioteca:categoria'),
('LIB_FORMATO', 'urn:llam:classscheme:biblioteca:formato'),

-- Estados e Status
('LIB_STATUS_TITULO', 'urn:llam:classscheme:biblioteca:status_titulo'),
('LIB_STATUS_EXEMPLAR', 'urn:llam:classscheme:biblioteca:status_exemplar'),
('LIB_STATUS_LEITOR', 'urn:llam:classscheme:biblioteca:status_leitor'),
('LIB_STATUS_EMPRESTIMO', 'urn:llam:classscheme:biblioteca:status_emprestimo'),

-- Transações
('LIB_TIPO_TRANSACAO', 'urn:llam:classscheme:biblioteca:tipo_transacao'),
('LIB_TIPO_CONTA', 'urn:llam:classscheme:biblioteca:tipo_conta'),
('LIB_TIPO_LANCAMENTO', 'urn:llam:classscheme:biblioteca:tipo_lancamento'),

-- Relações
('LIB_RELACAO_TITULO_EXEMPLAR', 'urn:llam:classscheme:biblioteca:rel_titulo_exemplar'),
('LIB_RELACAO_TITULO_CATEGORIA', 'urn:llam:classscheme:biblioteca:rel_titulo_categoria'),
('LIB_RELACAO_LEITOR_TENANT', 'urn:llam:classscheme:biblioteca:rel_leitor_tenant'),
('LIB_RELACAO_EMPRESTIMO', 'urn:llam:classscheme:biblioteca:rel_emprestimo'),
('LIB_RELACAO_OU', 'urn:llam:classscheme:biblioteca:rel_organizationunit'),

-- Multi-Tenant
('LIB_TENANT', 'urn:llam:classscheme:biblioteca:tenant'),
('LIB_CONFIG_TENANT', 'urn:llam:classscheme:biblioteca:config_tenant'),
('LIB_PLANO_ASSINATURA', 'urn:llam:classscheme:biblioteca:plano_assinatura'),

-- Financeiro
('LIB_TIPO_MULTA', 'urn:llam:classscheme:biblioteca:tipo_multa'),
('LIB_STATUS_MULTA', 'urn:llam:classscheme:biblioteca:status_multa'),

-- Perfis e Permissões
('LIB_PERFIL_USUARIO', 'urn:llam:classscheme:biblioteca:perfil_usuario'),
('LIB_ACAO', 'urn:llam:classscheme:biblioteca:acao');

-- ============================================================================
-- 3. Classifications - Formatos de Título (LIB_FORMATO)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'FORMATO_LIVRO' AS Constant, 'urn:llam:class:biblioteca:formato:livro' AS URI UNION ALL
    SELECT 'FORMATO_REVISTA', 'urn:llam:class:biblioteca:formato:revista' UNION ALL
    SELECT 'FORMATO_PERIODICO', 'urn:llam:class:biblioteca:formato:periodico' UNION ALL
    SELECT 'FORMATO_TESE', 'urn:llam:class:biblioteca:formato:tese' UNION ALL
    SELECT 'FORMATO_DISSERTACAO', 'urn:llam:class:biblioteca:formato:dissertacao' UNION ALL
    SELECT 'FORMATO_ARTIGO', 'urn:llam:class:biblioteca:formato:artigo' UNION ALL
    SELECT 'FORMATO_EBOOK', 'urn:llam:class:biblioteca:formato:ebook' UNION ALL
    SELECT 'FORMATO_AUDIOBOOK', 'urn:llam:class:biblioteca:formato:audiobook' UNION ALL
    SELECT 'FORMATO_PDF', 'urn:llam:class:biblioteca:formato:pdf'
) v WHERE cs.Constant = 'LIB_FORMATO';

-- ============================================================================
-- 4. Classifications - Status de Título (LIB_STATUS_TITULO)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'TITULO_ATIVO' AS Constant, 'urn:llam:class:biblioteca:status_titulo:ativo' AS URI UNION ALL
    SELECT 'TITULO_INATIVO', 'urn:llam:class:biblioteca:status_titulo:inativo' UNION ALL
    SELECT 'TITULO_DESCONTINUADO', 'urn:llam:class:biblioteca:status_titulo:descontinuado'
) v WHERE cs.Constant = 'LIB_STATUS_TITULO';

-- ============================================================================
-- 5. Classifications - Status de Exemplar (LIB_STATUS_EXEMPLAR)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'EXEMPLAR_DISPONIVEL' AS Constant, 'urn:llam:class:biblioteca:status_exemplar:disponivel' AS URI UNION ALL
    SELECT 'EXEMPLAR_EMPRESTADO', 'urn:llam:class:biblioteca:status_exemplar:emprestado' UNION ALL
    SELECT 'EXEMPLAR_RESERVADO', 'urn:llam:class:biblioteca:status_exemplar:reservado' UNION ALL
    SELECT 'EXEMPLAR_MANUTENCAO', 'urn:llam:class:biblioteca:status_exemplar:manutencao' UNION ALL
    SELECT 'EXEMPLAR_EXTRAVIADO', 'urn:llam:class:biblioteca:status_exemplar:extraviado' UNION ALL
    SELECT 'EXEMPLAR_BAIXADO', 'urn:llam:class:biblioteca:status_exemplar:baixado'
) v WHERE cs.Constant = 'LIB_STATUS_EXEMPLAR';

-- ============================================================================
-- 6. Classifications - Status de Leitor (LIB_STATUS_LEITOR)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'LEITOR_ATIVO' AS Constant, 'urn:llam:class:biblioteca:status_leitor:ativo' AS URI UNION ALL
    SELECT 'LEITOR_INATIVO', 'urn:llam:class:biblioteca:status_leitor:inativo' UNION ALL
    SELECT 'LEITOR_SUSPENSO', 'urn:llam:class:biblioteca:status_leitor:suspenso' UNION ALL
    SELECT 'LEITOR_BLOQUEADO', 'urn:llam:class:biblioteca:status_leitor:bloqueado' UNION ALL
    SELECT 'LEITOR_INADIMPLENTE', 'urn:llam:class:biblioteca:status_leitor:inadimplente'
) v WHERE cs.Constant = 'LIB_STATUS_LEITOR';

-- ============================================================================
-- 7. Classifications - Status de Empréstimo (LIB_STATUS_EMPRESTIMO)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'EMPRESTIMO_ATIVO' AS Constant, 'urn:llam:class:biblioteca:status_emprestimo:ativo' AS URI UNION ALL
    SELECT 'EMPRESTIMO_DEVOLVIDO', 'urn:llam:class:biblioteca:status_emprestimo:devolvido' UNION ALL
    SELECT 'EMPRESTIMO_ATRASADO', 'urn:llam:class:biblioteca:status_emprestimo:atrasado' UNION ALL
    SELECT 'EMPRESTIMO_RENOVADO', 'urn:llam:class:biblioteca:status_emprestimo:renovado' UNION ALL
    SELECT 'EMPRESTIMO_CANCELADO', 'urn:llam:class:biblioteca:status_emprestimo:cancelado'
) v WHERE cs.Constant = 'LIB_STATUS_EMPRESTIMO';

-- ============================================================================
-- 8. Classifications - Tipos de Transação (LIB_TIPO_TRANSACAO) - CORE
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'TRANSACAO_EMPRESTIMO' AS Constant, 'urn:llam:class:biblioteca:tipo_transacao:emprestimo' AS URI UNION ALL
    SELECT 'TRANSACAO_DEVOLUCAO', 'urn:llam:class:biblioteca:tipo_transacao:devolucao' UNION ALL
    SELECT 'TRANSACAO_RENOVACAO', 'urn:llam:class:biblioteca:tipo_transacao:renovacao' UNION ALL
    SELECT 'TRANSACAO_RESERVA', 'urn:llam:class:biblioteca:tipo_transacao:reserva' UNION ALL
    SELECT 'TRANSACAO_CANCELAMENTO_RESERVA', 'urn:llam:class:biblioteca:tipo_transacao:cancelamento_reserva' UNION ALL
    SELECT 'TRANSACAO_VENDA_DIGITAL', 'urn:llam:class:biblioteca:tipo_transacao:venda_digital' UNION ALL
    SELECT 'TRANSACAO_MULTA', 'urn:llam:class:biblioteca:tipo_transacao:multa' UNION ALL
    SELECT 'TRANSACAO_PAGAMENTO_MULTA', 'urn:llam:class:biblioteca:tipo_transacao:pagamento_multa' UNION ALL
    SELECT 'TRANSACAO_BAIXA_EXEMPLAR', 'urn:llam:class:biblioteca:tipo_transacao:baixa_exemplar' UNION ALL
    SELECT 'TRANSACAO_ENTRADA_EXEMPLAR', 'urn:llam:class:biblioteca:tipo_transacao:entrada_exemplar'
) v WHERE cs.Constant = 'LIB_TIPO_TRANSACAO';

-- ============================================================================
-- 9. Classifications - Tipos de Conta (LIB_TIPO_CONTA)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'CONTA_BIBLIOTECA' AS Constant, 'urn:llam:class:biblioteca:tipo_conta:biblioteca' AS URI UNION ALL
    SELECT 'CONTA_LEITOR', 'urn:llam:class:biblioteca:tipo_conta:leitor' UNION ALL
    SELECT 'CONTA_DIGITAL', 'urn:llam:class:biblioteca:tipo_conta:digital' UNION ALL
    SELECT 'CONTA_FINANCEIRA', 'urn:llam:class:biblioteca:tipo_conta:financeira'
) v WHERE cs.Constant = 'LIB_TIPO_CONTA';

-- ============================================================================
-- 10. Classifications - Tipos de Lançamento (LIB_TIPO_LANCAMENTO)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'LANCAMENTO_DEBITO' AS Constant, 'urn:llam:class:biblioteca:tipo_lancamento:debito' AS URI UNION ALL
    SELECT 'LANCAMENTO_CREDITO', 'urn:llam:class:biblioteca:tipo_lancamento:credito'
) v WHERE cs.Constant = 'LIB_TIPO_LANCAMENTO';

-- ============================================================================
-- 11. Classifications - Relação Título-Exemplar (LIB_RELACAO_TITULO_EXEMPLAR)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'REL_TITULO_EXEMPLAR_FISICO' AS Constant, 'urn:llam:class:biblioteca:rel_titulo_exemplar:fisico' AS URI UNION ALL
    SELECT 'REL_TITULO_EXEMPLAR_DIGITAL', 'urn:llam:class:biblioteca:rel_titulo_exemplar:digital'
) v WHERE cs.Constant = 'LIB_RELACAO_TITULO_EXEMPLAR';

-- ============================================================================
-- 12. Classifications - Relação Empréstimo (LIB_RELACAO_EMPRESTIMO)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'REL_EMPRESTIMO_LEITOR' AS Constant, 'urn:llam:class:biblioteca:rel_emprestimo:leitor' AS URI UNION ALL
    SELECT 'REL_EMPRESTIMO_EXEMPLAR', 'urn:llam:class:biblioteca:rel_emprestimo:exemplar' UNION ALL
    SELECT 'REL_EMPRESTIMO_BIBLIOTECARIO', 'urn:llam:class:biblioteca:rel_emprestimo:bibliotecario'
) v WHERE cs.Constant = 'LIB_RELACAO_EMPRESTIMO';

-- ============================================================================
-- 12.1 Classifications - Relação OrganizationUnit (LIB_RELACAO_OU)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'REL_OU_FILHO' AS Constant, 'urn:llam:class:biblioteca:rel_ou:filho' AS URI UNION ALL
    SELECT 'REL_OU_PARCEIRO', 'urn:llam:class:biblioteca:rel_ou:parceiro'
) v WHERE cs.Constant = 'LIB_RELACAO_OU';

-- ============================================================================
-- 13. Classifications - Configurações de Tenant (LIB_CONFIG_TENANT)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'CONFIG_PRAZO_EMPRESTIMO' AS Constant, 'urn:llam:class:biblioteca:config_tenant:prazo_emprestimo' AS URI UNION ALL
    SELECT 'CONFIG_LIMITE_EMPRESTIMOS', 'urn:llam:class:biblioteca:config_tenant:limite_emprestimos' UNION ALL
    SELECT 'CONFIG_LIMITE_RENOVACOES', 'urn:llam:class:biblioteca:config_tenant:limite_renovacoes' UNION ALL
    SELECT 'CONFIG_VALOR_MULTA_DIA', 'urn:llam:class:biblioteca:config_tenant:valor_multa_dia' UNION ALL
    SELECT 'CONFIG_PERMITE_RESERVA', 'urn:llam:class:biblioteca:config_tenant:permite_reserva' UNION ALL
    SELECT 'CONFIG_PERMITE_VENDA_DIGITAL', 'urn:llam:class:biblioteca:config_tenant:permite_venda_digital' UNION ALL
    SELECT 'CONFIG_TEMA_CORES', 'urn:llam:class:biblioteca:config_tenant:tema_cores' UNION ALL
    SELECT 'CONFIG_LOGO_URL', 'urn:llam:class:biblioteca:config_tenant:logo_url' UNION ALL
    SELECT 'CONFIG_DOMINIO_PERSONALIZADO', 'urn:llam:class:biblioteca:config_tenant:dominio_personalizado'
) v WHERE cs.Constant = 'LIB_CONFIG_TENANT';

-- ============================================================================
-- 14. Classifications - Planos de Assinatura (LIB_PLANO_ASSINATURA)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'PLANO_GRATUITO' AS Constant, 'urn:llam:class:biblioteca:plano:gratuito' AS URI UNION ALL
    SELECT 'PLANO_BASICO', 'urn:llam:class:biblioteca:plano:basico' UNION ALL
    SELECT 'PLANO_PREMIUM', 'urn:llam:class:biblioteca:plano:premium' UNION ALL
    SELECT 'PLANO_INSTITUCIONAL', 'urn:llam:class:biblioteca:plano:institucional'
) v WHERE cs.Constant = 'LIB_PLANO_ASSINATURA';

-- ============================================================================
-- 15. Classifications - Tipos de Multa (LIB_TIPO_MULTA)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'MULTA_ATRASO' AS Constant, 'urn:llam:class:biblioteca:tipo_multa:atraso' AS URI UNION ALL
    SELECT 'MULTA_EXTRAVIO', 'urn:llam:class:biblioteca:tipo_multa:extravio' UNION ALL
    SELECT 'MULTA_DANO', 'urn:llam:class:biblioteca:tipo_multa:dano'
) v WHERE cs.Constant = 'LIB_TIPO_MULTA';

-- ============================================================================
-- 16. Classifications - Status de Multa (LIB_STATUS_MULTA)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'MULTA_PENDENTE' AS Constant, 'urn:llam:class:biblioteca:status_multa:pendente' AS URI UNION ALL
    SELECT 'MULTA_PAGA', 'urn:llam:class:biblioteca:status_multa:paga' UNION ALL
    SELECT 'MULTA_ISENTA', 'urn:llam:class:biblioteca:status_multa:isenta' UNION ALL
    SELECT 'MULTA_CANCELADA', 'urn:llam:class:biblioteca:status_multa:cancelada'
) v WHERE cs.Constant = 'LIB_STATUS_MULTA';

-- ============================================================================
-- 17. Classifications - Perfis de Usuário (LIB_PERFIL_USUARIO)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'PERFIL_ADMIN_PLATAFORMA' AS Constant, 'urn:llam:class:biblioteca:perfil:admin_plataforma' AS URI UNION ALL
    SELECT 'PERFIL_ADMIN_TENANT', 'urn:llam:class:biblioteca:perfil:admin_tenant' UNION ALL
    SELECT 'PERFIL_BIBLIOTECARIO', 'urn:llam:class:biblioteca:perfil:bibliotecario' UNION ALL
    SELECT 'PERFIL_ATENDENTE', 'urn:llam:class:biblioteca:perfil:atendente' UNION ALL
    SELECT 'PERFIL_LEITOR', 'urn:llam:class:biblioteca:perfil:leitor'
) v WHERE cs.Constant = 'LIB_PERFIL_USUARIO';

-- ============================================================================
-- 18. Classifications - Ações (LIB_ACAO)
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
CROSS JOIN (
    SELECT 'ACAO_VISUALIZAR' AS Constant, 'urn:llam:class:biblioteca:acao:visualizar' AS URI UNION ALL
    SELECT 'ACAO_CRIAR', 'urn:llam:class:biblioteca:acao:criar' UNION ALL
    SELECT 'ACAO_EDITAR', 'urn:llam:class:biblioteca:acao:editar' UNION ALL
    SELECT 'ACAO_EXCLUIR', 'urn:llam:class:biblioteca:acao:excluir' UNION ALL
    SELECT 'ACAO_EMPRESTAR', 'urn:llam:class:biblioteca:acao:emprestar' UNION ALL
    SELECT 'ACAO_DEVOLVER', 'urn:llam:class:biblioteca:acao:devolver' UNION ALL
    SELECT 'ACAO_RENOVAR', 'urn:llam:class:biblioteca:acao:renovar' UNION ALL
    SELECT 'ACAO_RESERVAR', 'urn:llam:class:biblioteca:acao:reservar' UNION ALL
    SELECT 'ACAO_COMPRAR', 'urn:llam:class:biblioteca:acao:comprar' UNION ALL
    SELECT 'ACAO_CONFIGURAR', 'urn:llam:class:biblioteca:acao:configurar' UNION ALL
    SELECT 'ACAO_RELATORIO', 'urn:llam:class:biblioteca:acao:relatorio'
) v WHERE cs.Constant = 'LIB_ACAO';

-- ============================================================================
-- 19. ClassScheme - Atributos EVA de Título (LIB_ATRIBUTO_TITULO)
-- Para uso com DublinCoreValueString, DublinCoreValueInteger, DublinCoreValueDate
-- ============================================================================
INSERT IGNORE INTO classification_desenvolvimento.ClassScheme (Constant, URI) VALUES
('LIB_ATRIBUTO_TITULO', 'urn:llam:classscheme:biblioteca:atributo_titulo');

-- Classifications - Atributos de Título (para tabelas DublinCoreValue*)
INSERT IGNORE INTO classification_desenvolvimento.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_desenvolvimento.ClassScheme cs
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

---

### 2.2 HOMOLOGAÇÃO

```sql
-- ============================================================================
-- BASELINE LLAM BIBLIOTECA - HOMOLOGAÇÃO
-- Executar em: classification_homologacao
-- ============================================================================

-- 1. Registrar sistema BIBLIOTECA no ClassScheme raiz
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, 'BIBLIOTECA', 'urn:llam:class:sistema:biblioteca'
FROM classification_homologacao.ClassScheme cs
WHERE cs.Constant = 'SISTEMA_LLAM';

-- ============================================================================
-- 2. ClassSchemes principais do LLAM Biblioteca
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.ClassScheme (Constant, URI) VALUES
-- Estrutura do Acervo
('LIB_TITULO', 'urn:llam:classscheme:biblioteca:titulo'),
('LIB_EXEMPLAR', 'urn:llam:classscheme:biblioteca:exemplar'),
('LIB_CATEGORIA', 'urn:llam:classscheme:biblioteca:categoria'),
('LIB_FORMATO', 'urn:llam:classscheme:biblioteca:formato'),

-- Estados e Status
('LIB_STATUS_TITULO', 'urn:llam:classscheme:biblioteca:status_titulo'),
('LIB_STATUS_EXEMPLAR', 'urn:llam:classscheme:biblioteca:status_exemplar'),
('LIB_STATUS_LEITOR', 'urn:llam:classscheme:biblioteca:status_leitor'),
('LIB_STATUS_EMPRESTIMO', 'urn:llam:classscheme:biblioteca:status_emprestimo'),

-- Transações
('LIB_TIPO_TRANSACAO', 'urn:llam:classscheme:biblioteca:tipo_transacao'),
('LIB_TIPO_CONTA', 'urn:llam:classscheme:biblioteca:tipo_conta'),
('LIB_TIPO_LANCAMENTO', 'urn:llam:classscheme:biblioteca:tipo_lancamento'),

-- Relações
('LIB_RELACAO_TITULO_EXEMPLAR', 'urn:llam:classscheme:biblioteca:rel_titulo_exemplar'),
('LIB_RELACAO_TITULO_CATEGORIA', 'urn:llam:classscheme:biblioteca:rel_titulo_categoria'),
('LIB_RELACAO_LEITOR_TENANT', 'urn:llam:classscheme:biblioteca:rel_leitor_tenant'),
('LIB_RELACAO_EMPRESTIMO', 'urn:llam:classscheme:biblioteca:rel_emprestimo'),
('LIB_RELACAO_OU', 'urn:llam:classscheme:biblioteca:rel_organizationunit'),

-- Multi-Tenant
('LIB_TENANT', 'urn:llam:classscheme:biblioteca:tenant'),
('LIB_CONFIG_TENANT', 'urn:llam:classscheme:biblioteca:config_tenant'),
('LIB_PLANO_ASSINATURA', 'urn:llam:classscheme:biblioteca:plano_assinatura'),

-- Financeiro
('LIB_TIPO_MULTA', 'urn:llam:classscheme:biblioteca:tipo_multa'),
('LIB_STATUS_MULTA', 'urn:llam:classscheme:biblioteca:status_multa'),

-- Perfis e Permissões
('LIB_PERFIL_USUARIO', 'urn:llam:classscheme:biblioteca:perfil_usuario'),
('LIB_ACAO', 'urn:llam:classscheme:biblioteca:acao');

-- ============================================================================
-- 3. Classifications - Formatos de Título (LIB_FORMATO)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'FORMATO_LIVRO' AS Constant, 'urn:llam:class:biblioteca:formato:livro' AS URI UNION ALL
    SELECT 'FORMATO_REVISTA', 'urn:llam:class:biblioteca:formato:revista' UNION ALL
    SELECT 'FORMATO_PERIODICO', 'urn:llam:class:biblioteca:formato:periodico' UNION ALL
    SELECT 'FORMATO_TESE', 'urn:llam:class:biblioteca:formato:tese' UNION ALL
    SELECT 'FORMATO_DISSERTACAO', 'urn:llam:class:biblioteca:formato:dissertacao' UNION ALL
    SELECT 'FORMATO_ARTIGO', 'urn:llam:class:biblioteca:formato:artigo' UNION ALL
    SELECT 'FORMATO_EBOOK', 'urn:llam:class:biblioteca:formato:ebook' UNION ALL
    SELECT 'FORMATO_AUDIOBOOK', 'urn:llam:class:biblioteca:formato:audiobook' UNION ALL
    SELECT 'FORMATO_PDF', 'urn:llam:class:biblioteca:formato:pdf'
) v WHERE cs.Constant = 'LIB_FORMATO';

-- ============================================================================
-- 4. Classifications - Status de Título (LIB_STATUS_TITULO)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'TITULO_ATIVO' AS Constant, 'urn:llam:class:biblioteca:status_titulo:ativo' AS URI UNION ALL
    SELECT 'TITULO_INATIVO', 'urn:llam:class:biblioteca:status_titulo:inativo' UNION ALL
    SELECT 'TITULO_DESCONTINUADO', 'urn:llam:class:biblioteca:status_titulo:descontinuado'
) v WHERE cs.Constant = 'LIB_STATUS_TITULO';

-- ============================================================================
-- 5. Classifications - Status de Exemplar (LIB_STATUS_EXEMPLAR)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'EXEMPLAR_DISPONIVEL' AS Constant, 'urn:llam:class:biblioteca:status_exemplar:disponivel' AS URI UNION ALL
    SELECT 'EXEMPLAR_EMPRESTADO', 'urn:llam:class:biblioteca:status_exemplar:emprestado' UNION ALL
    SELECT 'EXEMPLAR_RESERVADO', 'urn:llam:class:biblioteca:status_exemplar:reservado' UNION ALL
    SELECT 'EXEMPLAR_MANUTENCAO', 'urn:llam:class:biblioteca:status_exemplar:manutencao' UNION ALL
    SELECT 'EXEMPLAR_EXTRAVIADO', 'urn:llam:class:biblioteca:status_exemplar:extraviado' UNION ALL
    SELECT 'EXEMPLAR_BAIXADO', 'urn:llam:class:biblioteca:status_exemplar:baixado'
) v WHERE cs.Constant = 'LIB_STATUS_EXEMPLAR';

-- ============================================================================
-- 6. Classifications - Status de Leitor (LIB_STATUS_LEITOR)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'LEITOR_ATIVO' AS Constant, 'urn:llam:class:biblioteca:status_leitor:ativo' AS URI UNION ALL
    SELECT 'LEITOR_INATIVO', 'urn:llam:class:biblioteca:status_leitor:inativo' UNION ALL
    SELECT 'LEITOR_SUSPENSO', 'urn:llam:class:biblioteca:status_leitor:suspenso' UNION ALL
    SELECT 'LEITOR_BLOQUEADO', 'urn:llam:class:biblioteca:status_leitor:bloqueado' UNION ALL
    SELECT 'LEITOR_INADIMPLENTE', 'urn:llam:class:biblioteca:status_leitor:inadimplente'
) v WHERE cs.Constant = 'LIB_STATUS_LEITOR';

-- ============================================================================
-- 7. Classifications - Status de Empréstimo (LIB_STATUS_EMPRESTIMO)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'EMPRESTIMO_ATIVO' AS Constant, 'urn:llam:class:biblioteca:status_emprestimo:ativo' AS URI UNION ALL
    SELECT 'EMPRESTIMO_DEVOLVIDO', 'urn:llam:class:biblioteca:status_emprestimo:devolvido' UNION ALL
    SELECT 'EMPRESTIMO_ATRASADO', 'urn:llam:class:biblioteca:status_emprestimo:atrasado' UNION ALL
    SELECT 'EMPRESTIMO_RENOVADO', 'urn:llam:class:biblioteca:status_emprestimo:renovado' UNION ALL
    SELECT 'EMPRESTIMO_CANCELADO', 'urn:llam:class:biblioteca:status_emprestimo:cancelado'
) v WHERE cs.Constant = 'LIB_STATUS_EMPRESTIMO';

-- ============================================================================
-- 8. Classifications - Tipos de Transação (LIB_TIPO_TRANSACAO) - CORE
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'TRANSACAO_EMPRESTIMO' AS Constant, 'urn:llam:class:biblioteca:tipo_transacao:emprestimo' AS URI UNION ALL
    SELECT 'TRANSACAO_DEVOLUCAO', 'urn:llam:class:biblioteca:tipo_transacao:devolucao' UNION ALL
    SELECT 'TRANSACAO_RENOVACAO', 'urn:llam:class:biblioteca:tipo_transacao:renovacao' UNION ALL
    SELECT 'TRANSACAO_RESERVA', 'urn:llam:class:biblioteca:tipo_transacao:reserva' UNION ALL
    SELECT 'TRANSACAO_CANCELAMENTO_RESERVA', 'urn:llam:class:biblioteca:tipo_transacao:cancelamento_reserva' UNION ALL
    SELECT 'TRANSACAO_VENDA_DIGITAL', 'urn:llam:class:biblioteca:tipo_transacao:venda_digital' UNION ALL
    SELECT 'TRANSACAO_MULTA', 'urn:llam:class:biblioteca:tipo_transacao:multa' UNION ALL
    SELECT 'TRANSACAO_PAGAMENTO_MULTA', 'urn:llam:class:biblioteca:tipo_transacao:pagamento_multa' UNION ALL
    SELECT 'TRANSACAO_BAIXA_EXEMPLAR', 'urn:llam:class:biblioteca:tipo_transacao:baixa_exemplar' UNION ALL
    SELECT 'TRANSACAO_ENTRADA_EXEMPLAR', 'urn:llam:class:biblioteca:tipo_transacao:entrada_exemplar'
) v WHERE cs.Constant = 'LIB_TIPO_TRANSACAO';

-- ============================================================================
-- 9. Classifications - Tipos de Conta (LIB_TIPO_CONTA)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'CONTA_BIBLIOTECA' AS Constant, 'urn:llam:class:biblioteca:tipo_conta:biblioteca' AS URI UNION ALL
    SELECT 'CONTA_LEITOR', 'urn:llam:class:biblioteca:tipo_conta:leitor' UNION ALL
    SELECT 'CONTA_DIGITAL', 'urn:llam:class:biblioteca:tipo_conta:digital' UNION ALL
    SELECT 'CONTA_FINANCEIRA', 'urn:llam:class:biblioteca:tipo_conta:financeira'
) v WHERE cs.Constant = 'LIB_TIPO_CONTA';

-- ============================================================================
-- 10. Classifications - Tipos de Lançamento (LIB_TIPO_LANCAMENTO)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'LANCAMENTO_DEBITO' AS Constant, 'urn:llam:class:biblioteca:tipo_lancamento:debito' AS URI UNION ALL
    SELECT 'LANCAMENTO_CREDITO', 'urn:llam:class:biblioteca:tipo_lancamento:credito'
) v WHERE cs.Constant = 'LIB_TIPO_LANCAMENTO';

-- ============================================================================
-- 11. Classifications - Relação Título-Exemplar (LIB_RELACAO_TITULO_EXEMPLAR)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'REL_TITULO_EXEMPLAR_FISICO' AS Constant, 'urn:llam:class:biblioteca:rel_titulo_exemplar:fisico' AS URI UNION ALL
    SELECT 'REL_TITULO_EXEMPLAR_DIGITAL', 'urn:llam:class:biblioteca:rel_titulo_exemplar:digital'
) v WHERE cs.Constant = 'LIB_RELACAO_TITULO_EXEMPLAR';

-- ============================================================================
-- 12. Classifications - Relação Empréstimo (LIB_RELACAO_EMPRESTIMO)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'REL_EMPRESTIMO_LEITOR' AS Constant, 'urn:llam:class:biblioteca:rel_emprestimo:leitor' AS URI UNION ALL
    SELECT 'REL_EMPRESTIMO_EXEMPLAR', 'urn:llam:class:biblioteca:rel_emprestimo:exemplar' UNION ALL
    SELECT 'REL_EMPRESTIMO_BIBLIOTECARIO', 'urn:llam:class:biblioteca:rel_emprestimo:bibliotecario'
) v WHERE cs.Constant = 'LIB_RELACAO_EMPRESTIMO';

-- ============================================================================
-- 12.1 Classifications - Relação OrganizationUnit (LIB_RELACAO_OU)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'REL_OU_FILHO' AS Constant, 'urn:llam:class:biblioteca:rel_ou:filho' AS URI UNION ALL
    SELECT 'REL_OU_PARCEIRO', 'urn:llam:class:biblioteca:rel_ou:parceiro'
) v WHERE cs.Constant = 'LIB_RELACAO_OU';

-- ============================================================================
-- 13. Classifications - Configurações de Tenant (LIB_CONFIG_TENANT)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'CONFIG_PRAZO_EMPRESTIMO' AS Constant, 'urn:llam:class:biblioteca:config_tenant:prazo_emprestimo' AS URI UNION ALL
    SELECT 'CONFIG_LIMITE_EMPRESTIMOS', 'urn:llam:class:biblioteca:config_tenant:limite_emprestimos' UNION ALL
    SELECT 'CONFIG_LIMITE_RENOVACOES', 'urn:llam:class:biblioteca:config_tenant:limite_renovacoes' UNION ALL
    SELECT 'CONFIG_VALOR_MULTA_DIA', 'urn:llam:class:biblioteca:config_tenant:valor_multa_dia' UNION ALL
    SELECT 'CONFIG_PERMITE_RESERVA', 'urn:llam:class:biblioteca:config_tenant:permite_reserva' UNION ALL
    SELECT 'CONFIG_PERMITE_VENDA_DIGITAL', 'urn:llam:class:biblioteca:config_tenant:permite_venda_digital' UNION ALL
    SELECT 'CONFIG_TEMA_CORES', 'urn:llam:class:biblioteca:config_tenant:tema_cores' UNION ALL
    SELECT 'CONFIG_LOGO_URL', 'urn:llam:class:biblioteca:config_tenant:logo_url' UNION ALL
    SELECT 'CONFIG_DOMINIO_PERSONALIZADO', 'urn:llam:class:biblioteca:config_tenant:dominio_personalizado'
) v WHERE cs.Constant = 'LIB_CONFIG_TENANT';

-- ============================================================================
-- 14. Classifications - Planos de Assinatura (LIB_PLANO_ASSINATURA)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'PLANO_GRATUITO' AS Constant, 'urn:llam:class:biblioteca:plano:gratuito' AS URI UNION ALL
    SELECT 'PLANO_BASICO', 'urn:llam:class:biblioteca:plano:basico' UNION ALL
    SELECT 'PLANO_PREMIUM', 'urn:llam:class:biblioteca:plano:premium' UNION ALL
    SELECT 'PLANO_INSTITUCIONAL', 'urn:llam:class:biblioteca:plano:institucional'
) v WHERE cs.Constant = 'LIB_PLANO_ASSINATURA';

-- ============================================================================
-- 15. Classifications - Tipos de Multa (LIB_TIPO_MULTA)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'MULTA_ATRASO' AS Constant, 'urn:llam:class:biblioteca:tipo_multa:atraso' AS URI UNION ALL
    SELECT 'MULTA_EXTRAVIO', 'urn:llam:class:biblioteca:tipo_multa:extravio' UNION ALL
    SELECT 'MULTA_DANO', 'urn:llam:class:biblioteca:tipo_multa:dano'
) v WHERE cs.Constant = 'LIB_TIPO_MULTA';

-- ============================================================================
-- 16. Classifications - Status de Multa (LIB_STATUS_MULTA)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'MULTA_PENDENTE' AS Constant, 'urn:llam:class:biblioteca:status_multa:pendente' AS URI UNION ALL
    SELECT 'MULTA_PAGA', 'urn:llam:class:biblioteca:status_multa:paga' UNION ALL
    SELECT 'MULTA_ISENTA', 'urn:llam:class:biblioteca:status_multa:isenta' UNION ALL
    SELECT 'MULTA_CANCELADA', 'urn:llam:class:biblioteca:status_multa:cancelada'
) v WHERE cs.Constant = 'LIB_STATUS_MULTA';

-- ============================================================================
-- 17. Classifications - Perfis de Usuário (LIB_PERFIL_USUARIO)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'PERFIL_ADMIN_PLATAFORMA' AS Constant, 'urn:llam:class:biblioteca:perfil:admin_plataforma' AS URI UNION ALL
    SELECT 'PERFIL_ADMIN_TENANT', 'urn:llam:class:biblioteca:perfil:admin_tenant' UNION ALL
    SELECT 'PERFIL_BIBLIOTECARIO', 'urn:llam:class:biblioteca:perfil:bibliotecario' UNION ALL
    SELECT 'PERFIL_ATENDENTE', 'urn:llam:class:biblioteca:perfil:atendente' UNION ALL
    SELECT 'PERFIL_LEITOR', 'urn:llam:class:biblioteca:perfil:leitor'
) v WHERE cs.Constant = 'LIB_PERFIL_USUARIO';

-- ============================================================================
-- 18. Classifications - Ações (LIB_ACAO)
-- ============================================================================
INSERT IGNORE INTO classification_homologacao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_homologacao.ClassScheme cs
CROSS JOIN (
    SELECT 'ACAO_VISUALIZAR' AS Constant, 'urn:llam:class:biblioteca:acao:visualizar' AS URI UNION ALL
    SELECT 'ACAO_CRIAR', 'urn:llam:class:biblioteca:acao:criar' UNION ALL
    SELECT 'ACAO_EDITAR', 'urn:llam:class:biblioteca:acao:editar' UNION ALL
    SELECT 'ACAO_EXCLUIR', 'urn:llam:class:biblioteca:acao:excluir' UNION ALL
    SELECT 'ACAO_EMPRESTAR', 'urn:llam:class:biblioteca:acao:emprestar' UNION ALL
    SELECT 'ACAO_DEVOLVER', 'urn:llam:class:biblioteca:acao:devolver' UNION ALL
    SELECT 'ACAO_RENOVAR', 'urn:llam:class:biblioteca:acao:renovar' UNION ALL
    SELECT 'ACAO_RESERVAR', 'urn:llam:class:biblioteca:acao:reservar' UNION ALL
    SELECT 'ACAO_COMPRAR', 'urn:llam:class:biblioteca:acao:comprar' UNION ALL
    SELECT 'ACAO_CONFIGURAR', 'urn:llam:class:biblioteca:acao:configurar' UNION ALL
    SELECT 'ACAO_RELATORIO', 'urn:llam:class:biblioteca:acao:relatorio'
) v WHERE cs.Constant = 'LIB_ACAO';

-- ============================================================================
-- 19. ClassScheme - Atributos EVA de Título (LIB_ATRIBUTO_TITULO)
-- Para uso com DublinCoreValueString, DublinCoreValueInteger, DublinCoreValueDate
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

---

### 2.3 PRODUÇÃO

```sql
-- ============================================================================
-- BASELINE LLAM BIBLIOTECA - PRODUÇÃO
-- Executar em: classification_producao
-- ============================================================================

-- 1. Registrar sistema BIBLIOTECA no ClassScheme raiz
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, 'BIBLIOTECA', 'urn:llam:class:sistema:biblioteca'
FROM classification_producao.ClassScheme cs
WHERE cs.Constant = 'SISTEMA_LLAM';

-- ============================================================================
-- 2. ClassSchemes principais do LLAM Biblioteca
-- ============================================================================
INSERT IGNORE INTO classification_producao.ClassScheme (Constant, URI) VALUES
-- Estrutura do Acervo
('LIB_TITULO', 'urn:llam:classscheme:biblioteca:titulo'),
('LIB_EXEMPLAR', 'urn:llam:classscheme:biblioteca:exemplar'),
('LIB_CATEGORIA', 'urn:llam:classscheme:biblioteca:categoria'),
('LIB_FORMATO', 'urn:llam:classscheme:biblioteca:formato'),

-- Estados e Status
('LIB_STATUS_TITULO', 'urn:llam:classscheme:biblioteca:status_titulo'),
('LIB_STATUS_EXEMPLAR', 'urn:llam:classscheme:biblioteca:status_exemplar'),
('LIB_STATUS_LEITOR', 'urn:llam:classscheme:biblioteca:status_leitor'),
('LIB_STATUS_EMPRESTIMO', 'urn:llam:classscheme:biblioteca:status_emprestimo'),

-- Transações
('LIB_TIPO_TRANSACAO', 'urn:llam:classscheme:biblioteca:tipo_transacao'),
('LIB_TIPO_CONTA', 'urn:llam:classscheme:biblioteca:tipo_conta'),
('LIB_TIPO_LANCAMENTO', 'urn:llam:classscheme:biblioteca:tipo_lancamento'),

-- Relações
('LIB_RELACAO_TITULO_EXEMPLAR', 'urn:llam:classscheme:biblioteca:rel_titulo_exemplar'),
('LIB_RELACAO_TITULO_CATEGORIA', 'urn:llam:classscheme:biblioteca:rel_titulo_categoria'),
('LIB_RELACAO_LEITOR_TENANT', 'urn:llam:classscheme:biblioteca:rel_leitor_tenant'),
('LIB_RELACAO_EMPRESTIMO', 'urn:llam:classscheme:biblioteca:rel_emprestimo'),
('LIB_RELACAO_OU', 'urn:llam:classscheme:biblioteca:rel_organizationunit'),

-- Multi-Tenant
('LIB_TENANT', 'urn:llam:classscheme:biblioteca:tenant'),
('LIB_CONFIG_TENANT', 'urn:llam:classscheme:biblioteca:config_tenant'),
('LIB_PLANO_ASSINATURA', 'urn:llam:classscheme:biblioteca:plano_assinatura'),

-- Financeiro
('LIB_TIPO_MULTA', 'urn:llam:classscheme:biblioteca:tipo_multa'),
('LIB_STATUS_MULTA', 'urn:llam:classscheme:biblioteca:status_multa'),

-- Perfis e Permissões
('LIB_PERFIL_USUARIO', 'urn:llam:classscheme:biblioteca:perfil_usuario'),
('LIB_ACAO', 'urn:llam:classscheme:biblioteca:acao');

-- ============================================================================
-- 3. Classifications - Formatos de Título (LIB_FORMATO)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'FORMATO_LIVRO' AS Constant, 'urn:llam:class:biblioteca:formato:livro' AS URI UNION ALL
    SELECT 'FORMATO_REVISTA', 'urn:llam:class:biblioteca:formato:revista' UNION ALL
    SELECT 'FORMATO_PERIODICO', 'urn:llam:class:biblioteca:formato:periodico' UNION ALL
    SELECT 'FORMATO_TESE', 'urn:llam:class:biblioteca:formato:tese' UNION ALL
    SELECT 'FORMATO_DISSERTACAO', 'urn:llam:class:biblioteca:formato:dissertacao' UNION ALL
    SELECT 'FORMATO_ARTIGO', 'urn:llam:class:biblioteca:formato:artigo' UNION ALL
    SELECT 'FORMATO_EBOOK', 'urn:llam:class:biblioteca:formato:ebook' UNION ALL
    SELECT 'FORMATO_AUDIOBOOK', 'urn:llam:class:biblioteca:formato:audiobook' UNION ALL
    SELECT 'FORMATO_PDF', 'urn:llam:class:biblioteca:formato:pdf'
) v WHERE cs.Constant = 'LIB_FORMATO';

-- ============================================================================
-- 4. Classifications - Status de Título (LIB_STATUS_TITULO)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'TITULO_ATIVO' AS Constant, 'urn:llam:class:biblioteca:status_titulo:ativo' AS URI UNION ALL
    SELECT 'TITULO_INATIVO', 'urn:llam:class:biblioteca:status_titulo:inativo' UNION ALL
    SELECT 'TITULO_DESCONTINUADO', 'urn:llam:class:biblioteca:status_titulo:descontinuado'
) v WHERE cs.Constant = 'LIB_STATUS_TITULO';

-- ============================================================================
-- 5. Classifications - Status de Exemplar (LIB_STATUS_EXEMPLAR)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'EXEMPLAR_DISPONIVEL' AS Constant, 'urn:llam:class:biblioteca:status_exemplar:disponivel' AS URI UNION ALL
    SELECT 'EXEMPLAR_EMPRESTADO', 'urn:llam:class:biblioteca:status_exemplar:emprestado' UNION ALL
    SELECT 'EXEMPLAR_RESERVADO', 'urn:llam:class:biblioteca:status_exemplar:reservado' UNION ALL
    SELECT 'EXEMPLAR_MANUTENCAO', 'urn:llam:class:biblioteca:status_exemplar:manutencao' UNION ALL
    SELECT 'EXEMPLAR_EXTRAVIADO', 'urn:llam:class:biblioteca:status_exemplar:extraviado' UNION ALL
    SELECT 'EXEMPLAR_BAIXADO', 'urn:llam:class:biblioteca:status_exemplar:baixado'
) v WHERE cs.Constant = 'LIB_STATUS_EXEMPLAR';

-- ============================================================================
-- 6. Classifications - Status de Leitor (LIB_STATUS_LEITOR)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'LEITOR_ATIVO' AS Constant, 'urn:llam:class:biblioteca:status_leitor:ativo' AS URI UNION ALL
    SELECT 'LEITOR_INATIVO', 'urn:llam:class:biblioteca:status_leitor:inativo' UNION ALL
    SELECT 'LEITOR_SUSPENSO', 'urn:llam:class:biblioteca:status_leitor:suspenso' UNION ALL
    SELECT 'LEITOR_BLOQUEADO', 'urn:llam:class:biblioteca:status_leitor:bloqueado' UNION ALL
    SELECT 'LEITOR_INADIMPLENTE', 'urn:llam:class:biblioteca:status_leitor:inadimplente'
) v WHERE cs.Constant = 'LIB_STATUS_LEITOR';

-- ============================================================================
-- 7. Classifications - Status de Empréstimo (LIB_STATUS_EMPRESTIMO)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'EMPRESTIMO_ATIVO' AS Constant, 'urn:llam:class:biblioteca:status_emprestimo:ativo' AS URI UNION ALL
    SELECT 'EMPRESTIMO_DEVOLVIDO', 'urn:llam:class:biblioteca:status_emprestimo:devolvido' UNION ALL
    SELECT 'EMPRESTIMO_ATRASADO', 'urn:llam:class:biblioteca:status_emprestimo:atrasado' UNION ALL
    SELECT 'EMPRESTIMO_RENOVADO', 'urn:llam:class:biblioteca:status_emprestimo:renovado' UNION ALL
    SELECT 'EMPRESTIMO_CANCELADO', 'urn:llam:class:biblioteca:status_emprestimo:cancelado'
) v WHERE cs.Constant = 'LIB_STATUS_EMPRESTIMO';

-- ============================================================================
-- 8. Classifications - Tipos de Transação (LIB_TIPO_TRANSACAO) - CORE
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'TRANSACAO_EMPRESTIMO' AS Constant, 'urn:llam:class:biblioteca:tipo_transacao:emprestimo' AS URI UNION ALL
    SELECT 'TRANSACAO_DEVOLUCAO', 'urn:llam:class:biblioteca:tipo_transacao:devolucao' UNION ALL
    SELECT 'TRANSACAO_RENOVACAO', 'urn:llam:class:biblioteca:tipo_transacao:renovacao' UNION ALL
    SELECT 'TRANSACAO_RESERVA', 'urn:llam:class:biblioteca:tipo_transacao:reserva' UNION ALL
    SELECT 'TRANSACAO_CANCELAMENTO_RESERVA', 'urn:llam:class:biblioteca:tipo_transacao:cancelamento_reserva' UNION ALL
    SELECT 'TRANSACAO_VENDA_DIGITAL', 'urn:llam:class:biblioteca:tipo_transacao:venda_digital' UNION ALL
    SELECT 'TRANSACAO_MULTA', 'urn:llam:class:biblioteca:tipo_transacao:multa' UNION ALL
    SELECT 'TRANSACAO_PAGAMENTO_MULTA', 'urn:llam:class:biblioteca:tipo_transacao:pagamento_multa' UNION ALL
    SELECT 'TRANSACAO_BAIXA_EXEMPLAR', 'urn:llam:class:biblioteca:tipo_transacao:baixa_exemplar' UNION ALL
    SELECT 'TRANSACAO_ENTRADA_EXEMPLAR', 'urn:llam:class:biblioteca:tipo_transacao:entrada_exemplar'
) v WHERE cs.Constant = 'LIB_TIPO_TRANSACAO';

-- ============================================================================
-- 9. Classifications - Tipos de Conta (LIB_TIPO_CONTA)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'CONTA_BIBLIOTECA' AS Constant, 'urn:llam:class:biblioteca:tipo_conta:biblioteca' AS URI UNION ALL
    SELECT 'CONTA_LEITOR', 'urn:llam:class:biblioteca:tipo_conta:leitor' UNION ALL
    SELECT 'CONTA_DIGITAL', 'urn:llam:class:biblioteca:tipo_conta:digital' UNION ALL
    SELECT 'CONTA_FINANCEIRA', 'urn:llam:class:biblioteca:tipo_conta:financeira'
) v WHERE cs.Constant = 'LIB_TIPO_CONTA';

-- ============================================================================
-- 10. Classifications - Tipos de Lançamento (LIB_TIPO_LANCAMENTO)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'LANCAMENTO_DEBITO' AS Constant, 'urn:llam:class:biblioteca:tipo_lancamento:debito' AS URI UNION ALL
    SELECT 'LANCAMENTO_CREDITO', 'urn:llam:class:biblioteca:tipo_lancamento:credito'
) v WHERE cs.Constant = 'LIB_TIPO_LANCAMENTO';

-- ============================================================================
-- 11. Classifications - Relação Título-Exemplar (LIB_RELACAO_TITULO_EXEMPLAR)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'REL_TITULO_EXEMPLAR_FISICO' AS Constant, 'urn:llam:class:biblioteca:rel_titulo_exemplar:fisico' AS URI UNION ALL
    SELECT 'REL_TITULO_EXEMPLAR_DIGITAL', 'urn:llam:class:biblioteca:rel_titulo_exemplar:digital'
) v WHERE cs.Constant = 'LIB_RELACAO_TITULO_EXEMPLAR';

-- ============================================================================
-- 12. Classifications - Relação Empréstimo (LIB_RELACAO_EMPRESTIMO)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'REL_EMPRESTIMO_LEITOR' AS Constant, 'urn:llam:class:biblioteca:rel_emprestimo:leitor' AS URI UNION ALL
    SELECT 'REL_EMPRESTIMO_EXEMPLAR', 'urn:llam:class:biblioteca:rel_emprestimo:exemplar' UNION ALL
    SELECT 'REL_EMPRESTIMO_BIBLIOTECARIO', 'urn:llam:class:biblioteca:rel_emprestimo:bibliotecario'
) v WHERE cs.Constant = 'LIB_RELACAO_EMPRESTIMO';

-- ============================================================================
-- 12.1 Classifications - Relação OrganizationUnit (LIB_RELACAO_OU)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'REL_OU_FILHO' AS Constant, 'urn:llam:class:biblioteca:rel_ou:filho' AS URI UNION ALL
    SELECT 'REL_OU_PARCEIRO', 'urn:llam:class:biblioteca:rel_ou:parceiro'
) v WHERE cs.Constant = 'LIB_RELACAO_OU';

-- ============================================================================
-- 13. Classifications - Configurações de Tenant (LIB_CONFIG_TENANT)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'CONFIG_PRAZO_EMPRESTIMO' AS Constant, 'urn:llam:class:biblioteca:config_tenant:prazo_emprestimo' AS URI UNION ALL
    SELECT 'CONFIG_LIMITE_EMPRESTIMOS', 'urn:llam:class:biblioteca:config_tenant:limite_emprestimos' UNION ALL
    SELECT 'CONFIG_LIMITE_RENOVACOES', 'urn:llam:class:biblioteca:config_tenant:limite_renovacoes' UNION ALL
    SELECT 'CONFIG_VALOR_MULTA_DIA', 'urn:llam:class:biblioteca:config_tenant:valor_multa_dia' UNION ALL
    SELECT 'CONFIG_PERMITE_RESERVA', 'urn:llam:class:biblioteca:config_tenant:permite_reserva' UNION ALL
    SELECT 'CONFIG_PERMITE_VENDA_DIGITAL', 'urn:llam:class:biblioteca:config_tenant:permite_venda_digital' UNION ALL
    SELECT 'CONFIG_TEMA_CORES', 'urn:llam:class:biblioteca:config_tenant:tema_cores' UNION ALL
    SELECT 'CONFIG_LOGO_URL', 'urn:llam:class:biblioteca:config_tenant:logo_url' UNION ALL
    SELECT 'CONFIG_DOMINIO_PERSONALIZADO', 'urn:llam:class:biblioteca:config_tenant:dominio_personalizado'
) v WHERE cs.Constant = 'LIB_CONFIG_TENANT';

-- ============================================================================
-- 14. Classifications - Planos de Assinatura (LIB_PLANO_ASSINATURA)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'PLANO_GRATUITO' AS Constant, 'urn:llam:class:biblioteca:plano:gratuito' AS URI UNION ALL
    SELECT 'PLANO_BASICO', 'urn:llam:class:biblioteca:plano:basico' UNION ALL
    SELECT 'PLANO_PREMIUM', 'urn:llam:class:biblioteca:plano:premium' UNION ALL
    SELECT 'PLANO_INSTITUCIONAL', 'urn:llam:class:biblioteca:plano:institucional'
) v WHERE cs.Constant = 'LIB_PLANO_ASSINATURA';

-- ============================================================================
-- 15. Classifications - Tipos de Multa (LIB_TIPO_MULTA)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'MULTA_ATRASO' AS Constant, 'urn:llam:class:biblioteca:tipo_multa:atraso' AS URI UNION ALL
    SELECT 'MULTA_EXTRAVIO', 'urn:llam:class:biblioteca:tipo_multa:extravio' UNION ALL
    SELECT 'MULTA_DANO', 'urn:llam:class:biblioteca:tipo_multa:dano'
) v WHERE cs.Constant = 'LIB_TIPO_MULTA';

-- ============================================================================
-- 16. Classifications - Status de Multa (LIB_STATUS_MULTA)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'MULTA_PENDENTE' AS Constant, 'urn:llam:class:biblioteca:status_multa:pendente' AS URI UNION ALL
    SELECT 'MULTA_PAGA', 'urn:llam:class:biblioteca:status_multa:paga' UNION ALL
    SELECT 'MULTA_ISENTA', 'urn:llam:class:biblioteca:status_multa:isenta' UNION ALL
    SELECT 'MULTA_CANCELADA', 'urn:llam:class:biblioteca:status_multa:cancelada'
) v WHERE cs.Constant = 'LIB_STATUS_MULTA';

-- ============================================================================
-- 17. Classifications - Perfis de Usuário (LIB_PERFIL_USUARIO)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'PERFIL_ADMIN_PLATAFORMA' AS Constant, 'urn:llam:class:biblioteca:perfil:admin_plataforma' AS URI UNION ALL
    SELECT 'PERFIL_ADMIN_TENANT', 'urn:llam:class:biblioteca:perfil:admin_tenant' UNION ALL
    SELECT 'PERFIL_BIBLIOTECARIO', 'urn:llam:class:biblioteca:perfil:bibliotecario' UNION ALL
    SELECT 'PERFIL_ATENDENTE', 'urn:llam:class:biblioteca:perfil:atendente' UNION ALL
    SELECT 'PERFIL_LEITOR', 'urn:llam:class:biblioteca:perfil:leitor'
) v WHERE cs.Constant = 'LIB_PERFIL_USUARIO';

-- ============================================================================
-- 18. Classifications - Ações (LIB_ACAO)
-- ============================================================================
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
CROSS JOIN (
    SELECT 'ACAO_VISUALIZAR' AS Constant, 'urn:llam:class:biblioteca:acao:visualizar' AS URI UNION ALL
    SELECT 'ACAO_CRIAR', 'urn:llam:class:biblioteca:acao:criar' UNION ALL
    SELECT 'ACAO_EDITAR', 'urn:llam:class:biblioteca:acao:editar' UNION ALL
    SELECT 'ACAO_EXCLUIR', 'urn:llam:class:biblioteca:acao:excluir' UNION ALL
    SELECT 'ACAO_EMPRESTAR', 'urn:llam:class:biblioteca:acao:emprestar' UNION ALL
    SELECT 'ACAO_DEVOLVER', 'urn:llam:class:biblioteca:acao:devolver' UNION ALL
    SELECT 'ACAO_RENOVAR', 'urn:llam:class:biblioteca:acao:renovar' UNION ALL
    SELECT 'ACAO_RESERVAR', 'urn:llam:class:biblioteca:acao:reservar' UNION ALL
    SELECT 'ACAO_COMPRAR', 'urn:llam:class:biblioteca:acao:comprar' UNION ALL
    SELECT 'ACAO_CONFIGURAR', 'urn:llam:class:biblioteca:acao:configurar' UNION ALL
    SELECT 'ACAO_RELATORIO', 'urn:llam:class:biblioteca:acao:relatorio'
) v WHERE cs.Constant = 'LIB_ACAO';

-- ============================================================================
-- 19. ClassScheme - Atributos EVA de Título (LIB_ATRIBUTO_TITULO)
-- Para uso com DublinCoreValueString, DublinCoreValueInteger, DublinCoreValueDate
-- ============================================================================
INSERT IGNORE INTO classification_producao.ClassScheme (Constant, URI) VALUES
('LIB_ATRIBUTO_TITULO', 'urn:llam:classscheme:biblioteca:atributo_titulo');

-- Classifications - Atributos de Título (para tabelas DublinCoreValue*)
INSERT IGNORE INTO classification_producao.Class (ClassSchemeId, Constant, URI)
SELECT cs.ClassSchemeId, v.Constant, v.URI
FROM classification_producao.ClassScheme cs
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

---

## 3. Mapeamento de Entidades CERIF

### 3.1 Entidades Reutilizadas

| Conceito Biblioteca | Entidade CERIF | ClassScheme |
|---------------------|----------------|-------------|
| Título Bibliográfico | DublinCore | LIB_TITULO |
| Exemplar Físico | Class (filho de DublinCore) | LIB_EXEMPLAR |
| Categoria | Class | LIB_CATEGORIA |
| Leitor | Person | LIB_STATUS_LEITOR |
| Biblioteca (Tenant) | OrganizationUnit | LIB_TENANT |
| Empréstimo | Transaction | LIB_TIPO_TRANSACAO |
| Multa | Transaction | LIB_TIPO_MULTA |

### 3.2 Relações via ClassClass

| Relação | ClassScheme | Descrição |
|---------|-------------|-----------|
| Título → Exemplar | LIB_RELACAO_TITULO_EXEMPLAR | Vincula exemplares ao título |
| Título → Categoria | LIB_RELACAO_TITULO_CATEGORIA | Categorização do título |
| Leitor → Tenant | LIB_RELACAO_LEITOR_TENANT | Vínculo do leitor à biblioteca |
| Empréstimo → Atores | LIB_RELACAO_EMPRESTIMO | Leitor, Exemplar, Bibliotecário |

---

## 4. Verificação de Pré-requisitos

### 4.1 Verificar ClassSchemes

```sql
-- Esperado: 22 ClassSchemes do LLAM Biblioteca
-- Substituir SCHEMA pelo ambiente
SELECT Constant, URI FROM SCHEMA.ClassScheme 
WHERE Constant LIKE 'LIB_%'
ORDER BY Constant;
```

### 4.2 Verificar Classifications por ClassScheme

```sql
-- Contagem de Classes por ClassScheme
SELECT cs.Constant AS ClassScheme, COUNT(c.ClassId) AS TotalClasses
FROM SCHEMA.ClassScheme cs
LEFT JOIN SCHEMA.Class c ON c.ClassSchemeId = cs.ClassSchemeId
WHERE cs.Constant LIKE 'LIB_%'
GROUP BY cs.Constant
ORDER BY cs.Constant;
```

### 4.3 Query Consolidada de Verificação

```sql
-- Esperado: 22 ClassSchemes + ~70 Classes
SELECT 
    'ClassScheme' AS Tipo,
    COUNT(*) AS Total
FROM SCHEMA.ClassScheme 
WHERE Constant LIKE 'LIB_%'

UNION ALL

SELECT 
    'Class' AS Tipo,
    COUNT(*) AS Total
FROM SCHEMA.Class c
JOIN SCHEMA.ClassScheme cs ON cs.ClassSchemeId = c.ClassSchemeId
WHERE cs.Constant LIKE 'LIB_%';
```

| Tipo | Mínimo Esperado |
|------|-----------------|
| ClassScheme | 22 |
| Class | 70+ |

---

## 5. Critérios de Conclusão Técnica (DoD)

- [ ] **Pré-requisitos CERIF executados** (todos os ClassSchemes e Classifications)
- [ ] Verificação executada com sucesso em DEV
- [ ] Verificação executada com sucesso em HOM
- [ ] Verificação executada com sucesso em PRD
- [ ] Integração com `class-backend` validada
- [ ] Integração com `dublin-core-backend` validada
- [ ] Integração com `transaction-backend` validada

---

## 6. Observações

> **NOTA:** As categorias de acervo (`LIB_CATEGORIA`) são criadas **dinamicamente** pelos administradores de cada tenant. Este baseline contém apenas as constantes estruturais do sistema.

> **NOTA:** Os títulos e exemplares são criados via `dublin-core-backend` e vinculados aos ClassSchemes definidos aqui.

> **NOTA:** As transações de empréstimo/devolução são registradas via `transaction-backend` usando os tipos definidos em `LIB_TIPO_TRANSACAO`.
