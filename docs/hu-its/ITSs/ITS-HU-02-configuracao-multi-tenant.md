# ITS-HU-02 — Configuração Multi-Tenant White Label

Escopo: especificação técnica da HU-02 cobrindo a configuração de tenants (bibliotecas contratantes) como OrganizationUnits isoladas.

Relacionado:
- HU: `../HUs/hu-02-configuracao-multi-tenant.md`
- HU: `../HUs/hu-23-tela-configuracoes-tenant.md`
- ITS: `./ITS-HU-01-modelagem-dados-biblioteca.md`

---

## 1. Modelo de Dados Multi-Tenant

### 1.1 Hierarquia OrganizationUnit

```
LLAM (raiz)
└── LLAM Biblioteca (produto)
    ├── Biblioteca Municipal de São Paulo (tenant)
    ├── Biblioteca Universitária UFMG (tenant)
    └── Biblioteca Escolar Colégio XYZ (tenant)
```

### 1.2 Estrutura de Dados do Tenant

```json
{
  "organizationUnitId": "uuid-do-tenant",
  "name": "Biblioteca Municipal de São Paulo",
  "acronym": "BMSP",
  "domain": "biblioteca.sp.gov.br",
  "config": {
    "prazoEmprestimo": 14,
    "limiteEmprestimos": 5,
    "limiteRenovacoes": 2,
    "valorMultaDia": 1.50,
    "permiteReserva": true,
    "permiteVendaDigital": false,
    "temaCores": {
      "primaria": "#1E40AF",
      "secundaria": "#3B82F6"
    },
    "logoUrl": "https://storage.llam.tec.br/tenants/bmsp/logo.png"
  }
}
```

---

## 2. Scripts SQL - Configuração de Tenant

### 2.0 Pré-Requisito: Criar Produto LLAM Biblioteca

> **IMPORTANTE:** Execute este script **UMA ÚNICA VEZ** por ambiente para criar o OrganizationUnit raiz do produto.

```sql
-- ============================================================================
-- PRÉ-REQUISITO: Criar OrganizationUnit do Produto LLAM Biblioteca
-- Executar APENAS UMA VEZ por ambiente (DEV/HOM/PRD)
-- ============================================================================

-- Verificar se já existe
SET @llam_biblioteca_existe := (
    SELECT COUNT(*) FROM organizationunit_desenvolvimento.OrganizationUnit 
    WHERE URI = 'urn:llam:produto:biblioteca'
);

-- Criar apenas se não existir
-- Se @llam_biblioteca_existe = 0, execute:
INSERT INTO organizationunit_desenvolvimento.OrganizationUnit (
    OrganizationUnitId,
    URI
) VALUES (
    UUID(),
    'urn:llam:produto:biblioteca'
);

-- Verificar criação
SELECT OrganizationUnitId, URI 
FROM organizationunit_desenvolvimento.OrganizationUnit 
WHERE URI = 'urn:llam:produto:biblioteca';
```

### 2.1 Resolução de IDs

