# HU-19 – Tela de Catálogo de Títulos | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como leitor, quero navegar pelo catálogo de títulos da biblioteca, para encontrar livros de meu interesse e verificar disponibilidade.
- Plataforma: React + TailwindCSS + shadcn/ui
- Interface principal de busca e navegação do acervo
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Busca textual:** Buscar por título, autor, ISBN ou palavras-chave.
2. **RN-2 – Filtro por categoria:** Filtrar títulos por categoria temática.
3. **RN-3 – Filtro por disponibilidade:** Filtrar apenas títulos com exemplares disponíveis.
4. **RN-4 – Ordenação:** Ordenar por título, autor, data de cadastro, popularidade.
5. **RN-5 – Paginação:** Resultados paginados com lazy loading.
6. **RN-6 – Detalhes do título:** Exibir informações completas ao clicar.
7. **RN-7 – Disponibilidade:** Mostrar quantidade de exemplares disponíveis.
8. **RN-8 – Ação rápida:** Botões para emprestar, reservar ou comprar digital.

---

## Critérios de Aceite (CA)
1. **CA-1 – Listagem:** Tela exibe títulos do acervo com paginação.
2. **CA-2 – Busca:** Campo de busca filtra resultados em tempo real.
3. **CA-3 – Filtro categoria:** Dropdown de categorias filtra resultados.
4. **CA-4 – Filtro disponibilidade:** Checkbox "Apenas disponíveis" funciona.
5. **CA-5 – Ordenação:** Dropdown de ordenação altera ordem dos resultados.
6. **CA-6 – Card de título:** Exibe capa, título, autor, disponibilidade.
7. **CA-7 – Modal de detalhes:** Clicar no título abre modal com informações completas.
8. **CA-8 – Ações:** Botões de emprestar/reservar/comprar funcionais.

---

## Cenários de Teste (CT)
1. **CT-1 – Carregar catálogo:** Acessar tela → títulos carregados com paginação.
2. **CT-2 – Buscar por título:** Digitar "Java" → títulos com "Java" exibidos.
3. **CT-3 – Buscar por autor:** Digitar "Machado" → títulos do autor exibidos.
4. **CT-4 – Filtrar categoria:** Selecionar "Tecnologia" → apenas títulos da categoria.
5. **CT-5 – Filtrar disponíveis:** Marcar checkbox → apenas com exemplares disponíveis.
6. **CT-6 – Ordenar por título:** Selecionar "Título A-Z" → ordem alfabética.
7. **CT-7 – Ver detalhes:** Clicar em título → modal com sinopse, editora, ano, etc.
8. **CT-8 – Emprestar:** Clicar "Emprestar" → fluxo de empréstimo iniciado.
9. **CT-9 – Reservar:** Título sem exemplar disponível → botão "Reservar" habilitado.
10. **CT-10 – Comprar digital:** Título com versão digital → botão "Comprar PDF".

---

## Observações
- Pré-requisito: HU-18 (Setup Frontend), HU-03 (Cadastro de Títulos)
- Interface principal do leitor
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - MVP Frontend
- Estimativa: 4 dias
- Prioridade: Alta
