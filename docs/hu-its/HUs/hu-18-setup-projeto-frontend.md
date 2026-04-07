# HU-18 – Setup do Projeto Frontend | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como desenvolvedor, quero configurar o projeto frontend React com todas as dependências e estrutura base, para iniciar o desenvolvimento das interfaces do LLAM Biblioteca.
- Plataforma: React + TypeScript + TailwindCSS + shadcn/ui
- Interface White Label com suporte a temas por tenant
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – React 18+:** Versão mais recente do React com TypeScript.
2. **RN-2 – TailwindCSS:** Estilização via TailwindCSS.
3. **RN-3 – shadcn/ui:** Componentes base do shadcn/ui.
4. **RN-4 – Temas dinâmicos:** Suporte a temas customizados por tenant (cores, logo).
5. **RN-5 – Responsivo:** Interface responsiva para desktop e mobile.
6. **RN-6 – Keycloak JS:** Integração com Keycloak para autenticação.
7. **RN-7 – Vite:** Build com Vite para desenvolvimento rápido.
8. **RN-8 – Docker:** Dockerfile com Nginx para produção.

---

## Critérios de Aceite (CA)
1. **CA-1 – Projeto criado:** Projeto Vite + React + TypeScript configurado.
2. **CA-2 – Dependências:** package.json com todas as dependências.
3. **CA-3 – TailwindCSS:** Estilos Tailwind funcionando.
4. **CA-4 – shadcn/ui:** Componentes base instalados e funcionando.
5. **CA-5 – Tema dinâmico:** Cores e logo carregados do tenant.
6. **CA-6 – Keycloak:** Integração com Keycloak funcional.
7. **CA-7 – Dockerfile:** Build da imagem Docker funcional.
8. **CA-8 – README:** Documentação de setup e execução local.

---

## Cenários de Teste (CT)
1. **CT-1 – Dev server:** npm run dev → aplicação inicia em localhost.
2. **CT-2 – Build:** npm run build → build de produção gerado.
3. **CT-3 – Componentes:** Renderizar Button, Card, Input → funcionam.
4. **CT-4 – Tema tenant A:** Acessar tenant A → cores do tenant A aplicadas.
5. **CT-5 – Tema tenant B:** Acessar tenant B → cores do tenant B aplicadas.
6. **CT-6 – Login Keycloak:** Clicar login → redirecionado para Keycloak.
7. **CT-7 – Docker build:** docker build → imagem criada.
8. **CT-8 – Docker run:** docker run → Nginx serve aplicação.
9. **CT-9 – Responsivo:** Acessar em mobile → layout adaptado.

---

## Estrutura de Diretórios

```
src/
├── components/       # Componentes reutilizáveis
│   ├── ui/           # Componentes shadcn/ui
│   └── layout/       # Layout (Header, Sidebar, Footer)
├── pages/            # Páginas da aplicação
├── hooks/            # Custom hooks
├── services/         # Chamadas de API
├── contexts/         # Contextos React (Auth, Theme, Tenant)
├── types/            # Tipos TypeScript
├── utils/            # Utilitários
└── styles/           # Estilos globais
```

---

## Observações
- Pré-requisito para todas as HUs de frontend
- Estrutura baseada no SCPA Frontend
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - MVP Frontend
- Estimativa: 2 dias
- Prioridade: Alta
