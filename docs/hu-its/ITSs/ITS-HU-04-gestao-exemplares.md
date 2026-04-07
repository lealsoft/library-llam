# ITS-HU-04 — Gestão de Exemplares Físicos

Escopo: especificação técnica da HU-04 cobrindo o cadastro e gestão de exemplares físicos vinculados a títulos.

Relacionado:
- HU: `../HUs/hu-04-gestao-exemplares.md`
- HU: `../HUs/hu-03-cadastro-titulos.md`
- ITS: `./ITS-HU-01-modelagem-dados-biblioteca.md`
- ITS: `./ITS-HU-03-cadastro-titulos.md`

---

## 1. Modelo de Dados

### 1.1 Exemplar como Class vinculada ao Título

O exemplar é uma **Class** vinculada ao título (DublinCore) via **ClassClass**:

```
Título (DublinCore) ──[ClassClass]──> Exemplar (Class)
                                           │
                                           ├── ClassValueString (código de barras)
                                           ├── ClassValueString (localização)
                                           ├── ClassValueInteger (status)
                                           └── ClassValueDate (data aquisição)
```

### 1.2 Estrutura de Dados

```json
{
  "classId": 12345,
  "classSchemeId": "LIB_EXEMPLAR",
  "constant": "EX-2024-001",
  "uri": "urn:llam:biblioteca:exemplar:uuid",
  "tituloId": "dublin-core-id-do-titulo",
  "codigoBarras": "789123456789",
  "localizacao": "Estante A3, Prateleira 2",
  "status": "EXEMPLAR_DISPONIVEL",
  "dataAquisicao": "2024-01-15",
  "observacoes": "Doação da Biblioteca Nacional"
}
```

---

## 2. Scripts SQL - Resolução de IDs

### 2.1 Variáveis para Status de Exemplar

```sql
-- ============================================================================
-- RESOLUÇÃO DE IDs - STATUS DE EXEMPLAR
-- ============================================================================

SET @id_exemplar_disponivel := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_EXEMPLAR' 
    AND c.Constant = 'EXEMPLAR_DISPONIVEL' LIMIT 1
);

SET @id_exemplar_emprestado := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_EXEMPLAR' 
    AND c.Constant = 'EXEMPLAR_EMPRESTADO' LIMIT 1
);

SET @id_exemplar_reservado := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_EXEMPLAR' 
    AND c.Constant = 'EXEMPLAR_RESERVADO' LIMIT 1
);

SET @id_exemplar_manutencao := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_EXEMPLAR' 
    AND c.Constant = 'EXEMPLAR_MANUTENCAO' LIMIT 1
);

SET @id_exemplar_extraviado := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_EXEMPLAR' 
    AND c.Constant = 'EXEMPLAR_EXTRAVIADO' LIMIT 1
);

SET @id_exemplar_baixado := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_EXEMPLAR' 
    AND c.Constant = 'EXEMPLAR_BAIXADO' LIMIT 1
);

-- ClassScheme de Exemplar
SET @id_classscheme_exemplar := (
    SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme 
    WHERE Constant = 'LIB_EXEMPLAR' LIMIT 1
);
```

### 2.2 Exemplo de Cadastro de Exemplar

