# Modelo de Dados CERIF - Organização no Banco de Dados

**Versão:** 1.0  
**Data:** 01 de Abril de 2026  
**Objetivo:** Documentar como os dados são organizados no modelo CERIF para Person, Classification e Transaction

---

## 📊 Visão Geral do Modelo CERIF

O modelo CERIF (Common European Research Information Format) utiliza um padrão de **entidade-atributo-valor (EAV)** combinado com **tabelas de ligação classificadas**. Isso permite flexibilidade máxima na definição de atributos e relacionamentos.

### Princípios Fundamentais

1. **Entidades Base**: Tabelas principais (Person, Transaction, DublinCore)
2. **Classificações**: Sistema hierárquico de ClassScheme → Class
3. **Tabelas de Ligação `*_Class`**: Relacionam entidades com classificações
4. **Tabelas de Valor `*Value*`**: Armazenam atributos tipados (String, Integer, Float, Date, Text)

---

## 🏛️ 1. Classification (Sistema de Classificação)

O sistema de classificação é o **coração do modelo CERIF**. Todas as outras entidades dependem dele.

### 1.1 Estrutura das Tabelas

```sql
-- ClassScheme: Agrupa classes relacionadas (ex: TIPO_PESSOA, STATUS_LEITOR)
CREATE TABLE ClassScheme (
  ClassSchemeId int NOT NULL AUTO_INCREMENT,
  Constant varchar(255) NOT NULL,      -- Identificador único (ex: 'LIB_STATUS_LEITOR')
  URI varchar(255) NOT NULL,           -- URI semântica
  PRIMARY KEY (ClassSchemeId),
  UNIQUE KEY (Constant)
);

-- Class: Classes individuais dentro de um scheme
CREATE TABLE Class (
  ClassId int NOT NULL AUTO_INCREMENT,
  ClassSchemeId int NOT NULL,          -- FK para ClassScheme
  Constant varchar(255) NOT NULL,      -- Identificador único (ex: 'LEITOR_ATIVO')
  URI varchar(255) NOT NULL,           -- URI semântica
  PRIMARY KEY (ClassId),
  UNIQUE KEY (Constant),
  FOREIGN KEY (ClassSchemeId) REFERENCES ClassScheme(ClassSchemeId)
);
```

### 1.2 Exemplo de Dados - ClassScheme e Class

```sql
-- Consulta: Listar ClassSchemes da Biblioteca
SELECT ClassSchemeId, Constant, URI 
FROM ClassScheme 
WHERE Constant LIKE 'LIB_%';

-- Resultado esperado:
-- | ClassSchemeId | Constant              | URI                                    |
-- |---------------|-----------------------|----------------------------------------|
-- | 45            | LIB_STATUS_LEITOR     | urn:llam:biblioteca:status:leitor      |
-- | 46            | LIB_TIPO_TRANSACAO    | urn:llam:biblioteca:tipo:transacao     |
-- | 47            | LIB_ATRIBUTO_TITULO   | urn:llam:biblioteca:atributo:titulo    |

-- Consulta: Listar Classes de um ClassScheme
SELECT c.ClassId, c.Constant, c.URI, cs.Constant AS Scheme
FROM Class c
JOIN ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId
WHERE cs.Constant = 'LIB_STATUS_LEITOR';

-- Resultado esperado:
-- | ClassId | Constant        | URI                                      | Scheme            |
-- |---------|-----------------|------------------------------------------|-------------------|
-- | 101     | LEITOR_ATIVO    | urn:llam:biblioteca:status:leitor:ativo  | LIB_STATUS_LEITOR |
-- | 102     | LEITOR_SUSPENSO | urn:llam:biblioteca:status:leitor:suspenso| LIB_STATUS_LEITOR |
-- | 103     | LEITOR_EXCLUIDO | urn:llam:biblioteca:status:leitor:excluido| LIB_STATUS_LEITOR |
```

### 1.3 Tabelas de Valor da Class

```sql
-- ClassValueString: Valores string associados a uma Class
CREATE TABLE ClassValueString (
  ClassId int NOT NULL,
  ValueString varchar(255) NOT NULL,
  FOREIGN KEY (ClassId) REFERENCES Class(ClassId)
);

-- ClassValueInteger, ClassValueFloat, ClassValueDate, ClassValueText
-- Seguem o mesmo padrão
```

### 1.4 Tabela de Ligação Class_Class

