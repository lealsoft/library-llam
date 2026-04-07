# ITS-HU-05 — Cadastro de Leitores

Escopo: especificação técnica da HU-05 cobrindo o cadastro e gestão de leitores como Person vinculadas ao tenant.

Relacionado:
- HU: `../HUs/hu-05-cadastro-leitores.md`
- HU: `../HUs/hu-14-autenticacao-keycloak.md`
- ITS: `./ITS-HU-01-modelagem-dados-biblioteca.md`

---

## 1. Modelo de Dados

### 1.1 Leitor como Person

O leitor é uma **Person** do ecossistema CERIF, vinculada ao tenant via **Person_OrganizationUnit**:

```
Person (Leitor)
├── PersonValueString (CPF, RG, telefone)
├── PersonValueDate (data nascimento)
├── PersonValueInteger (status)
└── Person_OrganizationUnit (vínculo ao tenant)
```

### 1.2 Estrutura de Dados

```json
{
  "personId": "uuid-do-leitor",
  "name": "João Silva Santos",
  "email": "joao.silva@email.com",
  "federatedIdentifierId": "keycloak-user-id",
  "cpf": "123.456.789-00",
  "rg": "12.345.678-9",
  "telefone": "(11) 99999-9999",
  "dataNascimento": "1990-05-15",
  "endereco": {
    "logradouro": "Rua das Flores, 123",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01234-567"
  },
  "status": "LEITOR_ATIVO",
  "plano": "PLANO_BASICO",
  "tenantId": "uuid-do-tenant"
}
```

---

## 2. Scripts SQL

### 2.1 Resolução de IDs

```sql
-- ============================================================================
-- RESOLUÇÃO DE IDs - LEITORES
-- ============================================================================

-- Status de Leitor
SET @id_leitor_ativo := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_LEITOR' 
    AND c.Constant = 'LEITOR_ATIVO' LIMIT 1
);

SET @id_leitor_inativo := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_LEITOR' 
    AND c.Constant = 'LEITOR_INATIVO' LIMIT 1
);

SET @id_leitor_suspenso := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_LEITOR' 
    AND c.Constant = 'LEITOR_SUSPENSO' LIMIT 1
);

SET @id_leitor_bloqueado := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_LEITOR' 
    AND c.Constant = 'LEITOR_BLOQUEADO' LIMIT 1
);

SET @id_leitor_inadimplente := (
    SELECT c.ClassId FROM classification_desenvolvimento.Class c 
    JOIN classification_desenvolvimento.ClassScheme cs ON c.ClassSchemeId = cs.ClassSchemeId 
    WHERE cs.Constant = 'LIB_STATUS_LEITOR' 
    AND c.Constant = 'LEITOR_INADIMPLENTE' LIMIT 1
);

-- Relação Leitor-Tenant
SET @id_rel_leitor_tenant := (
    SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme 
    WHERE Constant = 'LIB_RELACAO_LEITOR_TENANT' LIMIT 1
);
```

### 2.2 Cadastrar Leitor

```sql
-- ============================================================================
-- EXEMPLO: Cadastrar leitor "João Silva Santos"
-- ============================================================================

SET @uuid_leitor := UUID();
SET @federated_id := UUID();  -- Será atualizado após criar no Keycloak

-- 1. Criar Person
INSERT INTO person_desenvolvimento.Person (
    PersonId,
    Name,
    FederatedIdentifierId
) VALUES (
    @uuid_leitor,
    'João Silva Santos',
    @federated_id
);

-- 2. Adicionar CPF
INSERT INTO person_desenvolvimento.PersonValueString (
    PersonId,
    ClassId,
    StringValue
) VALUES (
    @uuid_leitor,
    (SELECT ClassId FROM classification_desenvolvimento.Class WHERE Constant = 'ATTR_CPF' LIMIT 1),
    '12345678900'  -- Sem formatação
);

-- 3. Adicionar telefone
INSERT INTO person_desenvolvimento.PersonValueString (
    PersonId,
    ClassId,
    StringValue
) VALUES (
    @uuid_leitor,
    (SELECT ClassId FROM classification_desenvolvimento.Class WHERE Constant = 'ATTR_TELEFONE' LIMIT 1),
    '11999999999'
);

-- 4. Adicionar data de nascimento
INSERT INTO person_desenvolvimento.PersonValueDate (
    PersonId,
    ClassId,
    DateValue
) VALUES (
    @uuid_leitor,
    (SELECT ClassId FROM classification_desenvolvimento.Class WHERE Constant = 'ATTR_DATA_NASCIMENTO' LIMIT 1),
    '1990-05-15'
);

-- 5. Definir status inicial (ATIVO)
INSERT INTO person_desenvolvimento.PersonValueInteger (
    PersonId,
    ClassId,
    IntegerValue
) VALUES (
    @uuid_leitor,
    (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_STATUS_LEITOR'),
    @id_leitor_ativo
);

-- 6. Vincular ao tenant
INSERT INTO person_desenvolvimento.Person_OrganizationUnit (
    PersonId,
    OrganizationUnitId,
    ClassId
) VALUES (
    @uuid_leitor,
    @tenant_id,
    @id_rel_leitor_tenant
);

-- 7. Vincular plano de assinatura
INSERT INTO person_desenvolvimento.Person_Class (
    PersonId,
    ClassId,
    ClassSchemeId
) VALUES (
    @uuid_leitor,
    @id_plano_basico,
    (SELECT ClassSchemeId FROM classification_desenvolvimento.ClassScheme WHERE Constant = 'LIB_PLANO_ASSINATURA')
);
```