```sql
-- ============================================================================
-- RESOLUÇÃO DE IDs - CONFIGURAÇÕES DE TENANT
-- ============================================================================

-- Configurações
SET @id_config_prazo_emprestimo := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_CONFIG_TENANT' 
    AND c.Constant = 'CONFIG_PRAZO_EMPRESTIMO' LIMIT 1
);

SET @id_config_limite_emprestimos := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_CONFIG_TENANT' 
    AND c.Constant = 'CONFIG_LIMITE_EMPRESTIMOS' LIMIT 1
);

SET @id_config_limite_renovacoes := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_CONFIG_TENANT' 
    AND c.Constant = 'CONFIG_LIMITE_RENOVACOES' LIMIT 1
);

SET @id_config_valor_multa_dia := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_CONFIG_TENANT' 
    AND c.Constant = 'CONFIG_VALOR_MULTA_DIA' LIMIT 1
);

SET @id_config_permite_reserva := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_CONFIG_TENANT' 
    AND c.Constant = 'CONFIG_PERMITE_RESERVA' LIMIT 1
);

SET @id_config_permite_venda_digital := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_CONFIG_TENANT' 
    AND c.Constant = 'CONFIG_PERMITE_VENDA_DIGITAL' LIMIT 1
);

SET @id_config_tema_cores := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_CONFIG_TENANT' 
    AND c.Constant = 'CONFIG_TEMA_CORES' LIMIT 1
);

SET @id_config_logo_url := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_CONFIG_TENANT' 
    AND c.Constant = 'CONFIG_LOGO_URL' LIMIT 1
);

SET @id_config_dominio := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_CONFIG_TENANT' 
    AND c.Constant = 'CONFIG_DOMINIO_PERSONALIZADO' LIMIT 1
);

-- Planos de Assinatura
SET @id_plano_gratuito := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_PLANO_ASSINATURA' 
    AND c.Constant = 'PLANO_GRATUITO' LIMIT 1
);

SET @id_plano_basico := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_PLANO_ASSINATURA' 
    AND c.Constant = 'PLANO_BASICO' LIMIT 1
);

SET @id_plano_premium := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_PLANO_ASSINATURA' 
    AND c.Constant = 'PLANO_PREMIUM' LIMIT 1
);

SET @id_plano_institucional := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_PLANO_ASSINATURA' 
    AND c.Constant = 'PLANO_INSTITUCIONAL' LIMIT 1
);
```

### 2.2 Criar Tenant (OrganizationUnit)

> **IMPORTANTE:** Substitua `'urn:llam:biblioteca:tenant:SEU_TENANT'` pela URI única do seu tenant.

```sql
-- ============================================================================
-- EXEMPLO: Criar tenant "Biblioteca Municipal de São Paulo"
-- Sem uso de variáveis - cada INSERT é independente
-- ============================================================================

-- 1. Criar OrganizationUnit do tenant
INSERT INTO organizationunit_desenvolvimento.OrganizationUnit (
    OrganizationUnitId,
    URI
) 
SELECT UUID(), 'urn:llam:biblioteca:tenant:bmsp'
WHERE NOT EXISTS (
    SELECT 1 FROM organizationunit_desenvolvimento.OrganizationUnit 
    WHERE URI = 'urn:llam:biblioteca:tenant:bmsp'
);

-- 1.1 Criar nome do tenant
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitName (
    OrganizationUnitNameId,
    Name,
    OrganizationUnitId,
    LanguageId
)
SELECT 
    UUID(),
    'Biblioteca Municipal de São Paulo',
    ou.OrganizationUnitId,
    1
FROM organizationunit_desenvolvimento.OrganizationUnit ou
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp';

-- 2. Vincular ao produto LLAM Biblioteca (hierarquia pai-filho)
INSERT INTO organizationunit_desenvolvimento.OrganizationUnit_OrganizationUnit (
    OrganizationUnitId1,
    OrganizationUnitId2,
    ClassId
)
SELECT 
    pai.OrganizationUnitId,
    filho.OrganizationUnitId,
    c.ClassId
FROM organizationunit_desenvolvimento.OrganizationUnit pai
CROSS JOIN organizationunit_desenvolvimento.OrganizationUnit filho
CROSS JOIN classification_desenvolvimento.Class c
WHERE pai.URI = 'urn:llam:produto:biblioteca'
  AND filho.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'REL_OU_FILHO';

-- 3. Configurar prazo de empréstimo (14 dias)
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitValueInteger (
    OrganizationUnitId,
    ClassId,
    ValueInteger
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId,
    14
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'CONFIG_PRAZO_EMPRESTIMO';

-- 4. Configurar limite de empréstimos (5 livros)
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitValueInteger (
    OrganizationUnitId,
    ClassId,
    ValueInteger
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId,
    5
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'CONFIG_LIMITE_EMPRESTIMOS';

-- 5. Configurar limite de renovações (2 vezes)
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitValueInteger (
    OrganizationUnitId,
    ClassId,
    ValueInteger
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId,
    2
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'CONFIG_LIMITE_RENOVACOES';

-- 6. Configurar valor da multa por dia (R$ 1,50)
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitValueFloat (
    OrganizationUnitId,
    ClassId,
    ValueFloat
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId,
    1.50
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'CONFIG_VALOR_MULTA_DIA';

-- 7. Configurar permissão de reserva (sim = 1)
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitValueInteger (
    OrganizationUnitId,
    ClassId,
    ValueInteger
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId,
    1
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'CONFIG_PERMITE_RESERVA';

-- 8. Configurar permissão de venda digital (não = 0)
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitValueInteger (
    OrganizationUnitId,
    ClassId,
    ValueInteger
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId,
    0
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'CONFIG_PERMITE_VENDA_DIGITAL';

-- 9. Configurar tema de cores (JSON)
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitValueString (
    OrganizationUnitId,
    ClassId,
    ValueString
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId,
    '{"primaria":"#1E40AF","secundaria":"#3B82F6"}'
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'CONFIG_TEMA_CORES';

-- 10. Configurar URL do logo
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitValueString (
    OrganizationUnitId,
    ClassId,
    ValueString
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId,
    'https://storage.llam.tec.br/tenants/bmsp/logo.png'
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'CONFIG_LOGO_URL';

-- 11. Configurar domínio personalizado
INSERT INTO organizationunit_desenvolvimento.OrganizationUnitValueString (
    OrganizationUnitId,
    ClassId,
    ValueString
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId,
    'biblioteca.sp.gov.br'
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'CONFIG_DOMINIO_PERSONALIZADO';

-- 12. Vincular plano de assinatura (Institucional)
INSERT INTO organizationunit_desenvolvimento.OrganizationUnit_Class (
    OrganizationUnitId,
    ClassId
)
SELECT 
    ou.OrganizationUnitId,
    c.ClassId
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant = 'PLANO_INSTITUCIONAL';

-- Verificar criação do tenant
SELECT 
    ou.OrganizationUnitId,
    ou.URI,
    oun.Name
FROM organizationunit_desenvolvimento.OrganizationUnit ou
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnitName oun ON ou.OrganizationUnitId = oun.OrganizationUnitId
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp';

---

## 3. Consultas de Verificação

> **IMPORTANTE:** Execute estes SELECTs para verificar se todos os dados foram inseridos corretamente.

### 3.0 Verificação Completa do Tenant (Checklist)

```sql
-- ============================================================================
-- VERIFICAÇÃO COMPLETA: Tenant "Biblioteca Municipal de São Paulo"
-- Substitua 'urn:llam:biblioteca:tenant:bmsp' pela URI do seu tenant
-- ============================================================================

