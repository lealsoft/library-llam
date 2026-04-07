# ITS-HU-00 — Aperfeiçoamentos Necessários no Ecossistema LLAM

Escopo: documentação das entidades de relacionamento que precisam ser criadas no ecossistema Ecos antes de implementar a library-api.

Relacionado:
- ITS: `./ITS-HU-01-modelagem-dados-biblioteca.md`
- ITS: `./ITS-HU-02-configuracao-multi-tenant.md`
- ITS: `./ITS-HU-03-cadastro-titulos.md`
- ITS: `./ITS-HU-05-cadastro-leitores.md`

---

## 1. Análise do Ecossistema Atual

### 1.1 Backends Existentes

| Backend | Entidades Principais | Status |
|---------|---------------------|--------|
| `class-backend` | ClassScheme, Class, ClassClass, ClassValue*, ClassTerm | ✅ Completo |
| `dublin-core-backend` | DublinCore, DublinCoreTitle, DublinCoreCreator, etc. (27 entidades) | ✅ Completo |
| `transaction-backend` | Transaction, TransactionValue* | ✅ Completo |
| `person-backend` | Person, PersonName, PersonValue*, PersonClass, Person_DublinCore | ✅ Completo |
| `organization-unit-backend` | OrganizationUnit, OrganizationUnitValue*, OrganizationUnit_OrganizationUnit | ✅ Completo |
| `address-backend` | PostAddress, ElectronicAddress | ✅ Completo |
| `language-backend` | Language | ✅ Completo |
| `situation-backend` | Situation, SituationValue* | ✅ Completo |
| `mediavitae-dtos` | DTOs compartilhados | ✅ Completo |

### 1.2 Entidades de Relacionamento Existentes

| Entidade | Backend | Uso |
|----------|---------|-----|
| `ClassClass` | class-backend | Relacionamento entre Classes |
| `ClassSchemeClassScheme` | class-backend | Hierarquia de ClassSchemes |
| `OrganizationUnit_OrganizationUnit` | organization-unit-backend | Hierarquia de OUs (tenants) |
| `Person_DublinCore` | person-backend | Pessoa → Documento Dublin Core |
| `PersonPerson` | person-backend | Relacionamento entre Pessoas |

---

## 2. Entidades de Relacionamento Necessárias

### 2.1 `Person_OrganizationUnit` — CRÍTICO ⚠️

**Necessidade:** Vincular leitores (Person) aos tenants (OrganizationUnit/Biblioteca).

**Uso no Library-LLAM:**
- Leitor pertence a uma biblioteca específica
- Isolamento de dados por tenant
- Consulta de leitores por biblioteca

**Estrutura Proposta:**

```java
// person-backend/src/main/java/br/tec/llam/mediavitae/person/model/Person_OrganizationUnit.java
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Person_OrganizationUnit")
public class Person_OrganizationUnit {

    @EmbeddedId
    private Person_OrganizationUnitId id;
    // id.personId, id.organizationUnitId, id.classId
}
```

```java
// mediavitae-dtos/src/main/java/org/mediavitae/dto/person/Person_OrganizationUnitDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person_OrganizationUnitDTO {
    private String personId;
    private String organizationUnitId;
    private Integer classId;
}
```

**DDL — Desenvolvimento:**

```sql
CREATE TABLE IF NOT EXISTS `person_desenvolvimento`.`Person_OrganizationUnit` (
  `PersonId` varchar(36) NOT NULL,
  `OrganizationUnitId` varchar(36) NOT NULL,
  `ClassId` int NOT NULL,
  PRIMARY KEY (`PersonId`,`OrganizationUnitId`,`ClassId`),
  KEY `IDX_POU_PERSON` (`PersonId`),
  KEY `IDX_POU_OU` (`OrganizationUnitId`),
  KEY `IDX_POU_CLASS` (`ClassId`),
  CONSTRAINT `FK_POU_PERSON` FOREIGN KEY (`PersonId`) REFERENCES `person_desenvolvimento`.`Person` (`PersonId`),
  CONSTRAINT `FK_POU_OU` FOREIGN KEY (`OrganizationUnitId`) REFERENCES `organizationunit_desenvolvimento`.`OrganizationUnit` (`OrganizationUnitId`),
  CONSTRAINT `FK_POU_CLASS` FOREIGN KEY (`ClassId`) REFERENCES `classification_desenvolvimento`.`Class` (`ClassId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

