# LLAM Biblioteca — Histórias de Usuário (HUs)

## Visão Geral

Este diretório contém as Histórias de Usuário do projeto **LLAM Biblioteca**, um sistema SaaS de gestão de acervo bibliográfico desenvolvido pela LLAM Tecnologia do Brasil Ltda.

O sistema opera no modelo **White Label multi-tenant** e cobre o ciclo completo de vida do livro: catalogação, localização física, empréstimo de exemplares físicos e venda de conteúdo digital.

---

## Estrutura de Diretórios

```
hu-its/
├── HUs/           # Histórias de Usuário (requisitos de negócio)
├── ITSs/          # Instruções Técnicas de Serviço (implementação)
└── README.md      # Este arquivo
```

---

## Índice de HUs por Fase

### Fase 1 — MVP Backend

| HU | Título | Prioridade | Estimativa |
|----|--------|------------|------------|
| [HU-01](HUs/hu-01-modelagem-dados-biblioteca.md) | Modelagem de Dados LLAM Biblioteca (CERIF) | Alta | 3 dias |
| [HU-02](HUs/hu-02-configuracao-multi-tenant.md) | Configuração Multi-Tenant White Label | Alta | 4 dias |
| [HU-03](HUs/hu-03-cadastro-titulos.md) | Cadastro de Títulos Bibliográficos | Alta | 3 dias |
| [HU-04](HUs/hu-04-gestao-exemplares.md) | Gestão de Exemplares Físicos | Alta | 3 dias |
| [HU-05](HUs/hu-05-cadastro-leitores.md) | Cadastro de Leitores | Alta | 4 dias |
| [HU-06](HUs/hu-06-modelo-transacional.md) | Modelo Transacional de Ativos Bibliográficos | Crítica | 5 dias |
| [HU-07](HUs/hu-07-emprestimo-livros.md) | Empréstimo de Livros Físicos | Alta | 4 dias |
| [HU-08](HUs/hu-08-devolucao-livros.md) | Devolução de Livros | Alta | 3 dias |
| [HU-09](HUs/hu-09-renovacao-reserva.md) | Renovação e Reserva de Livros | Média | 3 dias |
| [HU-12](HUs/hu-12-gestao-categorias.md) | Gestão de Categorias do Acervo | Média | 2 dias |
| [HU-13](HUs/hu-13-gestao-multas.md) | Gestão de Multas e Pendências Financeiras | Alta | 3 dias |
| [HU-14](HUs/hu-14-autenticacao-keycloak.md) | Autenticação Segura via Keycloak | Alta | 3 dias |
| [HU-17](HUs/hu-17-setup-projeto-backend.md) | Setup do Projeto Backend | Crítica | 2 dias |

**Total Fase 1:** ~42 dias

---

### Fase 2 — Funcionalidades Avançadas + MVP Frontend

| HU | Título | Prioridade | Estimativa |
|----|--------|------------|------------|
| [HU-10](HUs/hu-10-venda-conteudo-digital.md) | Venda de Conteúdo Digital (PDF) | Média | 5 dias |
| [HU-11](HUs/hu-11-planos-assinatura.md) | Planos de Assinatura do Leitor | Média | 4 dias |
| [HU-15](HUs/hu-15-biblioteca-pessoal-leitor.md) | Biblioteca Pessoal do Leitor | Média | 3 dias |
| [HU-16](HUs/hu-16-relatorios-auditoria.md) | Relatórios e Auditoria | Média | 5 dias |
| [HU-18](HUs/hu-18-setup-projeto-frontend.md) | Setup do Projeto Frontend | Alta | 2 dias |
| [HU-19](HUs/hu-19-tela-catalogo.md) | Tela de Catálogo de Títulos | Alta | 4 dias |
| [HU-20](HUs/hu-20-tela-backoffice-acervo.md) | Tela Backoffice de Gestão do Acervo | Alta | 5 dias |
| [HU-21](HUs/hu-21-tela-backoffice-emprestimos.md) | Tela Backoffice de Empréstimos e Devoluções | Alta | 4 dias |
| [HU-22](HUs/hu-22-tela-backoffice-leitores.md) | Tela Backoffice de Gestão de Leitores | Alta | 4 dias |
| [HU-23](HUs/hu-23-tela-configuracoes-tenant.md) | Tela de Configurações do Tenant | Média | 4 dias |
| [HU-24](HUs/hu-24-tela-dashboard-biblioteca.md) | Tela Dashboard da Biblioteca | Média | 4 dias |