-- 1. Verificar OrganizationUnit do Produto (pai)
SELECT 
    '1. Produto LLAM Biblioteca' AS verificacao,
    CASE WHEN COUNT(*) > 0 THEN '✅ OK' ELSE '❌ FALTANDO' END AS status,
    OrganizationUnitId AS id
FROM organizationunit_desenvolvimento.OrganizationUnit 
WHERE URI = 'urn:llam:produto:biblioteca';

-- 2. Verificar OrganizationUnit do Tenant
SELECT 
    '2. Tenant criado' AS verificacao,
    CASE WHEN COUNT(*) > 0 THEN '✅ OK' ELSE '❌ FALTANDO' END AS status,
    OrganizationUnitId AS id
FROM organizationunit_desenvolvimento.OrganizationUnit 
WHERE URI = 'urn:llam:biblioteca:tenant:bmsp';

-- 3. Verificar Nome do Tenant
SELECT 
    '3. Nome do Tenant' AS verificacao,
    CASE WHEN oun.Name IS NOT NULL THEN '✅ OK' ELSE '❌ FALTANDO' END AS status,
    oun.Name AS valor
FROM organizationunit_desenvolvimento.OrganizationUnit ou
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnitName oun ON ou.OrganizationUnitId = oun.OrganizationUnitId
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp';

-- 4. Verificar Hierarquia (vinculo pai-filho)
SELECT 
    '4. Hierarquia pai-filho' AS verificacao,
    CASE WHEN COUNT(*) > 0 THEN '✅ OK' ELSE '❌ FALTANDO' END AS status,
    c.Constant AS relacao
