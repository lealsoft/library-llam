# HU-20 – Tela Backoffice de Gestão do Acervo | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como bibliotecário, quero gerenciar títulos e exemplares do acervo via interface administrativa, para manter o catálogo atualizado e controlar a localização física dos exemplares.
- Plataforma: React + TailwindCSS + shadcn/ui
- Interface administrativa para gestão do Módulo 1 (Gestão do Acervo)
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – CRUD de títulos:** Criar, editar, visualizar e desativar títulos.
2. **RN-2 – CRUD de exemplares:** Criar, editar, visualizar e baixar exemplares.
3. **RN-3 – Upload de capa:** Fazer upload de imagem de capa do título.
4. **RN-4 – Localização:** Definir seção, corredor, prateleira e posição do exemplar.
5. **RN-5 – Importação em lote:** Importar títulos via arquivo CSV.
6. **RN-6 – Etiquetas:** Gerar etiquetas de código de barras para exemplares.
7. **RN-7 – Histórico:** Visualizar histórico de movimentações do exemplar.
8. **RN-8 – Baixa:** Registrar baixa definitiva de exemplar com motivo.

---

## Critérios de Aceite (CA)
1. **CA-1 – Listar títulos:** Tabela com títulos, busca e paginação.
2. **CA-2 – Criar título:** Formulário cria título com validação.
3. **CA-3 – Editar título:** Formulário edita título existente.
4. **CA-4 – Listar exemplares:** Expandir título mostra exemplares vinculados.
5. **CA-5 – Criar exemplar:** Formulário cria exemplar com localização.
6. **CA-6 – Upload capa:** Drag-and-drop ou seleção de arquivo funciona.
7. **CA-7 – Importar CSV:** Upload de CSV importa títulos em lote.
8. **CA-8 – Gerar etiqueta:** Botão gera PDF com código de barras.

---

## Cenários de Teste (CT)
1. **CT-1 – Listar títulos:** Acessar tela → títulos listados com paginação.
2. **CT-2 – Buscar título:** Digitar ISBN → título encontrado.
3. **CT-3 – Criar título:** Preencher formulário → título criado.
4. **CT-4 – Validação:** Omitir ISBN → erro de validação exibido.
5. **CT-5 – Editar título:** Alterar sinopse → dados atualizados.
6. **CT-6 – Criar exemplar:** Vincular ao título com localização → exemplar criado.
7. **CT-7 – Upload capa:** Arrastar imagem → preview exibido → salvar → capa atualizada.
8. **CT-8 – Importar CSV:** Upload de 100 títulos → todos importados.
9. **CT-9 – Gerar etiqueta:** Clicar "Etiqueta" → PDF com código de barras.
10. **CT-10 – Baixar exemplar:** Registrar baixa → exemplar marcado como BAIXADO.
11. **CT-11 – Histórico:** Clicar "Histórico" → lista de movimentações exibida.

---

## Observações
- Pré-requisito: HU-18 (Setup Frontend), HU-03 (Títulos), HU-04 (Exemplares)
- Interface administrativa do Módulo 1 do DVN
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - MVP Frontend
- Estimativa: 5 dias
- Prioridade: Alta
