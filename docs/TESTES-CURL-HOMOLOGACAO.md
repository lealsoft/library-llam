# 🧪 Testes CURL - Library API (Homologação)

**Data:** 29/03/2026  
**Ambiente:** Local (localhost:8090) → Homologação (*.llam.tec.br)

---

## 🎯 MVP - FLUXO DE DEMONSTRAÇÃO

> **Use esta seção para a apresentação ao cliente.** O fluxo demonstra o CRUD completo de Usuários, Livros e Empréstimos.

### Pré-requisitos

```bash
# Configurar variáveis
export BASE_URL="http://localhost:8090"
export TENANT_ID="1"
```

### 1. Health Check

```bash
curl -s "${BASE_URL}/api/health" | jq
```

### 2. CRUD de Usuário (Leitor)

#### 2.1 Criar Usuário
```bash
curl -s -X POST "${BASE_URL}/api/leitores" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "nome": "João Silva",
    "email": "joao.silva@email.com",
    "documento": "12345678901",
    "dataNascimento": "1990-05-15"
  }' | jq

# Salvar ID
export LEITOR_ID="<id-retornado>"
```

#### 2.2 Buscar Usuário
```bash
curl -s "${BASE_URL}/api/leitores/${LEITOR_ID}" | jq
```

#### 2.3 Atualizar Usuário
```bash
curl -s -X PUT "${BASE_URL}/api/leitores/${LEITOR_ID}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva Atualizado",
    "email": "joao.novo@email.com",
    "documento": "12345678901"
  }' | jq
```

#### 2.4 Deletar Usuário (Soft Delete)
```bash
curl -s -X DELETE "${BASE_URL}/api/leitores/${LEITOR_ID}" | jq
# Resposta: statusLeitor = "LEITOR_EXCLUIDO"
```

#### 2.5 Listar Usuários Ativos
```bash
curl -s "${BASE_URL}/api/leitores" -H "X-Tenant-Id: ${TENANT_ID}" | jq
```

### 3. CRUD de Livros (Títulos)

#### 3.1 Criar Livro
```bash
curl -s -X POST "${BASE_URL}/api/titulos" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "titulo": "Dom Casmurro",
    "autor": "Machado de Assis",
    "isbn": "978-85-359-0277-1"
  }' | jq

# Salvar ID
export TITULO_ID="<id-retornado>"
```

#### 3.2 Listar Todos os Livros
```bash
curl -s "${BASE_URL}/api/titulos" -H "X-Tenant-Id: ${TENANT_ID}" | jq
```

#### 3.3 Buscar Livro por ID
```bash
curl -s "${BASE_URL}/api/titulos/${TITULO_ID}" | jq
```

### 4. Empréstimos

#### 4.1 Realizar Empréstimo
```bash
curl -s -X POST "${BASE_URL}/api/emprestimos" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "leitorId": "'${LEITOR_ID}'",
    "exemplarId": "'${TITULO_ID}'"
  }' | jq

# Salvar ID
export EMPRESTIMO_ID="<id-retornado>"
```

#### 4.2 Devolver Livro
```bash
curl -s -X POST "${BASE_URL}/api/emprestimos/${EMPRESTIMO_ID}/devolucao" | jq
```

#### 4.3 Listar Empréstimos do Usuário
```bash
curl -s "${BASE_URL}/api/historico/leitor/${LEITOR_ID}" | jq
```

### 📋 Script Completo MVP (Copiar e Colar)

```bash
#!/bin/bash
# MVP Demo Script - Library API

BASE_URL="http://localhost:8090"
TENANT_ID="1"

echo "=== 1. Health Check ==="
curl -s "${BASE_URL}/api/health" | jq

echo -e "\n=== 2. Criar Usuário ==="
LEITOR_RESP=$(curl -s -X POST "${BASE_URL}/api/leitores" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{"nome": "Demo User", "email": "demo@email.com", "documento": "99988877766"}')
echo $LEITOR_RESP | jq
LEITOR_ID=$(echo $LEITOR_RESP | jq -r '.id')

echo -e "\n=== 3. Criar Livro ==="
TITULO_RESP=$(curl -s -X POST "${BASE_URL}/api/titulos" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{"titulo": "O Alquimista", "autor": "Paulo Coelho", "isbn": "978-85-325-2942-9"}')
echo $TITULO_RESP | jq
TITULO_ID=$(echo $TITULO_RESP | jq -r '.id')

echo -e "\n=== 4. Listar Livros ==="
curl -s "${BASE_URL}/api/titulos" -H "X-Tenant-Id: ${TENANT_ID}" | jq

echo -e "\n=== 5. Realizar Empréstimo ==="
EMPRESTIMO_RESP=$(curl -s -X POST "${BASE_URL}/api/emprestimos" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{"leitorId": "'$LEITOR_ID'", "exemplarId": "'$TITULO_ID'"}')
echo $EMPRESTIMO_RESP | jq
EMPRESTIMO_ID=$(echo $EMPRESTIMO_RESP | jq -r '.id')

echo -e "\n=== 6. Devolver Livro ==="
curl -s -X POST "${BASE_URL}/api/emprestimos/${EMPRESTIMO_ID}/devolucao" | jq

echo -e "\n=== 7. Histórico do Usuário ==="
curl -s "${BASE_URL}/api/historico/leitor/${LEITOR_ID}" | jq

echo -e "\n=== MVP Demo Completo! ==="
```

