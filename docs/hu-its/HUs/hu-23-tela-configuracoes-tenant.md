# HU-23 – Tela de Configurações do Tenant | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como administrador do tenant, quero configurar as regras de operação e identidade visual da biblioteca, para personalizar a experiência conforme a realidade da instituição.
- Plataforma: React + TailwindCSS + shadcn/ui
- Interface de configuração do tenant (seção 3 do DVN)
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Identidade visual:** Configurar logotipo, cores primárias e secundárias.
2. **RN-2 – Domínio:** Configurar domínio customizado do tenant.
3. **RN-3 – Prazos:** Definir prazos padrão de empréstimo por categoria.
4. **RN-4 – Limites:** Definir limite máximo de livros por leitor.
5. **RN-5 – Renovações:** Definir número máximo de renovações permitidas.
6. **RN-6 – Multas:** Configurar valor da multa por dia de atraso.
7. **RN-7 – Monetização:** Ativar/desativar modelos de monetização.
8. **RN-8 – Notificações:** Configurar templates de email e notificações.

---

## Critérios de Aceite (CA)
1. **CA-1 – Upload logo:** Upload de imagem atualiza logo do tenant.
2. **CA-2 – Cores:** Color picker define cores primárias e secundárias.
3. **CA-3 – Preview:** Preview em tempo real das alterações visuais.
4. **CA-4 – Salvar regras:** Formulário salva prazos, limites e multas.
5. **CA-5 – Monetização:** Toggles ativam/desativam modelos.
6. **CA-6 – Domínio:** Campo de domínio com validação.
7. **CA-7 – Templates:** Editor de templates de email funciona.
8. **CA-8 – Aplicação imediata:** Alterações refletem imediatamente na interface.

---

## Cenários de Teste (CT)
1. **CT-1 – Upload logo:** Fazer upload → logo exibido no header.
2. **CT-2 – Alterar cor:** Selecionar azul → interface atualizada.
3. **CT-3 – Preview:** Alterar cor → preview mostra resultado.
4. **CT-4 – Salvar prazo:** Definir 14 dias → novos empréstimos usam 14 dias.
5. **CT-5 – Salvar limite:** Definir 5 livros → leitor limitado a 5.
6. **CT-6 – Salvar multa:** Definir R$2/dia → multas calculadas com R$2.
7. **CT-7 – Ativar taxa:** Ativar "Taxa por empréstimo" → campo de valor habilitado.
8. **CT-8 – Desativar assinatura:** Desativar "Assinatura" → planos ocultados.
9. **CT-9 – Editar template:** Alterar texto de email → emails usam novo texto.
10. **CT-10 – Domínio inválido:** Digitar domínio inválido → erro de validação.

---

## Observações
- Pré-requisito: HU-18 (Setup Frontend), HU-02 (Multi-Tenant)
- Configurações do tenant conforme seção 3.3 do DVN
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - MVP Frontend
- Estimativa: 4 dias
- Prioridade: Média
