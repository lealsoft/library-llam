# Biblio API - Sistema de Biblioteca

API REST para gerenciamento de biblioteca desenvolvida como desafio tecnico.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data JPA**
- **Spring Security** (BCrypt para hash de senhas)
- **JWT** (JSON Web Token para autenticacao)
- **H2 Database** (banco embarcado)
- **Lombok**
- **Maven**
- **SpringDoc OpenAPI** (Swagger UI)

## Como Rodar o Projeto

### Pre-requisitos
- Java 17 ou superior
- Maven 3.6+

### Executar a aplicacao

```bash
# Clonar o repositorio e entrar na pasta
cd biblio-api

# Compilar e executar (profile dev - padrao)
mvn spring-boot:run

# Ou executar com profile especifico
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

A aplicacao estara disponivel em: `http://localhost:8080`

### Pagina de Login (Interface Web)
- URL: `http://localhost:8080/login.html`
- Interface HTML pura (sem framework) para autenticacao
- **Criptografia hibrida RSA-2048 + AES-256-GCM** no cliente
- Chave publica RSA buscada dinamicamente do servidor
- Chave privada nunca sai do servidor
- Suporta Login e Registro de usuarios

### Swagger UI (Documentacao da API)
- URL: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### Console H2 (Banco de Dados) - Apenas profile DEV
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:bibliodb`
- User: `sa`
- Password: (vazio)

## Profiles de Ambiente

A aplicacao possui dois profiles configurados:

### Profile DEV (Desenvolvimento)
Ativado por padrao. Configuracoes em `application-dev.properties`.

| Recurso | Configuracao |
|---------|--------------|
| Banco de dados | H2 em memoria |
| Console H2 | Habilitado |
| Endpoint /api/crypto/encrypt | Disponivel |
| Chaves JWT/AES | Fixas no arquivo |
| Logs | DEBUG |
| DDL | create-drop |

```bash
# Executar em desenvolvimento
mvn spring-boot:run
# ou
java -jar biblio-api.jar --spring.profiles.active=dev
```

### Profile PROD (Producao)
Configuracoes em `application-prod.properties`.

| Recurso | Configuracao |
|---------|--------------|
| Banco de dados | PostgreSQL (configurar) |
| Console H2 | Desabilitado |
| Endpoint /api/crypto/encrypt | NAO disponivel |
| Chaves JWT/AES | Via variaveis de ambiente |
| Logs | WARN/INFO |
| DDL | validate |

```bash
# Executar em producao
java -jar biblio-api.jar --spring.profiles.active=prod