**DDL — Homologação:**

```sql
CREATE TABLE IF NOT EXISTS `person_homologacao`.`Person_OrganizationUnit` (
  `PersonId` varchar(36) NOT NULL,
  `OrganizationUnitId` varchar(36) NOT NULL,
  `ClassId` int NOT NULL,
  PRIMARY KEY (`PersonId`,`OrganizationUnitId`,`ClassId`),
  KEY `IDX_POU_PERSON` (`PersonId`),
  KEY `IDX_POU_OU` (`OrganizationUnitId`),
  KEY `IDX_POU_CLASS` (`ClassId`),
  CONSTRAINT `FK_POU_PERSON` FOREIGN KEY (`PersonId`) REFERENCES `person_homologacao`.`Person` (`PersonId`),
  CONSTRAINT `FK_POU_OU` FOREIGN KEY (`OrganizationUnitId`) REFERENCES `organizationunit_homologacao`.`OrganizationUnit` (`OrganizationUnitId`),
  CONSTRAINT `FK_POU_CLASS` FOREIGN KEY (`ClassId`) REFERENCES `classification_homologacao`.`Class` (`ClassId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

**DDL — Produção:**

```sql
CREATE TABLE IF NOT EXISTS `person_producao`.`Person_OrganizationUnit` (
  `PersonId` varchar(36) NOT NULL,
  `OrganizationUnitId` varchar(36) NOT NULL,
  `ClassId` int NOT NULL,
  PRIMARY KEY (`PersonId`,`OrganizationUnitId`,`ClassId`),
  KEY `IDX_POU_PERSON` (`PersonId`),
  KEY `IDX_POU_OU` (`OrganizationUnitId`),
  KEY `IDX_POU_CLASS` (`ClassId`),
  CONSTRAINT `FK_POU_PERSON` FOREIGN KEY (`PersonId`) REFERENCES `person_producao`.`Person` (`PersonId`),
  CONSTRAINT `FK_POU_OU` FOREIGN KEY (`OrganizationUnitId`) REFERENCES `organizationunit_producao`.`OrganizationUnit` (`OrganizationUnitId`),
  CONSTRAINT `FK_POU_CLASS` FOREIGN KEY (`ClassId`) REFERENCES `classification_producao`.`Class` (`ClassId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

### 2.2 `DublinCore_Class` — IMPORTANTE

**Necessidade:** Vincular títulos (DublinCore) a categorias e exemplares (Class).

**Uso no Library-LLAM:**
- Título pertence a categorias dinâmicas
- Título possui exemplares físicos
- Classificação temática do acervo

**Estrutura Proposta:**

```java
// dublin-core-backend/src/main/java/br/tec/llam/dublincore/dublincore/entity/DublinCore_Class.java
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DublinCore_Class")
public class DublinCore_Class {

    @EmbeddedId
    private DublinCore_ClassId id;
    // id.dublinCoreId, id.classId, id.classSchemeId
}
```

```java
// mediavitae-dtos/src/main/java/org/mediavitae/dto/dublincore/DublinCore_ClassDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DublinCore_ClassDTO {
    private String dublinCoreId;
    private Integer classId;
    private Integer classSchemeId;
}
```

**DDL — Desenvolvimento:**

```sql
CREATE TABLE IF NOT EXISTS `dublincore_desenvolvimento`.`DublinCore_Class` (
  `DublinCoreId` varchar(36) NOT NULL,
  `ClassId` int NOT NULL,
  `ClassSchemeId` int NOT NULL,
  PRIMARY KEY (`DublinCoreId`,`ClassId`,`ClassSchemeId`),
  KEY `IDX_DCC_DC` (`DublinCoreId`),
  KEY `IDX_DCC_CLASS` (`ClassId`),
  KEY `IDX_DCC_SCHEME` (`ClassSchemeId`),
  CONSTRAINT `FK_DCC_DC` FOREIGN KEY (`DublinCoreId`) REFERENCES `dublincore_desenvolvimento`.`DublinCore` (`DublinCoreId`),
  CONSTRAINT `FK_DCC_CLASS` FOREIGN KEY (`ClassId`) REFERENCES `classification_desenvolvimento`.`Class` (`ClassId`),
  CONSTRAINT `FK_DCC_SCHEME` FOREIGN KEY (`ClassSchemeId`) REFERENCES `classification_desenvolvimento`.`ClassScheme` (`ClassSchemeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