**Total Fase 2:** ~44 dias

---

## Índice de HUs por Módulo do DVN

### Módulo 1 — Gestão do Acervo
- [HU-03](HUs/hu-03-cadastro-titulos.md) — Cadastro de Títulos
- [HU-04](HUs/hu-04-gestao-exemplares.md) — Gestão de Exemplares
- [HU-12](HUs/hu-12-gestao-categorias.md) — Gestão de Categorias
- [HU-20](HUs/hu-20-tela-backoffice-acervo.md) — Tela Backoffice Acervo

### Módulo 2 — Empréstimo de Livros Físicos
- [HU-06](HUs/hu-06-modelo-transacional.md) — Modelo Transacional (Core)
- [HU-07](HUs/hu-07-emprestimo-livros.md) — Empréstimo
- [HU-08](HUs/hu-08-devolucao-livros.md) — Devolução
- [HU-09](HUs/hu-09-renovacao-reserva.md) — Renovação e Reserva
- [HU-13](HUs/hu-13-gestao-multas.md) — Gestão de Multas
- [HU-21](HUs/hu-21-tela-backoffice-emprestimos.md) — Tela Backoffice Empréstimos

### Módulo 3 — Venda de Conteúdo Digital
- [HU-10](HUs/hu-10-venda-conteudo-digital.md) — Venda Digital
- [HU-15](HUs/hu-15-biblioteca-pessoal-leitor.md) — Biblioteca Pessoal

### Módulo 4 — Gestão de Usuários
- [HU-05](HUs/hu-05-cadastro-leitores.md) — Cadastro de Leitores
- [HU-11](HUs/hu-11-planos-assinatura.md) — Planos de Assinatura
- [HU-14](HUs/hu-14-autenticacao-keycloak.md) — Autenticação
- [HU-22](HUs/hu-22-tela-backoffice-leitores.md) — Tela Backoffice Leitores

### Infraestrutura e Multi-Tenant
- [HU-01](HUs/hu-01-modelagem-dados-biblioteca.md) — Modelagem de Dados
- [HU-02](HUs/hu-02-configuracao-multi-tenant.md) — Multi-Tenant
- [HU-17](HUs/hu-17-setup-projeto-backend.md) — Setup Backend
- [HU-18](HUs/hu-18-setup-projeto-frontend.md) — Setup Frontend
- [HU-23](HUs/hu-23-tela-configuracoes-tenant.md) — Configurações Tenant

### Relatórios e Dashboard
- [HU-16](HUs/hu-16-relatorios-auditoria.md) — Relatórios e Auditoria
- [HU-24](HUs/hu-24-tela-dashboard-biblioteca.md) — Dashboard

---

## Regras de Negócio Centrais (do DVN)

| RN | Descrição | HUs Relacionadas |
|----|-----------|------------------|
| RN-01 | Todo empréstimo gera lançamentos transacionais pareados | HU-06, HU-07 |
| RN-02 | Toda devolução gera lançamentos transacionais inversos | HU-06, HU-08 |
| RN-03 | Empréstimo múltiplo é lote atômico | HU-06, HU-07 |
| RN-04 | Saldo calculado do histórico, não editável | HU-06 |
| RN-05 | Lançamento financeiro integrado à transação | HU-06, HU-07 |
| RN-06 | Venda digital é permanente, sem estorno | HU-10 |
| RN-07 | Senha nunca em texto puro | HU-05, HU-14 |
| RN-08 | Leitor em atraso não pode emprestar | HU-07 |
| RN-09 | Leitor com pendência financeira bloqueado | HU-07, HU-10 |
| RN-10 | Histórico imutável | HU-06, HU-16 |
| RN-11 | Isolamento total entre tenants | HU-02 |
| RN-12 | Configurações independentes por tenant | HU-02, HU-23 |

---

## Referências

- [DVN - Documento de Visão Negocial](../dvn.md)
- [ITSs - Instruções Técnicas de Serviço](ITSs/) — Detalhes técnicos de implementação
- Modelo de HUs: SCPA/docs/hu_its/HUs/

---

*Documento gerado em Março/2026 — LLAM Tecnologia do Brasil Ltda*
