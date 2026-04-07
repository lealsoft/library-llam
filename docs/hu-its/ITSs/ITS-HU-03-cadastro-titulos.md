# ITS-HU-03 — Cadastro de Títulos Bibliográficos (Dublin Core)

Escopo: especificação técnica da HU-03 cobrindo o cadastro de títulos usando o padrão Dublin Core.

Relacionado:
- HU: `../HUs/hu-03-cadastro-titulos.md`
- HU: `../HUs/hu-04-gestao-exemplares.md`
- ITS: `./ITS-HU-01-modelagem-dados-biblioteca.md`

---

## 1. Mapeamento Dublin Core para Títulos Bibliográficos

### 1.1 Entidades Utilizadas (Modelo EVA - Entity-Value-Attribute)

> **NOTA:** O modelo EVA utiliza as tabelas `DublinCoreValueString`, `DublinCoreValueInteger`, `DublinCoreValueText`, `DublinCoreValueDate` e `DublinCoreValueFloat` para armazenar atributos dinâmicos. Cada atributo é identificado por um `ClassId` da camada semântica `classification.Class`.

| Campo Biblioteca | Tabela EVA | ClassId (Constant) | Tipo |
|------------------|------------|-------------------|------|
| ID do Título | `DublinCore` | - | UUID (PK) |
| Título | `DublinCoreValueString` | `ATTR_TITULO` | String |
| Subtítulo | `DublinCoreValueString` | `ATTR_SUBTITULO` | String |
| Autor(es) | `DublinCoreValueString` | `ATTR_AUTOR` | String |
| Editora | `DublinCoreValueString` | `ATTR_EDITORA` | String |
| ISBN | `DublinCoreValueString` | `ATTR_ISBN` | String |
| Categoria | `DublinCoreValueString` | `ATTR_CATEGORIA` | String |
| Capa URL | `DublinCoreValueString` | `ATTR_CAPA_URL` | String |
| Sinopse | `DublinCoreValueText` | `ATTR_SINOPSE` | Text |
| Ano Publicação | `DublinCoreValueInteger` | `ATTR_ANO_PUBLICACAO` | Integer |
| Número Páginas | `DublinCoreValueInteger` | `ATTR_NUMERO_PAGINAS` | Integer |
| Edição | `DublinCoreValueInteger` | `ATTR_EDICAO` | Integer |

### 1.1.1 Mapeamento Legado (Tabelas Específicas)

| Campo Biblioteca | Entidade Dublin Core | Tabela | Descrição |
|------------------|---------------------|--------|-----------|
| ID do Título | `DublinCore.dublinCoreId` | DublinCore | UUID único |
| Título | `DublinCoreTitle.title` | DublinCoreTitle | Nome do livro |
| Autor(es) | `DublinCoreCreator.creator` | DublinCoreCreator | Autores (múltiplos) |
| Editora | `DublinCorePublisher.publisher` | DublinCorePublisher | Casa editorial |
| ISBN | `DublinCoreResourceIdentifier.identifier` | DublinCoreResourceIdentifier | ISBN-10 ou ISBN-13 |
| Ano | `DublinCoreDate.date` | DublinCoreDate | Data de publicação |
| Idioma | `DublinCoreLanguage.languageId` | DublinCoreLanguage | Referência à Language |
| Sinopse | `DublinCoreDescription.description` | DublinCoreDescription | Resumo do conteúdo |
| Categoria | `DublinCoreSubject.subject` | DublinCoreSubject | Classificação temática |
| Capa | `DublinCoreFormat.format` | DublinCoreFormat | URL da imagem |
| Direitos | `DublinCoreRightsMM` | DublinCoreRightsMM | Licenciamento e preços |

### 1.2 Estrutura de Dados