```sql
-- Class_Class: Relaciona classes entre si (hierarquia, equivalência, etc.)
CREATE TABLE Class_Class (
  ClassId1 int NOT NULL,    -- Classe origem
  ClassId2 int NOT NULL,    -- Classe destino
  ClassId int NOT NULL,     -- Tipo do relacionamento (também é uma Class!)
  UNIQUE KEY (ClassId1, ClassId2, ClassId),
  FOREIGN KEY (ClassId1) REFERENCES Class(ClassId),
  FOREIGN KEY (ClassId2) REFERENCES Class(ClassId),
  FOREIGN KEY (ClassId) REFERENCES Class(ClassId)
);

-- Exemplo: LEITOR_ATIVO é "subtipo de" PESSOA_FISICA
-- ClassId1 = 101 (LEITOR_ATIVO)
-- ClassId2 = 50 (PESSOA_FISICA)
-- ClassId = 10 (SUBTIPO_DE)
```

---

## 👤 2. Person (Pessoas)

### 2.1 Estrutura da Tabela Principal

```sql
CREATE TABLE Person (
  PersonId varchar(36) NOT NULL,       -- UUID
  BirthHour time NOT NULL,
  BirthDate date NOT NULL,
  BirthCity varchar(80) NOT NULL,
  BirthState varchar(2) NOT NULL,
  BirthCountry varchar(80) NOT NULL,
  MotherName varchar(120) NOT NULL,
  SHA1 varchar(40) NOT NULL,           -- Hash único para deduplicação
  URI varchar(128) DEFAULT NULL,       -- URI semântica (ex: urn:llam:biblioteca:leitor:uuid)
  PRIMARY KEY (PersonId),
  UNIQUE KEY (SHA1)
);
```

### 2.2 Tabela de Ligação Person_Class

A tabela `Person_Class` **classifica** uma pessoa. É assim que definimos o "tipo" ou "papel" de uma pessoa.

```sql
CREATE TABLE Person_Class (
  PersonId varchar(36) NOT NULL,
  ClassId int NOT NULL,
  FOREIGN KEY (PersonId) REFERENCES Person(PersonId),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId)
);

-- Exemplo: Marcar pessoa como LEITOR_ATIVO
INSERT INTO Person_Class (PersonId, ClassId) 
VALUES ('009c121a-538c-4a8d-adf6-997869172924', 101);

-- Consulta: Buscar todas as classificações de uma pessoa
SELECT p.PersonId, p.URI, c.Constant AS Classificacao
FROM Person p
JOIN Person_Class pc ON p.PersonId = pc.PersonId
JOIN classification.Class c ON pc.ClassId = c.ClassId
WHERE p.PersonId = '009c121a-538c-4a8d-adf6-997869172924';

-- Resultado:
-- | PersonId                             | URI                                | Classificacao   |
-- |--------------------------------------|------------------------------------|-----------------|
-- | 009c121a-538c-4a8d-adf6-997869172924 | urn:llam:biblioteca:leitor:tenant:1| LEITOR_ATIVO    |
```

### 2.3 Tabelas de Valor da Person (Padrão EVA)

```sql
-- PersonValueString: Atributos string da pessoa
CREATE TABLE PersonValueString (
  PersonId varchar(36) NOT NULL,
  ClassId int NOT NULL,                -- Define QUAL atributo (ex: CPF, RG, EMAIL)
  ValueString varchar(250) NOT NULL,   -- O valor do atributo
  PRIMARY KEY (PersonId, ClassId),
  FOREIGN KEY (PersonId) REFERENCES Person(PersonId),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId)
);

-- Exemplo: Armazenar CPF de uma pessoa
-- ClassId 200 = CPF (definido no ClassScheme DOCUMENTO_IDENTIFICACAO)
INSERT INTO PersonValueString (PersonId, ClassId, ValueString)
VALUES ('009c121a-538c-4a8d-adf6-997869172924', 200, '123.456.789-00');

-- Consulta: Buscar todos os atributos string de uma pessoa
SELECT p.PersonId, c.Constant AS Atributo, pvs.ValueString AS Valor
FROM Person p
JOIN PersonValueString pvs ON p.PersonId = pvs.PersonId
JOIN classification.Class c ON pvs.ClassId = c.ClassId
WHERE p.PersonId = '009c121a-538c-4a8d-adf6-997869172924';

-- Resultado:
-- | PersonId | Atributo | Valor          |
-- |----------|----------|----------------|
-- | 009c...  | CPF      | 123.456.789-00 |
-- | 009c...  | EMAIL    | teste@email.com|
-- | 009c...  | TELEFONE | 11999999999    |
```