**DDL — Homologação:**

```sql
CREATE TABLE IF NOT EXISTS `dublincore_homologacao`.`DublinCore_Class` (
  `DublinCoreId` varchar(36) NOT NULL,
  `ClassId` int NOT NULL,
  `ClassSchemeId` int NOT NULL,
  PRIMARY KEY (`DublinCoreId`,`ClassId`,`ClassSchemeId`),
  KEY `IDX_DCC_DC` (`DublinCoreId`),
  KEY `IDX_DCC_CLASS` (`ClassId`),
  KEY `IDX_DCC_SCHEME` (`ClassSchemeId`),
  CONSTRAINT `FK_DCC_DC` FOREIGN KEY (`DublinCoreId`) REFERENCES `dublincore_homologacao`.`DublinCore` (`DublinCoreId`),
  CONSTRAINT `FK_DCC_CLASS` FOREIGN KEY (`ClassId`) REFERENCES `classification_homologacao`.`Class` (`ClassId`),
  CONSTRAINT `FK_DCC_SCHEME` FOREIGN KEY (`ClassSchemeId`) REFERENCES `classification_homologacao`.`ClassScheme` (`ClassSchemeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

**DDL — Produção:**

```sql
CREATE TABLE IF NOT EXISTS `dublincore_producao`.`DublinCore_Class` (
  `DublinCoreId` varchar(36) NOT NULL,
  `ClassId` int NOT NULL,
  `ClassSchemeId` int NOT NULL,
  PRIMARY KEY (`DublinCoreId`,`ClassId`,`ClassSchemeId`),
  KEY `IDX_DCC_DC` (`DublinCoreId`),
  KEY `IDX_DCC_CLASS` (`ClassId`),
  KEY `IDX_DCC_SCHEME` (`ClassSchemeId`),
  CONSTRAINT `FK_DCC_DC` FOREIGN KEY (`DublinCoreId`) REFERENCES `dublincore_producao`.`DublinCore` (`DublinCoreId`),
  CONSTRAINT `FK_DCC_CLASS` FOREIGN KEY (`ClassId`) REFERENCES `classification_producao`.`Class` (`ClassId`),
  CONSTRAINT `FK_DCC_SCHEME` FOREIGN KEY (`ClassSchemeId`) REFERENCES `classification_producao`.`ClassScheme` (`ClassSchemeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

### 2.3 `DublinCore_OrganizationUnit` — IMPORTANTE

**Necessidade:** Vincular títulos (DublinCore) aos tenants (OrganizationUnit).

**Uso no Library-LLAM:**
- Título pertence a uma biblioteca específica
- Isolamento de dados por tenant
- Consulta de títulos por biblioteca

**Estrutura Proposta:**

```java
// dublin-core-backend/src/main/java/br/tec/llam/dublincore/dublincore/entity/DublinCore_OrganizationUnit.java
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "DublinCore_OrganizationUnit")
public class DublinCore_OrganizationUnit {

    @EmbeddedId
    private DublinCore_OrganizationUnitId id;
    // id.dublinCoreId, id.organizationUnitId, id.classId
}
```

```java
// mediavitae-dtos/src/main/java/org/mediavitae/dto/dublincore/DublinCore_OrganizationUnitDTO.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DublinCore_OrganizationUnitDTO {
    private String dublinCoreId;
    private String organizationUnitId;
    private Integer classId;
}
```

**DDL — Desenvolvimento:**