```json
{
  "dublinCoreId": "uuid-do-titulo",
  "classSchemeId": "LIB_TITULO",
  "uri": "urn:llam:biblioteca:titulo:uuid",
  "languageId": 1,
  "translator": "",
  "title": {
    "title": "Dom Casmurro",
    "subtitle": "Romance"
  },
  "creators": [
    { "creator": "Machado de Assis", "role": "AUTOR" }
  ],
  "publisher": {
    "publisher": "Editora Garnier"
  },
  "identifier": {
    "identifier": "978-85-359-0277-5",
    "type": "ISBN-13"
  },
  "date": {
    "date": "1899-01-01",
    "type": "PUBLICACAO"
  },
  "description": {
    "description": "Romance que narra a história de Bentinho..."
  },
  "subjects": [
    { "subject": "Literatura Brasileira" },
    { "subject": "Romance" }
  ],
  "format": {
    "format": "https://storage.llam.tec.br/capas/uuid.jpg",
    "type": "CAPA"
  }
}
```

---

## 2. Scripts SQL - Resolução de IDs

### 2.0 Cadastro de Título usando EVA (Recomendado)

```sql
-- ============================================================================
-- EXEMPLO: Cadastrar título "Dom Casmurro" usando modelo EVA
-- ============================================================================

SET @uuid_titulo := UUID();
SET @tenant_id := '1';

-- 1. Criar DublinCore principal
INSERT INTO dublincore_homologacao.DublinCore (
    DublinCoreId,
    ClassSchemeId,
    URI,
    LanguageId,
    Translator
) VALUES (
    @uuid_titulo,
    '1',  -- ClassSchemeId padrão
    CONCAT('urn:llam:biblioteca:', @tenant_id, ':titulo:', @uuid_titulo),
    1,    -- Português
    'N'   -- Não é tradução
);

-- 2. Resolver ClassIds dos atributos
SET @class_titulo := (
    SELECT c.ClassId FROM classification_homologacao.Class c 
    JOIN classification_homologacao.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_ATRIBUTO_TITULO' AND c.Constant = 'ATTR_TITULO' LIMIT 1
);
SET @class_autor := (
    SELECT c.ClassId FROM classification_homologacao.Class c 
    JOIN classification_homologacao.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_ATRIBUTO_TITULO' AND c.Constant = 'ATTR_AUTOR' LIMIT 1
);
SET @class_isbn := (
    SELECT c.ClassId FROM classification_homologacao.Class c 
    JOIN classification_homologacao.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_ATRIBUTO_TITULO' AND c.Constant = 'ATTR_ISBN' LIMIT 1
);
SET @class_editora := (
    SELECT c.ClassId FROM classification_homologacao.Class c 
    JOIN classification_homologacao.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_ATRIBUTO_TITULO' AND c.Constant = 'ATTR_EDITORA' LIMIT 1
);
SET @class_ano := (
    SELECT c.ClassId FROM classification_homologacao.Class c 
    JOIN classification_homologacao.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_ATRIBUTO_TITULO' AND c.Constant = 'ATTR_ANO_PUBLICACAO' LIMIT 1
);
SET @class_sinopse := (
    SELECT c.ClassId FROM classification_homologacao.Class c 
    JOIN classification_homologacao.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_ATRIBUTO_TITULO' AND c.Constant = 'ATTR_SINOPSE' LIMIT 1
);

-- 3. Inserir atributos String
INSERT INTO dublincore_homologacao.DublinCoreValueString (DublinCoreId, ClassId, ValueString) VALUES
(@uuid_titulo, @class_titulo, 'Dom Casmurro'),
(@uuid_titulo, @class_autor, 'Machado de Assis'),
(@uuid_titulo, @class_isbn, '978-85-359-0277-5'),
(@uuid_titulo, @class_editora, 'Editora Garnier');

-- 4. Inserir atributos Integer
INSERT INTO dublincore_homologacao.DublinCoreValueInteger (DublinCoreId, ClassId, ValueInteger) VALUES
(@uuid_titulo, @class_ano, 1899);

-- 5. Inserir atributos Text
INSERT INTO dublincore_homologacao.DublinCoreValueText (DublinCoreId, ClassId, ValueText) VALUES
(@uuid_titulo, @class_sinopse, 'Romance que narra a história de Bentinho e Capitu, explorando temas de ciúme e dúvida.');
```