### 2.4 Outras Tabelas de Ligação da Person

```sql
-- Person_FederatedIdentifier: Liga pessoa a identificadores federados
CREATE TABLE Person_FederatedIdentifier (
  PersonId varchar(36) NOT NULL,
  FederatedIdentifierId varchar(128) NOT NULL,
  ClassId int NOT NULL,                -- Tipo do identificador
  PRIMARY KEY (PersonId, FederatedIdentifierId, ClassId),
  FOREIGN KEY (PersonId) REFERENCES Person(PersonId),
  FOREIGN KEY (FederatedIdentifierId) REFERENCES organizationunit.FederatedIdentifier(FederatedIdentifierId),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId)
);

-- Person_PostAddress: Liga pessoa a endereços postais
CREATE TABLE Person_PostAddress (
  PersonId varchar(36) NOT NULL,
  PostAddressId varchar(36) NOT NULL,
  ClassId int NOT NULL,                -- Tipo do endereço (RESIDENCIAL, COMERCIAL)
  FOREIGN KEY (PersonId) REFERENCES Person(PersonId),
  FOREIGN KEY (PostAddressId) REFERENCES address.PostAddress(PostAddressId),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId)
);

-- Person_EletronicAddress: Liga pessoa a endereços eletrônicos
CREATE TABLE Person_EletronicAddress (
  PersonId varchar(36) NOT NULL,
  EletronicAddressId varchar(36) NOT NULL,
  ClassId int NOT NULL,                -- Tipo (EMAIL_PESSOAL, EMAIL_TRABALHO)
  PRIMARY KEY (PersonId, EletronicAddressId, ClassId),
  FOREIGN KEY (PersonId) REFERENCES Person(PersonId),
  FOREIGN KEY (EletronicAddressId) REFERENCES address.EletronicAddress(EletronicAddressId),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId)
);

-- PersonName_Person: Liga nomes a pessoas (permite múltiplos nomes)
CREATE TABLE PersonName_Person (
  PersonNameId varchar(36) NOT NULL,
  PersonId varchar(36) NOT NULL,
  ClassId int NOT NULL,                -- Tipo do nome (NOME_CIVIL, NOME_SOCIAL, APELIDO)
  PRIMARY KEY (PersonNameId, PersonId, ClassId),
  FOREIGN KEY (PersonNameId) REFERENCES PersonName(PersonNameId),
  FOREIGN KEY (PersonId) REFERENCES Person(PersonId),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId)
);
```

---

## 💰 3. Transaction (Transações)

### 3.1 Estrutura da Tabela Principal

```sql
CREATE TABLE Transaction (
  TransactionId varchar(36) NOT NULL,
  FederatedIdentifierId varchar(36) NOT NULL,  -- Quem fez a transação
  ClassId int NOT NULL,                        -- Tipo da transação (EMPRESTIMO, DEVOLUCAO, MULTA)
  Quantity decimal(15,4) NOT NULL,             -- Quantidade (positivo=entrada, negativo=saída)
  DocReference varchar(500) DEFAULT NULL,      -- Referência do documento
  TransactionDate datetime NOT NULL,           -- Data/hora da transação
  PRIMARY KEY (TransactionId),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId),
  FOREIGN KEY (FederatedIdentifierId) REFERENCES organizationunit.FederatedIdentifier(FederatedIdentifierId)
);
```

### 3.2 Exemplo de Dados - Empréstimo e Devolução