```sql
-- ============================================================================
-- EXEMPLO: Cadastrar exemplar do título "Dom Casmurro"
-- ============================================================================

SET @uuid_exemplar := UUID();
SET @titulo_id := 'dublin-core-id-do-titulo';
SET @tenant_id := 'organization-unit-id-do-tenant';
SET @codigo_barras := '789123456789';
SET @localizacao := 'Estante A3, Prateleira 2';

-- 1. Criar Class do exemplar
INSERT INTO classification_desenvolvimento.Class (
    ClassSchemeId,
    Constant,
    URI
) VALUES (
    @id_classscheme_exemplar,
    CONCAT('EX-', DATE_FORMAT(NOW(), '%Y'), '-', LPAD(@sequencial, 4, '0')),
    CONCAT('urn:llam:biblioteca:exemplar:', @uuid_exemplar)
);

SET @exemplar_class_id := LAST_INSERT_ID();

-- 2. Vincular exemplar ao título via ClassClass
INSERT INTO classification_desenvolvimento.ClassClass (
    ClassId1,
    ClassId2,
    ClassSchemeId
) VALUES (
    @titulo_id,           -- Título (DublinCore)
    @exemplar_class_id,   -- Exemplar (Class)
    @id_rel_titulo_exemplar_fisico
);

-- 3. Adicionar código de barras via ClassValueString
INSERT INTO classification_desenvolvimento.ClassValueString (
    ClassId,
    ClassSchemeId,
    StringValue
) VALUES (
    @exemplar_class_id,
    (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_EXEMPLAR'),
    @codigo_barras
);

-- 4. Adicionar localização via ClassValueString
INSERT INTO classification_desenvolvimento.ClassValueString (
    ClassId,
    ClassSchemeId,
    StringValue
) VALUES (
    @exemplar_class_id,
    (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_EXEMPLAR'),
    @localizacao
);

-- 5. Definir status inicial via ClassValueInteger
INSERT INTO classification_desenvolvimento.ClassValueInteger (
    ClassId,
    ClassSchemeId,
    IntegerValue
) VALUES (
    @exemplar_class_id,
    (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_STATUS_EXEMPLAR'),
    @id_exemplar_disponivel
);

-- 6. Registrar data de aquisição via ClassValueDate
INSERT INTO classification_desenvolvimento.ClassValueDate (
    ClassId,
    ClassSchemeId,
    DateValue
) VALUES (
    @exemplar_class_id,
    @id_classscheme_exemplar,
    CURDATE()
);

-- 7. Vincular ao tenant
INSERT INTO classification_desenvolvimento.ClassClass (
    ClassId1,
    ClassId2,
    ClassSchemeId
) VALUES (
    @exemplar_class_id,
    @tenant_id,
    (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_TENANT')
);
```

---

## 3. Consultas

### 3.1 Listar Exemplares de um Título

```sql
SELECT 
    c.ClassId AS exemplar_id,
    c.Constant AS codigo_exemplar,
    cvs_barras.StringValue AS codigo_barras,
    cvs_local.StringValue AS localizacao,
    cs_status.Constant AS status,
    cvd.DateValue AS data_aquisicao
FROM classification_desenvolvimento.Class c
JOIN classification_desenvolvimento.ClassClass cc ON c.ClassId = cc.ClassId2
JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId
LEFT JOIN classification_desenvolvimento.ClassValueString cvs_barras ON c.ClassId = cvs_barras.ClassId
LEFT JOIN classification_desenvolvimento.ClassValueString cvs_local ON c.ClassId = cvs_local.ClassId AND cvs_local.StringValue LIKE 'Estante%'
LEFT JOIN classification_desenvolvimento.ClassValueInteger cvi ON c.ClassId = cvi.ClassId
LEFT JOIN classification_desenvolvimento.Class c_status ON cvi.IntegerValue = c_status.ClassId
LEFT JOIN classification_desenvolvimento.ClassScheme cs_status ON c_status.ClassSchemeId = cs_status.ClassSchemeId
LEFT JOIN classification_desenvolvimento.ClassValueDate cvd ON c.ClassId = cvd.ClassId
WHERE cc.ClassId1 = @titulo_id
AND cc.ClassSchemeId = @id_rel_titulo_exemplar_fisico
AND cs.Constant = 'LIB_EXEMPLAR';
```

### 3.2 Buscar Exemplar por Código de Barras

```sql
SELECT 
    c.ClassId AS exemplar_id,
    c.Constant AS codigo_exemplar,
    cc.ClassId1 AS titulo_id
FROM classification_desenvolvimento.Class c
JOIN classification_desenvolvimento.ClassValueString cvs ON c.ClassId = cvs.ClassId
JOIN classification_desenvolvimento.ClassClass cc ON c.ClassId = cc.ClassId2
WHERE cvs.StringValue = @codigo_barras
AND cc.ClassSchemeId = @id_rel_titulo_exemplar_fisico;
```

### 3.3 Contar Exemplares por Status

