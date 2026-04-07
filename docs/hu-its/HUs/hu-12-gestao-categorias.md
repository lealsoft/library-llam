# HU-12 – Gestão de Categorias do Acervo | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como administrador do tenant, quero criar e gerenciar categorias temáticas do acervo, para organizar os títulos de forma dinâmica e personalizada.
- Plataforma: Spring Boot + MySQL
- Categorias são Classes com ClassScheme LIB_TITULO_CATEGORIA
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Categorias dinâmicas:** Novas categorias criadas pelo administrador sem alteração no sistema (seção 5.1 DVN).
2. **RN-2 – Hierarquia opcional:** Categorias podem ter subcategorias (ex: Tecnologia > Programação > Java).
3. **RN-3 – Isolamento por tenant:** Cada tenant tem suas próprias categorias.
4. **RN-4 – Prazo por categoria:** Cada categoria pode ter prazo de empréstimo específico.
5. **RN-5 – Múltiplas categorias:** Um título pode pertencer a várias categorias.
6. **RN-6 – Categoria obrigatória:** Todo título deve ter pelo menos uma categoria.
7. **RN-7 – Soft delete:** Categoria pode ser desativada sem exclusão física.

---

## Critérios de Aceite (CA)
1. **CA-1 – Criar categoria:** POST /api/categorias cria categoria com sucesso.
2. **CA-2 – Listar categorias:** GET /api/categorias lista categorias do tenant.
3. **CA-3 – Criar subcategoria:** POST /api/categorias com parentId cria subcategoria.
4. **CA-4 – Editar categoria:** PUT /api/categorias/{id} atualiza dados.
5. **CA-5 – Desativar categoria:** PATCH /api/categorias/{id}/status desativa categoria.
6. **CA-6 – Definir prazo:** Categoria com prazo específico → empréstimos usam esse prazo.
7. **CA-7 – Vincular título:** POST /api/titulos/{id}/categorias vincula título a categorias.
8. **CA-8 – Árvore de categorias:** GET /api/categorias/arvore retorna estrutura hierárquica.

---

## Cenários de Teste (CT)
1. **CT-1 – Criar categoria raiz:** Criar "Tecnologia" → categoria criada.
2. **CT-2 – Criar subcategoria:** Criar "Programação" filho de "Tecnologia" → hierarquia correta.
3. **CT-3 – Prazo específico:** Categoria "Referência" com prazo 7 dias → empréstimo usa 7 dias.
4. **CT-4 – Múltiplas categorias:** Título em "Java" e "Backend" → ambas vinculadas.
5. **CT-5 – Desativar categoria:** Desativar "Romance" → não aparece em listagens.
6. **CT-6 – Títulos órfãos:** Desativar categoria com títulos → alerta gerado.
7. **CT-7 – Árvore completa:** GET /api/categorias/arvore → estrutura hierárquica JSON.
8. **CT-8 – Isolamento:** Categoria de tenant A não aparece em tenant B.
9. **CT-9 – Buscar por categoria:** GET /api/titulos?categoria=Java → títulos filtrados.

---

## Observações
- Pré-requisito: HU-01 (Modelagem de Dados)
- Categorização dinâmica conforme seção 5.1 do DVN
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 2 dias
- Prioridade: Média
