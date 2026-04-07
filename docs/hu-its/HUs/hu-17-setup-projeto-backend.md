# HU-17 – Setup do Projeto Backend | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como desenvolvedor, quero configurar o projeto backend Spring Boot com todas as dependências e estrutura base, para iniciar o desenvolvimento das funcionalidades do LLAM Biblioteca.
- Plataforma: Spring Boot 3.x + Java 17 + MySQL + H2 (testes)
- Estrutura baseada nos backends CERIF existentes (class-backend, person-backend)
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Java 17:** Projeto deve usar Java 17 conforme requisito do desafio técnico.
2. **RN-2 – Spring Boot 3.x:** Versão mais recente estável do Spring Boot.
3. **RN-3 – Banco embarcado:** H2 para desenvolvimento e testes (requisito do desafio).
4. **RN-4 – MySQL produção:** MySQL para ambientes de homologação e produção.
5. **RN-5 – Flyway:** Migrations versionadas via Flyway.
6. **RN-6 – Estrutura CERIF:** Reutilizar entidades e padrões dos backends CERIF existentes.
7. **RN-7 – Docker:** Dockerfile para containerização.
8. **RN-8 – CI/CD:** Pipeline GitLab CI configurado.

---

## Critérios de Aceite (CA)
1. **CA-1 – Projeto criado:** Projeto Maven com estrutura de pacotes definida.
2. **CA-2 – Dependências:** pom.xml com todas as dependências necessárias.
3. **CA-3 – Profiles:** Profiles para dev (H2), hom (MySQL), prod (MySQL).
4. **CA-4 – Health check:** GET /actuator/health retorna UP.
5. **CA-5 – Swagger:** Documentação OpenAPI disponível em /swagger-ui.html.
6. **CA-6 – Dockerfile:** Build da imagem Docker funcional.
7. **CA-7 – CI/CD:** Pipeline executa build e testes automaticamente.
8. **CA-8 – README:** Documentação de setup e execução local.

---

## Cenários de Teste (CT)
1. **CT-1 – Build Maven:** mvn clean package → BUILD SUCCESS.
2. **CT-2 – Testes unitários:** mvn test → todos os testes passam.
3. **CT-3 – Iniciar H2:** Iniciar com profile dev → H2 console acessível.
4. **CT-4 – Health check:** GET /actuator/health → {"status": "UP"}.
5. **CT-5 – Swagger:** Acessar /swagger-ui.html → documentação exibida.
6. **CT-6 – Docker build:** docker build → imagem criada com sucesso.
7. **CT-7 – Docker run:** docker run → aplicação inicia corretamente.
8. **CT-8 – Pipeline:** Push no GitLab → pipeline executa com sucesso.

---

## Estrutura de Pacotes

```
br.tec.llam.biblioteca
├── config/           # Configurações Spring
├── controller/       # REST Controllers
├── service/          # Serviços de negócio
├── repository/       # Repositórios JPA
├── entity/           # Entidades JPA (reutiliza CERIF)
├── dto/              # DTOs de request/response
├── exception/        # Exceções customizadas
├── security/         # Configurações de segurança
└── util/             # Utilitários
```

---

## Observações
- Pré-requisito para todas as demais HUs de backend
- Estrutura baseada nos backends CERIF existentes
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 2 dias
- Prioridade: Crítica