---

## 3. Integração Keycloak

### 3.1 Criar Usuário no Keycloak

```java
@Service
public class KeycloakUserService {

    @Autowired
    private Keycloak keycloak;
    
    public String criarUsuarioLeitor(LeitorRequest request, String tenantId) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(extrairPrimeiroNome(request.getNome()));
        user.setLastName(extrairSobrenome(request.getNome()));
        user.setEnabled(true);
        user.setEmailVerified(false);
        
        // Atributos customizados
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("tenant_id", List.of(tenantId));
        attributes.put("cpf", List.of(request.getCpf()));
        user.setAttributes(attributes);
        
        // Criar usuário
        Response response = keycloak.realm("llam-biblioteca")
            .users()
            .create(user);
        
        String userId = extractUserId(response);
        
        // Adicionar ao grupo do tenant
        keycloak.realm("llam-biblioteca")
            .users()
            .get(userId)
            .joinGroup(getGrupoTenant(tenantId));
        
        // Atribuir role de leitor
        keycloak.realm("llam-biblioteca")
            .users()
            .get(userId)
            .roles()
            .realmLevel()
            .add(List.of(getRoleLeitor()));
        
        // Enviar e-mail de definição de senha
        keycloak.realm("llam-biblioteca")
            .users()
            .get(userId)
            .executeActionsEmail(List.of("UPDATE_PASSWORD"));
        
        return userId;
    }
}
```

### 3.2 Claims JWT

```json
{
  "sub": "keycloak-user-id",
  "email": "joao.silva@email.com",
  "name": "João Silva Santos",
  "tenant_id": "uuid-do-tenant",
  "tenant_name": "Biblioteca Municipal de São Paulo",
  "roles": ["leitor"],
  "perfil": "PERFIL_LEITOR",
  "cpf": "12345678900"
}
```

---

## 4. Endpoints da API

### 4.1 CRUD de Leitores

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/leitores` | Cadastrar leitor |
| GET | `/api/leitores` | Listar leitores do tenant |
| GET | `/api/leitores/{id}` | Buscar leitor por ID |
| GET | `/api/leitores?cpf={cpf}` | Buscar por CPF |
| PUT | `/api/leitores/{id}` | Atualizar leitor |
| PATCH | `/api/leitores/{id}/status` | Alterar status |
| DELETE | `/api/leitores/{id}` | Desativar leitor |

### 4.2 Operações Especiais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/leitores/{id}/reset-senha` | Enviar e-mail de reset |
| GET | `/api/leitores/{id}/emprestimos` | Listar empréstimos |
| GET | `/api/leitores/{id}/historico` | Histórico completo |
| GET | `/api/leitores/{id}/multas` | Multas pendentes |

---

## 5. Validações

### 5.1 Campos Obrigatórios

| Campo | Validação |
|-------|-----------|
| Nome | Não vazio, 3-200 caracteres |
| E-mail | Formato válido, único no tenant |
| CPF | Formato válido, único no tenant |

### 5.2 Regras de Negócio

| Regra | Validação |
|-------|-----------|
| CPF único | Não pode haver CPF duplicado no mesmo tenant |
| E-mail único | Não pode haver e-mail duplicado no mesmo tenant |
| Senha segura | Mínimo 8 caracteres, 1 maiúscula, 1 número |
| Desativação | Leitor com empréstimos ativos não pode ser desativado |

---

## 6. Critérios de Conclusão Técnica (DoD)

- [ ] Integração com `person-backend` implementada
- [ ] Integração com Keycloak implementada
- [ ] CRUD de leitores funcionando
- [ ] Validação de CPF/e-mail únicos
- [ ] Vínculo com tenant funcionando
- [ ] Reset de senha funcionando
- [ ] Consulta de empréstimos/multas funcionando

---

## 7. Observações

> **NOTA:** O leitor é uma Person do ecossistema CERIF, não uma tabela separada.

> **NOTA:** A senha é gerenciada pelo Keycloak, nunca armazenada no banco de dados.

> **NOTA:** O FederatedIdentifierId é o ID do usuário no Keycloak.