FROM organizationunit_desenvolvimento.OrganizationUnit_OrganizationUnit ouou
JOIN organizationunit_desenvolvimento.OrganizationUnit pai ON ouou.OrganizationUnitId1 = pai.OrganizationUnitId
JOIN organizationunit_desenvolvimento.OrganizationUnit filho ON ouou.OrganizationUnitId2 = filho.OrganizationUnitId
JOIN classification_desenvolvimento.Class c ON ouou.ClassId = c.ClassId
WHERE pai.URI = 'urn:llam:produto:biblioteca'
  AND filho.URI = 'urn:llam:biblioteca:tenant:bmsp';

-- 5. Verificar Plano de Assinatura
SELECT 
    '5. Plano de Assinatura' AS verificacao,
    CASE WHEN COUNT(*) > 0 THEN '✅ OK' ELSE '❌ FALTANDO' END AS status,
    c.Constant AS plano
FROM organizationunit_desenvolvimento.OrganizationUnit ou
JOIN organizationunit_desenvolvimento.OrganizationUnit_Class ouc ON ou.OrganizationUnitId = ouc.OrganizationUnitId
JOIN classification_desenvolvimento.Class c ON ouc.ClassId = c.ClassId
JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND cs.Constant = 'LIB_PLANO_ASSINATURA';
```

### 3.1 Verificar TODAS as Configurações do Tenant

```sql
-- ============================================================================
-- CONFIGURAÇÕES: Listar todas as configurações do tenant
-- ============================================================================
SELECT 
    c.Constant AS configuracao,
    COALESCE(
        CAST(ouvi.ValueInteger AS CHAR),
        CAST(ouvf.ValueFloat AS CHAR),
        ouvs.ValueString
    ) AS valor,
    CASE 
        WHEN ouvi.ValueInteger IS NOT NULL THEN 'Integer'
        WHEN ouvf.ValueFloat IS NOT NULL THEN 'Float'
        WHEN ouvs.ValueString IS NOT NULL THEN 'String'
    END AS tipo
FROM organizationunit_desenvolvimento.OrganizationUnit ou
CROSS JOIN classification_desenvolvimento.Class c
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnitValueInteger ouvi 
    ON ou.OrganizationUnitId = ouvi.OrganizationUnitId AND c.ClassId = ouvi.ClassId
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnitValueFloat ouvf 
    ON ou.OrganizationUnitId = ouvf.OrganizationUnitId AND c.ClassId = ouvf.ClassId
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnitValueString ouvs 
    ON ou.OrganizationUnitId = ouvs.OrganizationUnitId AND c.ClassId = ouvs.ClassId
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp'
  AND c.Constant LIKE 'CONFIG_%'
  AND (ouvi.ValueInteger IS NOT NULL OR ouvf.ValueFloat IS NOT NULL OR ouvs.ValueString IS NOT NULL)
ORDER BY c.Constant;
```

### 3.2 Resumo Executivo do Tenant

```sql
-- ============================================================================
-- RESUMO EXECUTIVO: Visão completa do tenant em uma única consulta
-- ============================================================================
SELECT 
    ou.OrganizationUnitId AS tenant_id,
    ou.URI AS tenant_uri,
    oun.Name AS tenant_nome,
    pai.URI AS produto_pai,
    c_plano.Constant AS plano,
    (SELECT COUNT(*) FROM organizationunit_desenvolvimento.OrganizationUnitValueInteger ouvi WHERE ouvi.OrganizationUnitId = ou.OrganizationUnitId) +
    (SELECT COUNT(*) FROM organizationunit_desenvolvimento.OrganizationUnitValueFloat ouvf WHERE ouvf.OrganizationUnitId = ou.OrganizationUnitId) +
    (SELECT COUNT(*) FROM organizationunit_desenvolvimento.OrganizationUnitValueString ouvs WHERE ouvs.OrganizationUnitId = ou.OrganizationUnitId) AS total_configs
