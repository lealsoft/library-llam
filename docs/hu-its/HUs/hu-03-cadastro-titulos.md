# HU-03 – Cadastro de Títulos Bibliográficos | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como bibliotecário, quero cadastrar e gerenciar títulos do acervo, para manter o catálogo atualizado com informações editoriais completas.
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Campos obrigatórios:** Título, autor(es), ISBN são obrigatórios.
2. **RN-2 – Campos opcionais:** Subtítulo, editora, edição, idioma, ano de publicação, número de páginas, sinopse, categoria, capa URL.
3. **RN-3 – Categoria dinâmica:** Categorias são criadas pelo administrador do tenant.
4. **RN-4 – Modelo EVA:** Metadados são armazenados usando Entity-Value-Attribute (tabelas `DublinCoreValueString`, `DublinCoreValueInteger`, `DublinCoreValueText`) com `ClassId` da camada semântica `LIB_ATRIBUTO_TITULO`.
5. **RN-5 – ISBN único por tenant:** Mesmo ISBN não pode ser cadastrado duas vezes no mesmo tenant.
6. **RN-6 – Múltiplos autores:** Título pode ter vários autores.
7. **RN-7 – Isolamento por tenant:** Título pertence exclusivamente ao tenant que o cadastrou.
8. **RN-8 – Soft delete:** Título pode ser desativado sem exclusão física.
9. **RN-9 – Vinculação digital:** Título pode ter versão digital (PDF) vinculada.

---

## Critérios de Aceite (CA)
1. **CA-1 – Criar título:** POST /api/titulos cria título com sucesso.
2. **CA-2 – Listar títulos:** GET /api/titulos retorna títulos do tenant com paginação.
3. **CA-3 – Buscar título:** GET /api/titulos?q=termo busca por título, autor ou ISBN.
4. **CA-4 – Editar título:** PUT /api/titulos/{id} atualiza dados do título.
5. **CA-5 – Desativar título:** PATCH /api/titulos/{id}/status desativa título.
6. **CA-6 – Validação ISBN:** Título com ISBN duplicado no tenant → erro 409.
7. **CA-7 – Categorizar:** Título pode ser vinculado a uma ou mais categorias.
8. **CA-8 – Upload capa:** POST /api/titulos/{id}/capa faz upload da imagem de capa.

---

## Cenários de Teste (CT)
1. **CT-1 – Criar título válido:** Preencher campos obrigatórios → título criado.
2. **CT-2 – Criar sem ISBN:** Omitir ISBN → erro de validação.
3. **CT-3 – ISBN duplicado:** Cadastrar ISBN existente → erro 409.
4. **CT-4 – Buscar por autor:** GET /api/titulos?q=Machado → retorna títulos do autor.
5. **CT-5 – Listar paginado:** GET /api/titulos?page=0&size=10 → 10 títulos por página.
6. **CT-6 – Editar título:** Alterar sinopse → dados atualizados.
7. **CT-7 – Desativar título:** Desativar → título não aparece em buscas padrão.
8. **CT-8 – Múltiplos autores:** Cadastrar título com 3 autores → todos salvos.
9. **CT-9 – Isolamento:** Título de tenant A não aparece em busca de tenant B.

---

## Observações
- Pré-requisito: HU-01 (Modelagem), HU-02 (Multi-Tenant)
- Categorias são gerenciadas em HU separada
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 3 dias
- Prioridade: Alta