### 2.0.1 Consulta de Título com EVA

```sql
-- ============================================================================
-- CONSULTA: Buscar título com todos os atributos EVA
-- ============================================================================

SELECT 
    dc.DublinCoreId AS id,
    dc.URI AS uri,
    MAX(CASE WHEN c.Constant = 'ATTR_TITULO' THEN vs.ValueString END) AS titulo,
    MAX(CASE WHEN c.Constant = 'ATTR_SUBTITULO' THEN vs.ValueString END) AS subtitulo,
    MAX(CASE WHEN c.Constant = 'ATTR_AUTOR' THEN vs.ValueString END) AS autor,
    MAX(CASE WHEN c.Constant = 'ATTR_EDITORA' THEN vs.ValueString END) AS editora,
    MAX(CASE WHEN c.Constant = 'ATTR_ISBN' THEN vs.ValueString END) AS isbn,
    MAX(CASE WHEN c.Constant = 'ATTR_CATEGORIA' THEN vs.ValueString END) AS categoria,
    MAX(CASE WHEN ca.Constant = 'ATTR_ANO_PUBLICACAO' THEN vi.ValueInteger END) AS ano_publicacao,
    MAX(CASE WHEN ct.Constant = 'ATTR_SINOPSE' THEN vt.ValueText END) AS sinopse
FROM dublincore_homologacao.DublinCore dc
LEFT JOIN dublincore_homologacao.DublinCoreValueString vs ON dc.DublinCoreId = vs.DublinCoreId
LEFT JOIN classification_homologacao.Class c ON vs.ClassId = c.ClassId
LEFT JOIN dublincore_homologacao.DublinCoreValueInteger vi ON dc.DublinCoreId = vi.DublinCoreId
LEFT JOIN classification_homologacao.Class ca ON vi.ClassId = ca.ClassId
LEFT JOIN dublincore_homologacao.DublinCoreValueText vt ON dc.DublinCoreId = vt.DublinCoreId
LEFT JOIN classification_homologacao.Class ct ON vt.ClassId = ct.ClassId
WHERE dc.URI LIKE 'urn:llam:biblioteca:%:titulo:%'
GROUP BY dc.DublinCoreId, dc.URI;
```

---

### 2.1 Variáveis para Status e Formatos (Modelo Legado)

```sql
-- ============================================================================
-- RESOLUÇÃO DE IDs - TÍTULOS E FORMATOS (LEGADO)
-- ============================================================================

-- Status de Título
SET @id_titulo_ativo := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_TITULO' 
    AND c.Constant = 'TITULO_ATIVO' LIMIT 1
);

SET @id_titulo_inativo := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_TITULO' 
    AND c.Constant = 'TITULO_INATIVO' LIMIT 1
);

-- Formatos
SET @id_formato_livro := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_FORMATO' 
    AND c.Constant = 'FORMATO_LIVRO' LIMIT 1
);

SET @id_formato_ebook := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_FORMATO' 
    AND c.Constant = 'FORMATO_EBOOK' LIMIT 1
);

SET @id_formato_pdf := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_FORMATO' 
    AND c.Constant = 'FORMATO_PDF' LIMIT 1
);

-- Relações
SET @id_rel_titulo_exemplar_fisico := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_RELACAO_TITULO_EXEMPLAR' 
    AND c.Constant = 'REL_TITULO_EXEMPLAR_FISICO' LIMIT 1
);

SET @id_rel_titulo_exemplar_digital := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_RELACAO_TITULO_EXEMPLAR' 
    AND c.Constant = 'REL_TITULO_EXEMPLAR_DIGITAL' LIMIT 1
);
```

### 2.2 Exemplo de Cadastro de Título