FROM organizationunit_desenvolvimento.OrganizationUnit ou
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnitName oun ON ou.OrganizationUnitId = oun.OrganizationUnitId
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnit_OrganizationUnit ouou ON ou.OrganizationUnitId = ouou.OrganizationUnitId2
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnit pai ON ouou.OrganizationUnitId1 = pai.OrganizationUnitId
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnit_Class ouc ON ou.OrganizationUnitId = ouc.OrganizationUnitId
LEFT JOIN classification_desenvolvimento.Class c_plano ON ouc.ClassId = c_plano.ClassId
LEFT JOIN classification_desenvolvimento.ClassScheme cs_plano ON c_plano.ClassSchemeId = cs_plano.ClassSchemeId AND cs_plano.Constant = 'LIB_PLANO_ASSINATURA'
WHERE ou.URI = 'urn:llam:biblioteca:tenant:bmsp';
```

### 3.3 Listar Todos os Tenants do Produto

```sql
-- ============================================================================
-- LISTAR: Todos os tenants vinculados ao produto LLAM Biblioteca
-- ============================================================================
SELECT 
    filho.OrganizationUnitId AS tenant_id,
    filho.URI AS tenant_uri,
    oun.Name AS tenant_nome,
    c_plano.Constant AS plano
FROM organizationunit_desenvolvimento.OrganizationUnit pai
JOIN organizationunit_desenvolvimento.OrganizationUnit_OrganizationUnit ouou ON pai.OrganizationUnitId = ouou.OrganizationUnitId1
JOIN organizationunit_desenvolvimento.OrganizationUnit filho ON ouou.OrganizationUnitId2 = filho.OrganizationUnitId
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnitName oun ON filho.OrganizationUnitId = oun.OrganizationUnitId
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnit_Class ouc ON filho.OrganizationUnitId = ouc.OrganizationUnitId
LEFT JOIN classification_desenvolvimento.Class c_plano ON ouc.ClassId = c_plano.ClassId
LEFT JOIN classification_desenvolvimento.ClassScheme cs_plano ON c_plano.ClassSchemeId = cs_plano.ClassSchemeId AND cs_plano.Constant = 'LIB_PLANO_ASSINATURA'
WHERE pai.URI = 'urn:llam:produto:biblioteca';
```

### 3.4 Buscar Tenant por Domínio

```sql
-- ============================================================================
-- BUSCAR: Tenant pelo domínio personalizado
-- ============================================================================
SELECT 
    ou.OrganizationUnitId AS tenant_id,
    ou.URI AS tenant_uri,
    oun.Name AS tenant_nome,
    ouvs.ValueString AS dominio
FROM organizationunit_desenvolvimento.OrganizationUnit ou
LEFT JOIN organizationunit_desenvolvimento.OrganizationUnitName oun ON ou.OrganizationUnitId = oun.OrganizationUnitId
JOIN organizationunit_desenvolvimento.OrganizationUnitValueString ouvs ON ou.OrganizationUnitId = ouvs.OrganizationUnitId
JOIN classification_desenvolvimento.Class c ON ouvs.ClassId = c.ClassId
WHERE c.Constant = 'CONFIG_DOMINIO_PERSONALIZADO'
  AND ouvs.ValueString = 'biblioteca.sp.gov.br';
```

### 3.5 Verificar Pré-Requisitos (ClassSchemes e Classes)

```sql
-- ============================================================================
-- PRÉ-REQUISITOS: Verificar se ClassSchemes e Classes existem
-- ============================================================================

-- Verificar ClassScheme LIB_RELACAO_OU
SELECT 
    'ClassScheme LIB_RELACAO_OU' AS item,
    CASE WHEN COUNT(*) > 0 THEN '✅ OK' ELSE '❌ FALTANDO - Execute ITS-HU-01' END AS status
FROM classification_desenvolvimento.ClassScheme 
WHERE Constant = 'LIB_RELACAO_OU';

-- Verificar Class REL_OU_FILHO
SELECT 
    'Class REL_OU_FILHO' AS item,
    CASE WHEN COUNT(*) > 0 THEN '✅ OK' ELSE '❌ FALTANDO - Execute ITS-HU-01' END AS status
FROM classification_desenvolvimento.Class 
WHERE Constant = 'REL_OU_FILHO';

-- Verificar Classes de Configuração
SELECT 
    configs.Constant AS config_class,
    CASE WHEN c.ClassId IS NOT NULL THEN '✅ OK' ELSE '❌ FALTANDO' END AS status