```sql
CREATE TABLE IF NOT EXISTS `dublincore_desenvolvimento`.`DublinCore_OrganizationUnit` (
  `DublinCoreId` varchar(36) NOT NULL,
  `OrganizationUnitId` varchar(36) NOT NULL,
  `ClassId` int NOT NULL,
  PRIMARY KEY (`DublinCoreId`,`OrganizationUnitId`,`ClassId`),
  KEY `IDX_DCOU_DC` (`DublinCoreId`),
  KEY `IDX_DCOU_OU` (`OrganizationUnitId`),
  KEY `IDX_DCOU_CLASS` (`ClassId`),
  CONSTRAINT `FK_DCOU_DC` FOREIGN KEY (`DublinCoreId`) REFERENCES `dublincore_desenvolvimento`.`DublinCore` (`DublinCoreId`),
  CONSTRAINT `FK_DCOU_OU` FOREIGN KEY (`OrganizationUnitId`) REFERENCES `organizationunit_desenvolvimento`.`OrganizationUnit` (`OrganizationUnitId`),
  CONSTRAINT `FK_DCOU_CLASS` FOREIGN KEY (`ClassId`) REFERENCES `classification_desenvolvimento`.`Class` (`ClassId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

**DDL — Homologação:**

```sql
CREATE TABLE IF NOT EXISTS `dublincore_homologacao`.`DublinCore_OrganizationUnit` (
  `DublinCoreId` varchar(36) NOT NULL,
  `OrganizationUnitId` varchar(36) NOT NULL,
  `ClassId` int NOT NULL,
  PRIMARY KEY (`DublinCoreId`,`OrganizationUnitId`,`ClassId`),
  KEY `IDX_DCOU_DC` (`DublinCoreId`),
  KEY `IDX_DCOU_OU` (`OrganizationUnitId`),
  KEY `IDX_DCOU_CLASS` (`ClassId`),
  CONSTRAINT `FK_DCOU_DC` FOREIGN KEY (`DublinCoreId`) REFERENCES `dublincore_homologacao`.`DublinCore` (`DublinCoreId`),
  CONSTRAINT `FK_DCOU_OU` FOREIGN KEY (`OrganizationUnitId`) REFERENCES `organizationunit_homologacao`.`OrganizationUnit` (`OrganizationUnitId`),
  CONSTRAINT `FK_DCOU_CLASS` FOREIGN KEY (`ClassId`) REFERENCES `classification_homologacao`.`Class` (`ClassId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

**DDL — Produção:**

```sql
CREATE TABLE IF NOT EXISTS `dublincore_producao`.`DublinCore_OrganizationUnit` (
  `DublinCoreId` varchar(36) NOT NULL,
  `OrganizationUnitId` varchar(36) NOT NULL,
  `ClassId` int NOT NULL,
  PRIMARY KEY (`DublinCoreId`,`OrganizationUnitId`,`ClassId`),
  KEY `IDX_DCOU_DC` (`DublinCoreId`),
  KEY `IDX_DCOU_OU` (`OrganizationUnitId`),
  KEY `IDX_DCOU_CLASS` (`ClassId`),
  CONSTRAINT `FK_DCOU_DC` FOREIGN KEY (`DublinCoreId`) REFERENCES `dublincore_producao`.`DublinCore` (`DublinCoreId`),
  CONSTRAINT `FK_DCOU_OU` FOREIGN KEY (`OrganizationUnitId`) REFERENCES `organizationunit_producao`.`OrganizationUnit` (`OrganizationUnitId`),
  CONSTRAINT `FK_DCOU_CLASS` FOREIGN KEY (`ClassId`) REFERENCES `classification_producao`.`Class` (`ClassId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---

## 3. Arquivos a Criar por Backend

### 3.1 person-backend

```
src/main/java/br/tec/llam/mediavitae/person/
├── model/
│   ├── Person_OrganizationUnit.java           # CRIAR
│   └── embeddable/
│       └── Person_OrganizationUnitId.java     # CRIAR
├── repository/
│   └── Person_OrganizationUnitRepository.java # CRIAR
├── service/
│   └── Person_OrganizationUnitService.java    # CRIAR
├── controller/
│   └── Person_OrganizationUnitController.java # CRIAR
├── assembler/
│   └── Person_OrganizationUnitAssembler.java  # CRIAR
└── exceptions/
    └── Person_OrganizationUnitNotFoundException.java # CRIAR
```

### 3.2 dublin-core-backend