---

## 🚀 Como Iniciar a API

```bash
cd /Users/leandroleal/Projects/Library-LLAM/library-api
./mvnw spring-boot:run -Dspring-boot.run.profiles=hom
```

A API estará disponível em: `http://localhost:8090`

**Swagger UI:** http://localhost:8090/swagger-ui.html

---

## 📋 Variáveis de Ambiente

```bash
# Base URL da API local
export BASE_URL="http://localhost:8090"

# Tenant BMSP (Biblioteca Municipal de São Paulo) - criado em DEV
export TENANT_ID="1"

# IDs que serão preenchidos durante os testes
export LEITOR_ID=""
export TITULO_ID=""
export EXEMPLAR_ID=""
export EMPRESTIMO_ID=""
```

---

## 1️⃣ Health Check

```bash
curl -X GET "${BASE_URL}/api/health" | jq
```

---

## 2️⃣ CRUD de Leitores (Usuários)

### 2.1 Criar Leitor

```bash
curl -X POST "${BASE_URL}/api/leitores" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "nome": "João Silva",
    "email": "joao.silva@email.com",
    "telefone": "11999999999",
    "documento": "12345678900",
    "cpf": "123.456.789-00",
    "dataNascimento": "1990-01-15"
  }' | jq
```

**Salvar o ID retornado:**
```bash
export LEITOR_ID="<id-retornado>"
```

### 2.2 Buscar Leitor por ID

```bash
curl -X GET "${BASE_URL}/api/leitores/${LEITOR_ID}" | jq
```

### 2.3 Atualizar Leitor

```bash
curl -X PUT "${BASE_URL}/api/leitores/${LEITOR_ID}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva Atualizado",
    "email": "joao.silva.novo@email.com",
    "telefone": "11988888888",
    "documento": "12345678900",
    "dataNascimento": "1990-01-15"
  }' | jq
```

### 2.4 Suspender Leitor

```bash
curl -X POST "${BASE_URL}/api/leitores/${LEITOR_ID}/suspender?motivo=Atraso%20em%20devolucao" | jq
```

### 2.5 Reativar Leitor

```bash
curl -X POST "${BASE_URL}/api/leitores/${LEITOR_ID}/reativar" | jq
```

### 2.6 Excluir Leitor (Exclusão Lógica)

> ⚠️ **Soft Delete:** O leitor NÃO é removido fisicamente do banco. O status muda para `LEITOR_EXCLUIDO`.

```bash
curl -X DELETE "${BASE_URL}/api/leitores/${LEITOR_ID}" | jq
```

**Resposta esperada:**
```json
{
  "id": "...",
  "nome": "João Silva Atualizado",
  "statusLeitor": "LEITOR_EXCLUIDO",
  ...
}
```

### 2.7 Listar Leitores Excluídos (Lixeira)

```bash
curl -X GET "${BASE_URL}/api/leitores/excluidos" \
  -H "X-Tenant-Id: ${TENANT_ID}" | jq
```

### 2.8 Listar Leitores Ativos

```bash
curl -X GET "${BASE_URL}/api/leitores" \
  -H "X-Tenant-Id: ${TENANT_ID}" | jq
```

### 2.9 Restaurar Leitor Excluído

> ✅ Use o endpoint `/reativar` para restaurar um leitor da lixeira.

```bash
curl -X POST "${BASE_URL}/api/leitores/${LEITOR_ID}/reativar" | jq
```

---

## 3️⃣ CRUD de Títulos (Livros)

### 3.0 Listar Todos os Títulos

```bash
curl -X GET "${BASE_URL}/api/titulos" \
  -H "X-Tenant-Id: ${TENANT_ID}" | jq
```

### 3.1 Criar Título