```sql
-- ============================================================================
-- EXEMPLO: Cadastrar título "Dom Casmurro"
-- ============================================================================

SET @uuid_titulo := UUID();
SET @tenant_id := 'organization-unit-id-do-tenant';

-- 1. Criar DublinCore principal
INSERT INTO dublincore_desenvolvimento.DublinCore (
    dublinCoreId,
    classSchemeId,
    uri,
    languageId,
    translator
) VALUES (
    @uuid_titulo,
    (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_TITULO'),
    CONCAT('urn:llam:biblioteca:titulo:', @uuid_titulo),
    1,  -- Português
    ''
);

-- 2. Adicionar título
INSERT INTO dublincore_desenvolvimento.DublinCoreTitle (
    dublinCoreId,
    classId,
    title
) VALUES (
    @uuid_titulo,
    @id_titulo_ativo,
    'Dom Casmurro'
);

-- 3. Adicionar autor
INSERT INTO dublincore_desenvolvimento.DublinCoreCreator (
    dublinCoreId,
    classId,
    creator
) VALUES (
    @uuid_titulo,
    @id_formato_livro,
    'Machado de Assis'
);

-- 4. Adicionar editora
INSERT INTO dublincore_desenvolvimento.DublinCorePublisher (
    dublinCoreId,
    classId,
    publisher
) VALUES (
    @uuid_titulo,
    @id_formato_livro,
    'Editora Garnier'
);

-- 5. Adicionar ISBN
INSERT INTO dublincore_desenvolvimento.DublinCoreResourceIdentifier (
    dublinCoreId,
    classId,
    identifier
) VALUES (
    @uuid_titulo,
    @id_formato_livro,
    '978-85-359-0277-5'
);

-- 6. Adicionar data de publicação
INSERT INTO dublincore_desenvolvimento.DublinCoreDate (
    dublinCoreId,
    classId,
    dateValue
) VALUES (
    @uuid_titulo,
    @id_formato_livro,
    '1899-01-01'
);

-- 7. Adicionar sinopse
INSERT INTO dublincore_desenvolvimento.DublinCoreDescription (
    dublinCoreId,
    classId,
    description
) VALUES (
    @uuid_titulo,
    @id_formato_livro,
    'Romance que narra a história de Bentinho e Capitu, explorando temas de ciúme e dúvida.'
);

-- 8. Vincular ao tenant via ClassClass
INSERT INTO classification_desenvolvimento.ClassClass (
    ClassId1,
    ClassId2,
    ClassSchemeId
) VALUES (
    @uuid_titulo,  -- Título
    @tenant_id,    -- Tenant (OrganizationUnit)
    (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_TENANT')
);
```

---

## 3. Consultas

### 3.1 Listar Títulos do Tenant

```sql
SELECT 
    dc.dublinCoreId,
    dct.title,
    dcc.creator AS autor,
    dcp.publisher AS editora,
    dcri.identifier AS isbn,
    dcd.dateValue AS ano_publicacao
FROM dublincore_desenvolvimento.DublinCore dc
JOIN dublincore_desenvolvimento.DublinCoreTitle dct ON dc.dublinCoreId = dct.dublinCoreId
LEFT JOIN dublincore_desenvolvimento.DublinCoreCreator dcc ON dc.dublinCoreId = dcc.dublinCoreId
LEFT JOIN dublincore_desenvolvimento.DublinCorePublisher dcp ON dc.dublinCoreId = dcp.dublinCoreId
LEFT JOIN dublincore_desenvolvimento.DublinCoreResourceIdentifier dcri ON dc.dublinCoreId = dcri.dublinCoreId
LEFT JOIN dublincore_desenvolvimento.DublinCoreDate dcd ON dc.dublinCoreId = dcd.dublinCoreId
JOIN classification_desenvolvimento.ClassClass cc ON dc.dublinCoreId = cc.ClassId1
WHERE cc.ClassId2 = @tenant_id
AND cc.ClassSchemeId = (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_TENANT')
ORDER BY dct.title;
```

### 3.2 Buscar por Título, Autor ou ISBN