```
src/main/java/br/tec/llam/dublincore/dublincore/
├── entity/
│   ├── DublinCore_Class.java                  # CRIAR
│   ├── DublinCore_OrganizationUnit.java       # CRIAR
│   └── embeddable/
│       ├── DublinCore_ClassId.java            # CRIAR
│       └── DublinCore_OrganizationUnitId.java # CRIAR
├── repository/
│   ├── DublinCore_ClassRepository.java        # CRIAR
│   └── DublinCore_OrganizationUnitRepository.java # CRIAR
├── service/
│   ├── DublinCore_ClassService.java           # CRIAR
│   └── DublinCore_OrganizationUnitService.java # CRIAR
├── controller/
│   ├── DublinCore_ClassController.java        # CRIAR
│   └── DublinCore_OrganizationUnitController.java # CRIAR
└── assembler/
    ├── DublinCore_ClassAssembler.java         # CRIAR
    └── DublinCore_OrganizationUnitAssembler.java # CRIAR
```

### 3.3 mediavitae-dtos

```
src/main/java/org/mediavitae/dto/
├── person/
│   └── Person_OrganizationUnitDTO.java        # CRIAR
└── dublincore/
    ├── DublinCore_ClassDTO.java               # CRIAR
    └── DublinCore_OrganizationUnitDTO.java    # CRIAR
```

---

## 4. Ordem de Implementação

### Fase 1 — DTOs (mediavitae-dtos)
1. Criar `Person_OrganizationUnitDTO`
2. Criar `DublinCore_ClassDTO`
3. Criar `DublinCore_OrganizationUnitDTO`
4. Incrementar versão do artefato
5. Deploy no Nexus/Maven local

### Fase 2 — person-backend
1. Criar entidade `Person_OrganizationUnit`
2. Criar embeddable `Person_OrganizationUnitId`
3. Criar repository, service, controller, assembler
4. Criar migrations Flyway
5. Testes unitários e de integração

### Fase 3 — dublin-core-backend
1. Criar entidades `DublinCore_Class` e `DublinCore_OrganizationUnit`
2. Criar embeddables correspondentes
3. Criar repositories, services, controllers, assemblers
4. Criar migrations Flyway
5. Testes unitários e de integração

### Fase 4 — library-api
1. Criar projeto library-api em Java 17
2. Consumir backends via REST (Feign/WebClient)
3. Implementar lógica de negócio específica da biblioteca

---

## 5. Alternativa: Usar ClassClass como Workaround

Caso não seja possível criar as entidades de relacionamento imediatamente, é possível usar `ClassClass` como workaround:

| Relacionamento | Workaround com ClassClass |
|----------------|---------------------------|
| Person → OrganizationUnit | `ClassClass(PersonId, OrganizationUnitId, LIB_RELACAO_LEITOR_TENANT)` |
| DublinCore → Class | `ClassClass(DublinCoreId, ClassId, LIB_RELACAO_TITULO_CATEGORIA)` |
| DublinCore → OrganizationUnit | `ClassClass(DublinCoreId, OrganizationUnitId, LIB_TENANT)` |

**Desvantagens do workaround:**
- Menos semântico (ClassClass é genérico)
- Queries mais complexas
- Não segue o padrão CERIF estrito
- Pode gerar confusão com outros usos de ClassClass

---

## 6. Critérios de Conclusão

- [ ] DTOs criados no mediavitae-dtos
- [ ] Person_OrganizationUnit implementado no person-backend
- [ ] DublinCore_Class implementado no dublin-core-backend
- [ ] DublinCore_OrganizationUnit implementado no dublin-core-backend
- [ ] Migrations Flyway criadas para cada tabela
- [ ] Testes de integração passando
- [ ] Documentação de API (Swagger) atualizada

---

## 7. Observações

> **NOTA:** Estas entidades seguem o padrão CERIF de relacionamento N:N com ClassId para tipificação.

> **NOTA:** O ClassId no relacionamento permite diferenciar tipos de vínculo (ex: leitor ativo vs. leitor suspenso).

> **NOTA:** Após criar estas entidades, a library-api poderá ser implementada consumindo os backends via REST.

---

*Documento gerado em Março/2026 — LLAM Tecnologia do Brasil Ltda*