```sql
-- Consulta: Listar transações de um leitor
SELECT 
  t.TransactionId,
  t.TransactionDate,
  c.Constant AS TipoTransacao,
  t.Quantity,
  t.DocReference
FROM Transaction t
JOIN classification.Class c ON t.ClassId = c.ClassId
WHERE t.FederatedIdentifierId = '009c121a-538c-4a8d-adf6-997869172924'
ORDER BY t.TransactionDate DESC;

-- Resultado:
-- | TransactionId | TransactionDate     | TipoTransacao | Quantity | DocReference                                    |
-- |---------------|---------------------|---------------|----------|-------------------------------------------------|
-- | e7f225a6-...  | 2026-03-31 19:06:30 | EMPRESTIMO    | 1.0000   | urn:llam:biblioteca:1:emprestimo:e7f...:exemplar:cf8... |
-- | f8a336b7-...  | 2026-03-31 19:27:00 | DEVOLUCAO     | -1.0000  | urn:llam:biblioteca:1:emprestimo:e7f...:exemplar:cf8... |

-- Consulta: Saldo de empréstimos ativos de um leitor
SELECT 
  fi.FederatedIdentifierId,
  SUM(t.Quantity) AS SaldoEmprestimos
FROM Transaction t
JOIN organizationunit.FederatedIdentifier fi ON t.FederatedIdentifierId = fi.FederatedIdentifierId
JOIN classification.Class c ON t.ClassId = c.ClassId
WHERE c.Constant IN ('EMPRESTIMO', 'DEVOLUCAO')
  AND fi.FederatedIdentifierId = '009c121a-538c-4a8d-adf6-997869172924'
GROUP BY fi.FederatedIdentifierId;

-- Resultado:
-- | FederatedIdentifierId                | SaldoEmprestimos |
-- |--------------------------------------|------------------|
-- | 009c121a-538c-4a8d-adf6-997869172924 | 0.0000           |  -- (1 empréstimo - 1 devolução = 0)
```

### 3.3 Tabelas de Valor da Transaction

```sql
-- TransactionValueString: Atributos string da transação
CREATE TABLE TransactionValueString (
  TransactionId varchar(36) NOT NULL,
  ClassId int NOT NULL,                -- Define QUAL atributo
  Value varchar(500) NOT NULL,
  PRIMARY KEY (TransactionId, ClassId),
  FOREIGN KEY (TransactionId) REFERENCES Transaction(TransactionId),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId)
);

-- TransactionValueDate: Atributos de data da transação
CREATE TABLE TransactionValueDate (
  TransactionId varchar(36) NOT NULL,
  ClassId int NOT NULL,
  Value datetime NOT NULL,
  PRIMARY KEY (TransactionId, ClassId),
  FOREIGN KEY (TransactionId) REFERENCES Transaction(TransactionId),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId)
);

-- Exemplo: Armazenar data prevista de devolução
-- ClassId 301 = DATA_PREVISTA_DEVOLUCAO
INSERT INTO TransactionValueDate (TransactionId, ClassId, Value)
VALUES ('e7f225a6-7ac9-4416-ae9a-60113924f3af', 301, '2026-04-14 00:00:00');
```

---

## 🔗 4. Padrão das Tabelas de Ligação `*_Class`

### 4.1 Estrutura Comum

Todas as tabelas de ligação `*_Class` seguem o mesmo padrão:

```sql
CREATE TABLE [Entidade]_Class (
  [Entidade]Id varchar(36) NOT NULL,   -- FK para entidade principal
  ClassId int NOT NULL,                 -- FK para Class (classificação)
  FOREIGN KEY ([Entidade]Id) REFERENCES [Entidade]([Entidade]Id),
  FOREIGN KEY (ClassId) REFERENCES classification.Class(ClassId)
);
```

### 4.2 Propósito

| Tabela | Propósito |
|--------|-----------|
| `Person_Class` | Define o tipo/papel de uma pessoa (LEITOR, AUTOR, FUNCIONARIO) |
| `PersonName_Class` | Classifica um nome (NOME_CIVIL, NOME_SOCIAL) |
| `Class_Class` | Relaciona classes entre si (hierarquia, equivalência) |
| `ClassScheme_ClassScheme` | Relaciona schemes entre si |

### 4.3 Consulta Genérica para Entender Classificações

```sql
-- Consulta: Ver todas as classificações de uma entidade
SELECT 
  '[ENTIDADE]' AS Entidade,
  e.URI AS URIEntidade,
  c.Constant AS Classificacao,
  cs.Constant AS Scheme
FROM [Entidade] e
JOIN [Entidade]_Class ec ON e.[Entidade]Id = ec.[Entidade]Id
JOIN classification.Class c ON ec.ClassId = c.ClassId
JOIN classification.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId;
```

---