```bash
curl -X POST "${BASE_URL}/api/titulos" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "titulo": "O Senhor dos Anéis - A Sociedade do Anel",
    "subtitulo": "Volume 1",
    "isbn": "978-85-333-0227-3",
    "autor": "J.R.R. Tolkien",
    "editora": "Martins Fontes",
    "anoPublicacao": 2000,
    "categoria": "Fantasia",
    "formato": "FISICO"
  }' | jq
```

**Salvar o ID retornado:**
```bash
export TITULO_ID="<id-retornado>"
```

### 3.2 Buscar Título por ID

```bash
curl -X GET "${BASE_URL}/api/titulos/${TITULO_ID}" | jq
```

### 3.3 Atualizar Título

```bash
curl -X PUT "${BASE_URL}/api/titulos/${TITULO_ID}" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "O Senhor dos Anéis - A Sociedade do Anel",
    "subtitulo": "Volume 1 - Edição Revisada",
    "isbn": "978-85-333-0227-3",
    "autor": "J.R.R. Tolkien",
    "editora": "Martins Fontes",
    "anoPublicacao": 2022,
    "categoria": "Fantasia",
    "formato": "FISICO"
  }' | jq
```

### 3.4 Excluir Título

```bash
curl -X DELETE "${BASE_URL}/api/titulos/${TITULO_ID}" -v
```

---

## 4️⃣ CRUD de Exemplares

### 4.1 Criar Exemplar

```bash
curl -X POST "${BASE_URL}/api/exemplares" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "tituloId": "'${TITULO_ID}'",
    "codigoBarras": "BMSP-2026-00001",
    "localizacao": "Estante A, Prateleira 3",
    "observacoes": "Exemplar em bom estado"
  }' | jq
```

**Salvar o ID retornado:**
```bash
export EXEMPLAR_ID="<id-retornado>"
```

### 4.2 Buscar Exemplar por ID

```bash
# Formato: /api/exemplares/{classId}/{classSchemeId}
curl -X GET "${BASE_URL}/api/exemplares/1/1" | jq
```

---

## 5️⃣ Fluxo de Empréstimo

### 5.1 Realizar Empréstimo

```bash
curl -X POST "${BASE_URL}/api/emprestimos" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "leitorId": "'${LEITOR_ID}'",
    "exemplarId": "'${EXEMPLAR_ID}'"
  }' | jq
```

**Salvar o ID retornado:**
```bash
export EMPRESTIMO_ID="<id-retornado>"
```

### 5.2 Buscar Empréstimo por ID

```bash
curl -X GET "${BASE_URL}/api/emprestimos/${EMPRESTIMO_ID}" | jq
```

### 5.3 Renovar Empréstimo

```bash
curl -X POST "${BASE_URL}/api/emprestimos/${EMPRESTIMO_ID}/renovacao" \
  -H "X-Tenant-Id: ${TENANT_ID}" | jq
```

### 5.4 Realizar Devolução

```bash
curl -X POST "${BASE_URL}/api/emprestimos/${EMPRESTIMO_ID}/devolucao" | jq
```

### 5.5 Empréstimo Múltiplo

```bash
curl -X POST "${BASE_URL}/api/emprestimos/multiplo" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "leitorId": "'${LEITOR_ID}'",
    "exemplarIds": ["EXEMP-001", "EXEMP-002", "EXEMP-003"]
  }' | jq
```

### 5.6 Devolução Múltipla

```bash
curl -X POST "${BASE_URL}/api/emprestimos/devolucao/multipla" \
  -H "Content-Type: application/json" \
  -d '["emp-id-1", "emp-id-2", "emp-id-3"]' | jq
```

---

## 6️⃣ Reservas

### 6.1 Criar Reserva

```bash
curl -X POST "${BASE_URL}/api/reservas" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "leitorId": "'${LEITOR_ID}'",
    "tituloId": "'${TITULO_ID}'"
  }' | jq
```

**Salvar o ID retornado:**
```bash
export RESERVA_ID="<id-retornado>"
```

### 6.2 Buscar Reserva por ID

```bash
curl -X GET "${BASE_URL}/api/reservas/${RESERVA_ID}" | jq
```

### 6.3 Cancelar Reserva

```bash
curl -X POST "${BASE_URL}/api/reservas/${RESERVA_ID}/cancelar" | jq
```

---

## 7️⃣ Multas

### 7.1 Buscar Multa por ID

```bash
curl -X GET "${BASE_URL}/api/multas/${MULTA_ID}" | jq
```

### 7.2 Pagar Multa

```bash
curl -X POST "${BASE_URL}/api/multas/${MULTA_ID}/pagar" | jq
```