FROM (
    SELECT 'CONFIG_PRAZO_EMPRESTIMO' AS Constant UNION ALL
    SELECT 'CONFIG_LIMITE_EMPRESTIMOS' UNION ALL
    SELECT 'CONFIG_LIMITE_RENOVACOES' UNION ALL
    SELECT 'CONFIG_VALOR_MULTA_DIA' UNION ALL
    SELECT 'CONFIG_PERMITE_RESERVA' UNION ALL
    SELECT 'CONFIG_PERMITE_VENDA_DIGITAL' UNION ALL
    SELECT 'CONFIG_TEMA_CORES' UNION ALL
    SELECT 'CONFIG_LOGO_URL' UNION ALL
    SELECT 'CONFIG_DOMINIO_PERSONALIZADO' UNION ALL
    SELECT 'PLANO_INSTITUCIONAL'
) configs
LEFT JOIN classification_desenvolvimento.Class c ON configs.Constant = c.Constant;
```

---

## 4. Endpoints da API

### 4.1 CRUD de Tenants

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/tenants` | Criar novo tenant |
| GET | `/api/tenants` | Listar todos os tenants |
| GET | `/api/tenants/{id}` | Buscar tenant por ID |
| PUT | `/api/tenants/{id}` | Atualizar tenant |
| DELETE | `/api/tenants/{id}` | Desativar tenant |

### 4.2 Configurações

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/tenants/{id}/config` | Buscar todas as configurações |
| PUT | `/api/tenants/{id}/config` | Atualizar configurações |
| PATCH | `/api/tenants/{id}/config/emprestimo` | Atualizar config de empréstimo |
| PATCH | `/api/tenants/{id}/config/tema` | Atualizar tema visual |

### 4.3 Identidade Visual

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/tenants/{id}/logo` | Upload de logo |
| DELETE | `/api/tenants/{id}/logo` | Remover logo |
| GET | `/api/tenants/{id}/tema` | Buscar tema de cores |

---

## 5. Keycloak - Configuração por Tenant

### 5.1 Realm por Tenant

Cada tenant pode ter seu próprio Realm no Keycloak ou compartilhar um Realm com grupos:

```
Realm: llam-biblioteca
├── Group: tenant-bmsp
│   ├── Role: bibliotecario
│   ├── Role: atendente
│   └── Role: leitor
├── Group: tenant-ufmg
│   ├── Role: bibliotecario
│   └── Role: leitor
```

### 5.2 Claims Customizadas

```json
{
  "sub": "user-uuid",
  "tenant_id": "uuid-do-tenant",
  "tenant_name": "Biblioteca Municipal de São Paulo",
  "roles": ["bibliotecario"],
  "perfil": "PERFIL_BIBLIOTECARIO"
}
```

---

## 6. Isolamento de Dados

### 6.1 Filtro por Tenant em Todas as Queries

```java
// Exemplo de filtro automático via Spring Data JPA
@Query("SELECT t FROM Titulo t WHERE t.tenantId = :tenantId")
List<Titulo> findAllByTenant(@Param("tenantId") String tenantId);

// Ou via Hibernate Filter
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
```

### 6.2 Interceptor de Tenant

```java
@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = extractTenantFromToken(request);
        TenantContext.setCurrentTenant(tenantId);
        return true;
    }
}
```

---

## 7. Critérios de Conclusão Técnica (DoD)

- [ ] ClassSchemes de tenant criados (ITS-HU-01)
- [ ] Integração com `organization-unit-backend` implementada
- [ ] CRUD de tenants funcionando
- [ ] Configurações por tenant funcionando
- [ ] Upload de logo funcionando
- [ ] Tema de cores aplicado no frontend
- [ ] Isolamento de dados validado
- [ ] Integração com Keycloak configurada

---

## 8. Observações

> **NOTA:** O tenant é uma OrganizationUnit filha de "LLAM Biblioteca" na hierarquia.

> **NOTA:** As configurações são armazenadas via OrganizationUnitValue* (Integer, Float, String).

> **NOTA:** O isolamento de dados é garantido via filtro de tenant em todas as queries.