```sql
SELECT 
    cs_status.Constant AS status,
    COUNT(*) AS total
FROM classification_desenvolvimento.Class c
JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId
JOIN classification_desenvolvimento.ClassValueInteger cvi ON c.ClassId = cvi.ClassId
JOIN classification_desenvolvimento.Class c_status ON cvi.IntegerValue = c_status.ClassId
JOIN classification_desenvolvimento.ClassScheme cs_status ON c_status.ClassSchemeId = cs_status.ClassSchemeId
JOIN classification_desenvolvimento.ClassClass cc_tenant ON c.ClassId = cc_tenant.ClassId1
WHERE cs.Constant = 'LIB_EXEMPLAR'
AND cs_status.Constant = 'LIB_STATUS_EXEMPLAR'
AND cc_tenant.ClassId2 = @tenant_id
GROUP BY cs_status.Constant;
```

### 3.4 Estado Derivado do Exemplar

O estado do exemplar é **derivado** das transações:

```sql
-- Verificar se exemplar está emprestado (tem crédito sem devolução)
SELECT 
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM transaction_desenvolvimento.Transaction t
            WHERE t.DocReference = @exemplar_id
            AND t.ClassId = @id_transacao_emprestimo
            AND t.Quantity > 0  -- Crédito (leitor recebeu)
            AND NOT EXISTS (
                SELECT 1 FROM transaction_desenvolvimento.Transaction t2
                WHERE t2.DocReference = t.DocReference
                AND t2.ClassId = @id_transacao_devolucao
                AND t2.TransactionDate > t.TransactionDate
            )
        ) THEN 'EMPRESTADO'
        ELSE 'DISPONIVEL'
    END AS estado_atual;
```

---

## 4. Endpoints da API

### 4.1 CRUD de Exemplares

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/titulos/{tituloId}/exemplares` | Criar exemplar |
| GET | `/api/titulos/{tituloId}/exemplares` | Listar exemplares do título |
| GET | `/api/exemplares/{id}` | Buscar exemplar por ID |
| GET | `/api/exemplares?codigoBarras={codigo}` | Buscar por código de barras |
| PUT | `/api/exemplares/{id}` | Atualizar exemplar |
| PATCH | `/api/exemplares/{id}/localizacao` | Atualizar localização |
| DELETE | `/api/exemplares/{id}` | Baixa definitiva |

### 4.2 Status e Histórico

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/exemplares/{id}/status` | Status atual (derivado) |
| GET | `/api/exemplares/{id}/historico` | Histórico de movimentações |
| PATCH | `/api/exemplares/{id}/manutencao` | Marcar em manutenção |
| PATCH | `/api/exemplares/{id}/extraviado` | Marcar como extraviado |

### 4.3 Operações em Lote

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/titulos/{tituloId}/exemplares/lote` | Criar múltiplos exemplares |
| GET | `/api/exemplares/etiquetas?ids={ids}` | Gerar etiquetas |

---

## 5. Validações

### 5.1 Campos Obrigatórios

| Campo | Validação |
|-------|-----------|
| Código de barras | Único no tenant |
| Localização | Não vazio |
| Título | Deve existir e estar ativo |

### 5.2 Regras de Negócio

| Regra | Validação |
|-------|-----------|
| Código único | Código de barras não pode ser duplicado no tenant |
| Baixa | Exemplar emprestado não pode ser baixado |
| Exclusão | Exemplar com histórico não é excluído, apenas baixado |

---

## 6. Critérios de Conclusão Técnica (DoD)

- [ ] ClassSchemes de exemplar criados (ITS-HU-01)
- [ ] Integração com `class-backend` implementada
- [ ] CRUD de exemplares funcionando
- [ ] Vinculação título-exemplar funcionando
- [ ] Busca por código de barras funcionando
- [ ] Estado derivado calculado corretamente
- [ ] Baixa definitiva implementada
- [ ] Geração de etiquetas funcionando

---

## 7. Observações

> **NOTA:** O estado do exemplar é **derivado** das transações, não armazenado diretamente. Isso garante consistência com o modelo transacional.

> **NOTA:** A baixa definitiva gera uma transação `TRANSACAO_BAIXA_EXEMPLAR` para manter o histórico.

> **NOTA:** O código de barras pode ser gerado automaticamente ou informado manualmente.