### 7.3 Isentar Multa

```bash
curl -X POST "${BASE_URL}/api/multas/${MULTA_ID}/isentar?motivo=Primeira%20infração" | jq
```

---

## 8️⃣ Categorias

### 8.1 Criar Categoria

```bash
curl -X POST "${BASE_URL}/api/categorias" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "nome": "Ficção Científica",
    "descricao": "Livros de ficção científica"
  }' | jq
```

### 8.2 Buscar Categoria por ID

```bash
# Formato: /api/categorias/{classId}/{classSchemeId}
curl -X GET "${BASE_URL}/api/categorias/1/1" | jq
```

---

## 9️⃣ Autores

### 9.1 Criar Autor

```bash
curl -X POST "${BASE_URL}/api/autores" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: ${TENANT_ID}" \
  -d '{
    "nome": "Isaac Asimov",
    "nacionalidade": "Russo-Americano",
    "biografia": "Escritor e bioquímico americano"
  }' | jq
```

### 9.2 Buscar Autor por ID

```bash
curl -X GET "${BASE_URL}/api/autores/${AUTOR_ID}" | jq
```

---

## 🔟 Histórico do Leitor

```bash
curl -X GET "${BASE_URL}/api/historico/leitor/${LEITOR_ID}" | jq
```

---

## 📊 Fluxo Completo de Demonstração

Execute na ordem para demonstrar o MVP:

```bash
# 1. Health Check
curl -s "${BASE_URL}/api/health" | jq

# 2. Criar Leitor
LEITOR_RESP=$(curl -s -X POST "${BASE_URL}/api/leitores" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: 1" \
  -d '{"nome": "Maria Santos", "email": "maria@email.com", "documento": "98765432100"}')
echo $LEITOR_RESP | jq
LEITOR_ID=$(echo $LEITOR_RESP | jq -r '.id')

# 3. Criar Título
TITULO_RESP=$(curl -s -X POST "${BASE_URL}/api/titulos" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: 1" \
  -d '{"titulo": "Dom Casmurro", "isbn": "978-85-359-0277-1", "autor": "Machado de Assis"}')
echo $TITULO_RESP | jq
TITULO_ID=$(echo $TITULO_RESP | jq -r '.id')

# 4. Criar Exemplar
EXEMPLAR_RESP=$(curl -s -X POST "${BASE_URL}/api/exemplares" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: 1" \
  -d '{"tituloId": "'$TITULO_ID'", "codigoBarras": "BMSP-DOM-001", "localizacao": "Estante B"}')
echo $EXEMPLAR_RESP | jq
EXEMPLAR_ID=$(echo $EXEMPLAR_RESP | jq -r '.id // .codigoBarras')

# 5. Realizar Empréstimo
EMPRESTIMO_RESP=$(curl -s -X POST "${BASE_URL}/api/emprestimos" \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: 1" \
  -d '{"leitorId": "'$LEITOR_ID'", "exemplarId": "'$EXEMPLAR_ID'"}')
echo $EMPRESTIMO_RESP | jq
EMPRESTIMO_ID=$(echo $EMPRESTIMO_RESP | jq -r '.id')

# 6. Renovar Empréstimo
curl -s -X POST "${BASE_URL}/api/emprestimos/${EMPRESTIMO_ID}/renovacao" \
  -H "X-Tenant-Id: 1" | jq

# 7. Realizar Devolução
curl -s -X POST "${BASE_URL}/api/emprestimos/${EMPRESTIMO_ID}/devolucao" | jq

# 8. Verificar Histórico
curl -s "${BASE_URL}/api/historico/leitor/${LEITOR_ID}" | jq
```

---

## ⚠️ Troubleshooting

### API não conecta aos backends de homologação

Verifique se os endpoints estão acessíveis:
```bash
curl -k https://api-class-backend-hom.llam.tec.br/actuator/health
curl -k https://api-person-backend-hom.llam.tec.br/actuator/health
curl -k https://api-dublin-core-backend-hom.llam.tec.br/actuator/health
curl -k https://api-transaction-backend-hom.llam.tec.br/actuator/health
```

### Erro de certificado SSL

Se houver erro de SSL, adicione ao `application-hom.properties`:
```properties
# Desabilitar verificação SSL (apenas para testes)
server.ssl.enabled=false
```

### Verificar logs da API

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=hom 2>&1 | tee library-api.log
```

---

## 📚 Referências

- **Swagger UI:** http://localhost:8090/swagger-ui.html
- **API Docs:** http://localhost:8090/v3/api-docs
- **Postman Collection:** `Library_LLAM_MVP.postman_collection.json`
