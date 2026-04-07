# Library-LLAM API - MVP Ecos

Este projeto orquestra transações com os microsserviços do **Sistema Ecos (CERIF)** para gestão do negócio de Bibliotecas.

## Funcionalidades
1. **Modelagem CERIF (Person, DublinCore, Transaction):** Integração com `person-backend`, `dublin-core-backend`, e `transaction-backend`.
2. **Entity-Value-Attribute (EVA):** Metadados e catálogo em DDL genérico.
3. **Criptografia & Banco Embarcado (Trânsito Seguro):** Para a demonstração do MVP, implementamos criptografia `BCrypt` usando banco embarcado (`H2 local`).

## Executando o Perfil Local (Demonstração Offline H2)

Para executar a API testando com o banco contruído em memória e com endpoints de Criptografia Ativados:

```bash
mvn clean package -DskipTests
java -jar target/library-api-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

### Acessando o Banco Administrativo H2
Acesse `http://localhost:8090/h2-console`
- JDBC URL: `jdbc:h2:mem:librarydb`
- Username: `sa`
- Password: `password`

*Observe a tabela `USUARIOLOCAL` criada e as senhas salvas com hash BCrypt provando a proteção de credenciais.*

## Collection do Postman

Disponibilizamos o arquivo Postman na raiz do projeto contendo as rotas de Autenticação Segura: `Library_LLAM_MVP.postman_collection.json`.

---
*Gerado durante as correções do Feedback MVP.*