# Variaveis de ambiente necessarias:
export JWT_SECRET="sua-chave-jwt-segura-256-bits"
export CRYPTO_AES_KEY="sua-chave-aes-256-bits"
export DATABASE_URL="jdbc:postgresql://host:5432/bibliodb"
export DATABASE_USERNAME="usuario"
export DATABASE_PASSWORD="senha"
```

### Diferencas entre Profiles

| Aspecto | DEV | PROD |
|---------|-----|------|
| Banco | H2 (memoria) | PostgreSQL |
| Secrets | Hardcoded | Variaveis de ambiente |
| /api/crypto/encrypt | Disponivel | Indisponivel |
| Console H2 | Habilitado | Desabilitado |
| DataInitializer | Ativo | Inativo |
| Logs | Verbose | Minimo |
| HTTPS | Opcional | Obrigatorio |

### Checklist para Producao

- [ ] Configurar banco de dados PostgreSQL
- [ ] Definir variaveis de ambiente para secrets
- [ ] Configurar HTTPS com certificado valido
- [x] Implementar rate limiting (Bucket4j)
- [ ] Configurar monitoramento (Actuator + Prometheus)
- [x] Implementar blacklist de tokens JWT (Caffeine Cache)
- [ ] Configurar backup do banco de dados
- [ ] Revisar politicas de CORS

## Decisoes de Seguranca

### 1. Criptografia em Transito (AES-256-GCM)
- Todas as senhas e logins sao transmitidos **criptografados** do cliente para a API
- Algoritmo: AES/GCM/NoPadding com IV de 12 bytes
- A API fornece um endpoint auxiliar para criptografar dados: `POST /api/crypto/encrypt`

### 2. Hash de Senhas (BCrypt)
- Senhas sao armazenadas usando **BCrypt com fator de custo 12**
- Nunca armazenamos senhas em texto puro
- Cada senha tem um salt unico gerado automaticamente

### 3. Validacao de Forca de Senha
Senhas devem conter:
- Minimo 8 caracteres
- Pelo menos 1 letra maiuscula
- Pelo menos 1 letra minuscula
- Pelo menos 1 numero
- Pelo menos 1 caractere especial

### 4. Autenticacao JWT com Refresh Token
- Login e senha sao recebidos criptografados via AES
- Apos autenticacao, a API retorna:
  - **Access Token (JWT)**: curta duracao (15 minutos)
  - **Refresh Token**: longa duracao (7 dias), armazenado no banco
- O access token deve ser enviado no header `Authorization: Bearer <token>`
- Quando o access token expira, use o refresh token para obter um novo
- Refresh token e rotacionado a cada uso (aumenta seguranca)
- Logout revoga o refresh token e invalida o access token imediatamente
- Mensagens de erro genericas ("Credenciais invalidas") para evitar enumeracao de usuarios

### 5. Rate Limiting (Protecao contra Brute Force)
- Endpoint de login limitado a **5 tentativas por minuto** por IP
- Utiliza algoritmo Token Bucket (Bucket4j)
- Retorna HTTP 429 (Too Many Requests) quando limite excedido
- Protege contra ataques de forca bruta em senhas

### 6. Blacklist de Tokens JWT (Logout Imediato)
- Tokens JWT podem ser invalidados antes da expiracao natural
- Utiliza cache Caffeine em memoria (TTL igual ao JWT)
- Logout adiciona o access token a blacklist
- Logout de todas as sessoes revoga todos os refresh tokens do usuario
- Em producao com multiplas instancias, recomenda-se usar Redis

### 7. Protecao contra SQL Injection
- Todas as queries usam **Spring Data JPA** com PreparedStatements
- Parametros sao sempre vinculados (`:parametro`), nunca concatenados
- Nenhuma query SQL dinamica com concatenacao de strings
- Validacao de entrada em todos os DTOs

### 8. Endpoints Publicos vs Protegidos
| Tipo | Endpoints |
|------|-----------|
| Publico | `/api/auth/**`, `/api/crypto/**`, `GET /api/livros/**` |
| Protegido | `/api/usuarios/**`, `/api/emprestimos/**`, `POST/DELETE /api/livros/**` |
| Admin (DEV) | `/api/admin/**` - monitoramento de blacklist e rate limit |

## Endpoints Disponiveis

### Autenticacao (JWT + Refresh Token)
| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| POST | `/api/auth/login` | Login e obtencao dos tokens (rate limited) |
| POST | `/api/auth/registrar` | Registrar novo usuario e obter tokens |
| POST | `/api/auth/refresh` | Renovar access token usando refresh token |
| POST | `/api/auth/logout` | Logout (revoga refresh + blacklist access) |
| POST | `/api/auth/logout-all` | Logout de todas as sessoes do usuario |

**Login - Request:**
```json
{
  "loginCriptografado": "VALOR_CRIPTOGRAFADO_DO_LOGIN",
  "senhaCriptografada": "VALOR_CRIPTOGRAFADO_DA_SENHA"
}
```

**Login - Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tipo": "Bearer",
  "expiraEm": 900,
  "usuario": {
    "id": 1,
    "nome": "Joao Silva",
    "login": "joao.silva",
    "dataCriacao": "2024-01-01T10:00:00"
  }
}
```

**Refresh - Request:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Logout - Request:**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Administracao (APENAS PROFILE DEV)

> **ATENCAO:** Estes endpoints so estao disponiveis quando `spring.profiles.active=dev`.

| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| GET | `/api/admin/blacklist/info` | Quantidade de tokens na blacklist |
| GET | `/api/admin/blacklist/check?token=xxx` | Verifica se token esta na blacklist |
| DELETE | `/api/admin/blacklist/clear` | Limpa toda a blacklist |
| GET | `/api/admin/ratelimit/login/{ip}` | Tentativas restantes para o IP |
| GET | `/api/admin/ratelimit/login` | Lista todos os IPs com rate limit de login |
| GET | `/api/admin/ratelimit/general` | Lista todos os IPs com rate limit geral |
| DELETE | `/api/admin/ratelimit/clear` | Reseta todos os rate limits |

### Criptografia

| Metodo | Endpoint | Descricao | Disponibilidade |
|--------|----------|-----------|-----------------|
| GET | `/api/crypto/public-key` | Obtem chave publica RSA para criptografia hibrida | DEV e PROD |
| POST | `/api/crypto/encrypt` | Criptografa um texto (auxiliar para testes) | APENAS DEV |

> **Criptografia Hibrida RSA+AES:** O frontend deve buscar a chave publica RSA via `/api/crypto/public-key`, 
> gerar uma chave AES aleatoria, criptografar os dados com AES-GCM, e criptografar a chave AES com RSA.
> O formato enviado e: `Base64(RSA(chaveAES)).Base64(IV+CipherText)`

**Request Body:**
```json
{
  "texto": "valor_a_criptografar"
}
```

**Response:**
```json
{
  "criptografado": "BASE64_DO_VALOR_CRIPTOGRAFADO"
}
```

### Usuarios
| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| GET | `/api/usuarios` | Listar todos os usuarios |
| GET | `/api/usuarios/{id}` | Buscar usuario por ID |
| POST | `/api/usuarios` | Criar usuario |
| PUT | `/api/usuarios/{id}` | Atualizar usuario |
| DELETE | `/api/usuarios/{id}` | Deletar usuario |

### Livros
| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| GET | `/api/livros?ordenacao=X` | Listar todos os livros |
| GET | `/api/livros/disponiveis?ordenacao=X` | Listar livros disponiveis |
| GET | `/api/livros/{id}` | Buscar livro por ID |
| POST | `/api/livros` | Cadastrar novo livro |
| DELETE | `/api/livros/{id}` | Deletar livro (se disponivel) |

**Opcoes de ordenacao:**
- `recentes` (default) - Mais recentes primeiro
- `antigos` - Mais antigos primeiro
- `titulo` - Ordem alfabetica por titulo
- `populares` - Mais emprestados primeiro

### Emprestimos
| Metodo | Endpoint | Descricao |
|--------|----------|-----------|
| GET | `/api/emprestimos` | Listar todos os emprestimos |
| GET | `/api/emprestimos/{id}` | Buscar emprestimo por ID |
| POST | `/api/emprestimos` | Realizar emprestimo |
| PUT | `/api/emprestimos/{id}/devolver` | Devolver livro |

#### Criar Usuario
**POST** `/api/usuarios`
```json
{
  "nome": "Nome do Usuario",
  "login": "login_unico",
  "senhaCriptografada": "VALOR_CRIPTOGRAFADO_DA_SENHA"
}
```

#### Atualizar Usuario
**PUT** `/api/usuarios/{id}`
```json
{
  "nome": "Novo Nome",
  "login": "novo_login",
  "senhaCriptografada": "NOVA_SENHA_CRIPTOGRAFADA"
}
```

#### Deletar Usuario
**DELETE** `/api/usuarios/{id}`

## Dados Pre-carregados

Ao iniciar a aplicacao, os seguintes dados sao criados automaticamente:

### Usuarios
| Login | Senha |
|-------|-------|
| joao.silva | Senha@123 |
| maria.santos | Senha@456 |
| pedro.oliveira | Senha@789 |

### Livros
- Clean Code (Robert C. Martin) - EMPRESTADO
- Design Patterns (Gang of Four) - DISPONIVEL
- Domain-Driven Design (Eric Evans) - EMPRESTADO
- Refactoring (Martin Fowler) - DISPONIVEL
- The Pragmatic Programmer (David Thomas, Andrew Hunt) - DISPONIVEL

## Como Importar e Usar a Collection

1. Importe o arquivo `Biblio-API.postman_collection.json` no Postman
2. A collection contem variaveis pre-configuradas
3. Execute as requisicoes na ordem sugerida:
   - Primeiro use `/api/crypto/encrypt` para obter valores criptografados
   - Use esses valores nos demais endpoints

## Testes com CURL

### Passo 1: Criptografar credenciais

```bash
# Criptografar login
curl -X POST http://localhost:8080/api/crypto/encrypt \
  -H "Content-Type: application/json" \
  -d '{"texto": "joao.silva"}'

# Criptografar senha
curl -X POST http://localhost:8080/api/crypto/encrypt \
  -H "Content-Type: application/json" \
  -d '{"texto": "Senha@123"}'
```

### Passo 2: Fazer login e obter JWT

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginCriptografado": "LOGIN_CRIPTOGRAFADO",
    "senhaCriptografada": "SENHA_CRIPTOGRAFADA"
  }'
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "expiraEm": 3600,
  "usuario": {...}
}
```

Guarde o `token` para usar nos proximos comandos.

### Passo 3: Acessar endpoint protegido (com JWT)

```bash
# Listar usuarios (requer autenticacao)
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer SEU_TOKEN_JWT"

# Realizar emprestimo (requer autenticacao)
curl -X POST http://localhost:8080/api/emprestimos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -d '{"usuarioId": 1, "livroId": 2}'
```

### Passo 4: Acessar endpoint publico (sem JWT)

```bash
# Listar livros (publico)
curl -X GET http://localhost:8080/api/livros

# Listar livros disponiveis (publico)
curl -X GET http://localhost:8080/api/livros/disponiveis
```

---

### Registrar novo usuario e obter JWT

```bash
# 1. Criptografar senha
curl -X POST http://localhost:8080/api/crypto/encrypt \
  -H "Content-Type: application/json" \
  -d '{"texto": "MinhaSenha@123"}'

# 2. Registrar (retorna JWT + Refresh Token automaticamente)
curl -X POST http://localhost:8080/api/auth/registrar \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Novo Usuario",
    "login": "novo.usuario",
    "senhaCriptografada": "SENHA_CRIPTOGRAFADA"
  }'
```

**Resposta esperada (201 Created):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tipo": "Bearer",
  "expiraEm": 900,
  "usuario": {
    "id": 4,
    "nome": "Novo Usuario",
    "login": "novo.usuario",
    "dataCriacao": "2026-04-13T06:59:00"
  }
}
```

---

### Renovar Access Token (Refresh)

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "SEU_REFRESH_TOKEN"
  }'
```

**Resposta esperada (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...(novo)",
  "refreshToken": "novo-refresh-token-uuid",
  "tipo": "Bearer",
  "expiraEm": 900,
  "usuario": {...}
}
```

---

### Logout (Revogar Tokens)

```bash
# Logout simples (revoga refresh token + blacklist access token)
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_ACCESS_TOKEN" \
  -d '{
    "refreshToken": "SEU_REFRESH_TOKEN"
  }'

# Logout de todas as sessoes
curl -X POST http://localhost:8080/api/auth/logout-all \
  -H "Authorization: Bearer SEU_ACCESS_TOKEN"
```

**Resposta esperada:** 204 No Content

---

### Atualizar usuario (requer JWT)

```bash
curl -X PUT http://localhost:8080/api/usuarios/4 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_ACCESS_TOKEN" \
  -d '{
    "nome": "Usuario Atualizado"
  }'
```

**Resposta esperada (200 OK):**
```json
{
  "id": 4,
  "nome": "Usuario Atualizado",
  "login": "novo.usuario",
  "dataCriacao": "2026-04-13T06:59:00"
}
```

### Deletar usuario (requer JWT)

```bash
curl -X DELETE http://localhost:8080/api/usuarios/4 \
  -H "Authorization: Bearer SEU_ACCESS_TOKEN"
```

**Resposta esperada:** 204 No Content

---

### Testar Usuario Pre-carregado (joao.silva)

```bash
# 1. Criptografar login
curl -X POST http://localhost:8080/api/crypto/encrypt \
  -H "Content-Type: application/json" \
  -d '{"texto": "joao.silva"}'

# 2. Criptografar senha
curl -X POST http://localhost:8080/api/crypto/encrypt \
  -H "Content-Type: application/json" \
  -d '{"texto": "Senha@123"}'

# 3. Login (substitua pelos valores obtidos acima)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginCriptografado": "LOGIN_JOAO_CRIPTOGRAFADO",
    "senhaCriptografada": "SENHA_JOAO_CRIPTOGRAFADA"
  }'
```

**Resposta esperada:** Access Token + Refresh Token + dados do usuario.

---

### Endpoints Admin (APENAS PROFILE DEV)

```bash
# Ver quantidade de tokens na blacklist
curl -X GET http://localhost:8080/api/admin/blacklist/info

# Verificar se um token esta na blacklist
curl -X GET "http://localhost:8080/api/admin/blacklist/check?token=SEU_TOKEN"

# Ver tentativas de login restantes para um IP
curl -X GET http://localhost:8080/api/admin/ratelimit/login/127.0.0.1

# Listar TODOS os IPs com rate limit de login
curl -X GET http://localhost:8080/api/admin/ratelimit/login

# Listar TODOS os IPs com rate limit geral
curl -X GET http://localhost:8080/api/admin/ratelimit/general
```

**Resposta esperada (lista de IPs):**
```json
{
  "totalIps": 2,
  "limitePorMinuto": 5,
  "ips": {
    "127.0.0.1": 3,
    "192.168.1.100": 5
  }
}
```

---

### Testes de Livros com CURL

```bash
# Listar todos os livros (mais recentes primeiro - default)
curl -X GET http://localhost:8080/api/livros

# Listar livros ordenados por popularidade (mais emprestados)
curl -X GET "http://localhost:8080/api/livros?ordenacao=populares"

# Listar livros ordenados por titulo
curl -X GET "http://localhost:8080/api/livros?ordenacao=titulo"

# Listar livros mais antigos primeiro
curl -X GET "http://localhost:8080/api/livros?ordenacao=antigos"

# Listar apenas livros disponiveis
curl -X GET http://localhost:8080/api/livros/disponiveis

# Listar livros disponiveis mais populares
curl -X GET "http://localhost:8080/api/livros/disponiveis?ordenacao=populares"

# Buscar livro por ID
curl -X GET http://localhost:8080/api/livros/1

# Cadastrar novo livro
curl -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Effective Java",
    "autor": "Joshua Bloch",
    "isbn": "978-0134685991"
  }'

# Deletar livro (apenas se estiver disponivel)
curl -X DELETE http://localhost:8080/api/livros/6
```

---

### Testes de Emprestimos com CURL

```bash
# Listar todos os emprestimos
curl -X GET http://localhost:8080/api/emprestimos

# Buscar emprestimo por ID
curl -X GET http://localhost:8080/api/emprestimos/1

# Realizar emprestimo (usuario 3 pega livro 2)
curl -X POST http://localhost:8080/api/emprestimos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 3,
    "livroId": 2
  }'

# Devolver livro (finalizar emprestimo)
curl -X PUT http://localhost:8080/api/emprestimos/4/devolver
```

**Fluxo completo de emprestimo:**
```bash
# 1. Ver livros disponiveis
curl -s http://localhost:8080/api/livros/disponiveis | jq .

# 2. Realizar emprestimo do livro 4 para usuario 1
curl -X POST http://localhost:8080/api/emprestimos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId": 1, "livroId": 4}'

# 3. Verificar que livro agora esta EMPRESTADO
curl -s http://localhost:8080/api/livros/4 | jq .

# 4. Devolver o livro (substitua pelo ID do emprestimo retornado)
curl -X PUT http://localhost:8080/api/emprestimos/4/devolver

# 5. Verificar que livro voltou a estar DISPONIVEL
curl -s http://localhost:8080/api/livros/4 | jq .
```

---

### Script Completo de Teste (Bash)

```bash
#!/bin/bash
BASE_URL="http://localhost:8080"

echo "=== 1. Criptografando senha ==="
SENHA_CRIPTO=$(curl -s -X POST $BASE_URL/api/crypto/encrypt \
  -H "Content-Type: application/json" \
  -d '{"texto": "TesteSenha@123"}' | jq -r '.criptografado')
echo "Senha criptografada: $SENHA_CRIPTO"

echo ""
echo "=== 2. Criando usuario ==="
USUARIO=$(curl -s -X POST $BASE_URL/api/usuarios \
  -H "Content-Type: application/json" \
  -d "{\"nome\": \"Usuario Teste\", \"login\": \"teste.curl\", \"senhaCriptografada\": \"$SENHA_CRIPTO\"}")
echo $USUARIO | jq .
USUARIO_ID=$(echo $USUARIO | jq -r '.id')

echo ""
echo "=== 3. Criptografando login ==="
LOGIN_CRIPTO=$(curl -s -X POST $BASE_URL/api/crypto/encrypt \
  -H "Content-Type: application/json" \
  -d '{"texto": "teste.curl"}' | jq -r '.criptografado')
echo "Login criptografado: $LOGIN_CRIPTO"

echo ""
echo "=== 4. Tentando autenticar com senha ERRADA ==="
SENHA_ERRADA=$(curl -s -X POST $BASE_URL/api/crypto/encrypt \
  -H "Content-Type: application/json" \
  -d '{"texto": "SenhaErrada@999"}' | jq -r '.criptografado')
curl -s -X POST $BASE_URL/api/usuarios/autenticar \
  -H "Content-Type: application/json" \
  -d "{\"loginCriptografado\": \"$LOGIN_CRIPTO\", \"senhaCriptografada\": \"$SENHA_ERRADA\"}" | jq .

echo ""
echo "=== 5. Autenticando com credenciais CORRETAS ==="
curl -s -X POST $BASE_URL/api/usuarios/autenticar \
  -H "Content-Type: application/json" \
  -d "{\"loginCriptografado\": \"$LOGIN_CRIPTO\", \"senhaCriptografada\": \"$SENHA_CRIPTO\"}" | jq .

echo ""
echo "=== 6. Atualizando usuario ==="
curl -s -X PUT $BASE_URL/api/usuarios/$USUARIO_ID \
  -H "Content-Type: application/json" \
  -d '{"nome": "Usuario Atualizado via CURL"}' | jq .

echo ""
echo "=== 7. Deletando usuario ==="
curl -s -X DELETE $BASE_URL/api/usuarios/$USUARIO_ID -w "HTTP Status: %{http_code}\n"

echo ""
echo "=== Teste completo! ==="
```

**Nota:** O script acima requer `jq` instalado para parsing de JSON.

## Testes Unitarios

O projeto possui cobertura de testes unitarios para todas as camadas.

**Executar testes:**
```bash
mvn test
```

### Resumo dos Testes

| Classe de Teste | Quantidade | Descricao |
|-----------------|------------|-----------|
| LivroServiceTest | 14 | CRUD de livros, ordenacao, validacoes |
| LivroControllerTest | 15 | Endpoints REST de livros |
| EmprestimoServiceTest | 12 | Fluxo de emprestimo e devolucao |
| EmprestimoControllerTest | 12 | Endpoints REST de emprestimos |
| AuthControllerTest | 11 | Login, registro, refresh, logout |
| JwtServiceTest | 14 | Geracao e validacao de tokens JWT |
| JwtAuthenticationFilterTest | 9 | Filtro de autenticacao |
| RefreshTokenServiceTest | 12 | Criacao, rotacao e revogacao de refresh tokens |
| RateLimiterServiceTest | 7 | Rate limiting por IP |
| TokenBlacklistServiceTest | 9 | Blacklist de tokens JWT |
| RsaCryptoServiceTest | 4 | Criptografia hibrida RSA+AES |
| BiblioApiApplicationTests | 1 | Contexto da aplicacao |
| **Total** | **122** | |

### Categorias de Testes

**LivroServiceTest:**
- Listagem com ordenacao (recentes, antigos, titulo, populares)
- Busca por ID
- Criacao com validacao de ISBN unico
- Delecao (disponivel vs emprestado)

**EmprestimoServiceTest:**
- Realizar emprestimo (validacoes de usuario, livro, disponibilidade)
- Realizar devolucao (validacao de status)
- Conversao de DTOs

**AuthControllerTest:**
- Login com credenciais validas/invalidas
- Registro de usuario
- Refresh token
- Logout simples e logout de todas sessoes

**JwtServiceTest:**
- Geracao de token com claims
- Validacao de assinatura e expiracao
- Extracao de dados (login, userId)

**RsaCryptoServiceTest:**
- Geracao de par de chaves RSA-2048
- Criptografia e descriptografia hibrida RSA+AES
- Validacao de formato hibrido
- Tratamento de dados invalidos

## Estrutura do Projeto

```
src/main/java/com/capgemini/biblioapi/
|-- config/           # Configuracoes (Security, DataInitializer)
|-- controller/       # Controllers REST
|-- dto/              # Data Transfer Objects
|-- entity/           # Entidades JPA
|-- exception/        # Tratamento de excecoes
|-- repository/       # Repositorios JPA
|-- service/          # Logica de negocio
```