```sql
SELECT 
    dc.dublinCoreId,
    dct.title,
    dcc.creator AS autor,
    dcri.identifier AS isbn
FROM dublincore_desenvolvimento.DublinCore dc
JOIN dublincore_desenvolvimento.DublinCoreTitle dct ON dc.dublinCoreId = dct.dublinCoreId
LEFT JOIN dublincore_desenvolvimento.DublinCoreCreator dcc ON dc.dublinCoreId = dcc.dublinCoreId
LEFT JOIN dublincore_desenvolvimento.DublinCoreResourceIdentifier dcri ON dc.dublinCoreId = dcri.dublinCoreId
JOIN classification_desenvolvimento.ClassClass cc ON dc.dublinCoreId = cc.ClassId1
WHERE cc.ClassId2 = @tenant_id
AND (
    dct.title LIKE CONCAT('%', @termo_busca, '%')
    OR dcc.creator LIKE CONCAT('%', @termo_busca, '%')
    OR dcri.identifier LIKE CONCAT('%', @termo_busca, '%')
);
```

### 3.3 Verificar ISBN Duplicado no Tenant

```sql
SELECT COUNT(*) AS duplicado
FROM dublincore_desenvolvimento.DublinCoreResourceIdentifier dcri
JOIN dublincore_desenvolvimento.DublinCore dc ON dcri.dublinCoreId = dc.dublinCoreId
JOIN classification_desenvolvimento.ClassClass cc ON dc.dublinCoreId = cc.ClassId1
WHERE dcri.identifier = @isbn
AND cc.ClassId2 = @tenant_id
AND cc.ClassSchemeId = (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_TENANT');
```

---

## 4. Endpoints da API

### 4.1 CRUD de Títulos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/titulos` | Criar novo título |
| GET | `/api/titulos` | Listar títulos do tenant (paginado) |
| GET | `/api/titulos/{id}` | Buscar título por ID |
| PUT | `/api/titulos/{id}` | Atualizar título |
| PATCH | `/api/titulos/{id}/status` | Ativar/desativar título |
| DELETE | `/api/titulos/{id}` | Soft delete (desativar) |

### 4.2 Busca e Filtros

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/titulos?q={termo}` | Busca textual |
| GET | `/api/titulos?categoria={id}` | Filtrar por categoria |
| GET | `/api/titulos?formato={id}` | Filtrar por formato |
| GET | `/api/titulos?status=ATIVO` | Filtrar por status |

### 4.3 Upload de Capa

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/titulos/{id}/capa` | Upload de imagem de capa |
| DELETE | `/api/titulos/{id}/capa` | Remover capa |

---

## 5. Validações

### 5.1 Campos Obrigatórios

| Campo | Validação |
|-------|-----------|
| Título | Não vazio, máx 500 caracteres |
| Autor | Pelo menos 1 autor |
| ISBN | Formato válido (ISBN-10 ou ISBN-13) |

### 5.2 Regras de Negócio

| Regra | Validação |
|-------|-----------|
| ISBN único | Não pode haver ISBN duplicado no mesmo tenant |
| Tenant obrigatório | Todo título deve estar vinculado a um tenant |
| Soft delete | Título com exemplares ativos não pode ser excluído |

---

## 6. Critérios de Conclusão Técnica (DoD)

- [ ] ClassSchemes de título criados (ITS-HU-01)
- [ ] Integração com `dublin-core-backend` implementada
- [ ] CRUD de títulos funcionando
- [ ] Busca textual funcionando
- [ ] Validação de ISBN implementada
- [ ] Upload de capa funcionando
- [ ] Isolamento por tenant validado
- [ ] Soft delete implementado

---

## 7. Observações

> **NOTA:** O `dublin-core-backend` já possui todas as entidades Dublin Core. Esta ITS define apenas o mapeamento para o domínio biblioteca.

> **NOTA:** As categorias são criadas dinamicamente pelos administradores de cada tenant e vinculadas via `DublinCoreSubject`.

> **NOTA:** A capa é armazenada em storage externo (S3/MinIO) e referenciada via `DublinCoreFormat`.