## 📐 5. Diagrama de Relacionamentos

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           CLASSIFICATION                                     │
│  ┌─────────────┐         ┌─────────────┐         ┌─────────────────────┐   │
│  │ ClassScheme │ 1───N → │    Class    │ ← N───N │    Class_Class      │   │
│  │             │         │             │         │ (ClassId1,ClassId2, │   │
│  │ - Constant  │         │ - Constant  │         │  ClassId)           │   │
│  │ - URI       │         │ - URI       │         └─────────────────────┘   │
│  └─────────────┘         └──────┬──────┘                                    │
│                                 │                                            │
│                    ┌────────────┼────────────┐                              │
│                    ▼            ▼            ▼                              │
│            ClassValue*    ClassTerm    ClassDefault                         │
└─────────────────────────────────────────────────────────────────────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    ▼                           ▼
┌───────────────────────────────┐   ┌───────────────────────────────┐
│            PERSON             │   │         TRANSACTION           │
│  ┌─────────────┐              │   │  ┌─────────────┐              │
│  │   Person    │              │   │  │ Transaction │              │
│  │             │              │   │  │             │              │
│  │ - PersonId  │              │   │  │ - ClassId   │◄─── Tipo     │
│  │ - URI       │              │   │  │ - Quantity  │              │
│  │ - SHA1      │              │   │  │ - DocRef    │              │
│  └──────┬──────┘              │   │  └──────┬──────┘              │
│         │                     │   │         │                     │
│    ┌────┴────┐                │   │    ┌────┴────┐                │
│    ▼         ▼                │   │    ▼         ▼                │
│ Person_   PersonValue*        │   │ Transaction  TransactionValue*│
│ Class     (String,Int,        │   │ (via ClassId (String,Int,     │
│           Float,Date,Text)    │   │  na tabela   Float,Date,Text) │
│                               │   │  principal)                   │
└───────────────────────────────┘   └───────────────────────────────┘
```

---

## 🎯 6. Consultas Úteis para Análise

### 6.1 Listar todos os ClassSchemes do sistema

```sql
SELECT ClassSchemeId, Constant, URI
FROM ClassScheme
ORDER BY Constant;
```

### 6.2 Listar Classes de um Scheme específico

```sql
SELECT c.ClassId, c.Constant, c.URI
FROM Class c
JOIN ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId
WHERE cs.Constant = 'LIB_STATUS_LEITOR'
ORDER BY c.Constant;
```

### 6.3 Contar entidades por classificação

```sql
SELECT c.Constant AS Classificacao, COUNT(*) AS Total
FROM Person_Class pc
JOIN Class c ON pc.ClassId = c.ClassId
GROUP BY c.Constant
ORDER BY Total DESC;
```

### 6.4 Listar atributos EVA de uma pessoa

```sql
-- String
SELECT 'String' AS Tipo, c.Constant AS Atributo, pvs.ValueString AS Valor
FROM PersonValueString pvs
JOIN Class c ON pvs.ClassId = c.ClassId
WHERE pvs.PersonId = '009c121a-538c-4a8d-adf6-997869172924'
UNION ALL
-- Integer
SELECT 'Integer', c.Constant, CAST(pvi.ValueInteger AS CHAR)
FROM PersonValueInteger pvi
JOIN Class c ON pvi.ClassId = c.ClassId
WHERE pvi.PersonId = '009c121a-538c-4a8d-adf6-997869172924'
UNION ALL
-- Date
SELECT 'Date', c.Constant, CAST(pvd.ValueDate AS CHAR)
FROM PersonValueDate pvd
JOIN Class c ON pvd.ClassId = c.ClassId
WHERE pvd.PersonId = '009c121a-538c-4a8d-adf6-997869172924';
```

### 6.5 Histórico de transações por tipo

```sql
SELECT 
  DATE(t.TransactionDate) AS Data,
  c.Constant AS Tipo,
  COUNT(*) AS Quantidade,
  SUM(t.Quantity) AS SaldoMovimento
FROM Transaction t
JOIN Class c ON t.ClassId = c.ClassId
GROUP BY DATE(t.TransactionDate), c.Constant
ORDER BY Data DESC, Tipo;
```

---

## 📚 Referências

- **CERIF 1.6**: [https://www.eurocris.org/cerif/main-features-cerif](https://www.eurocris.org/cerif/main-features-cerif)
- **DDL Person**: `/LLAM/Ecos/person-backend/docs/ddl.sql`
- **DDL Transaction**: `/Ecos/transaction-backend/docs/ddl.sql`
- **DDL Classification**: `/LLAM/Ecos/class-backend/docs/ddl.sql`
