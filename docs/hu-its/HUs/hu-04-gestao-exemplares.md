# HU-04 – Gestão de Exemplares Físicos | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como bibliotecário, quero cadastrar e gerenciar exemplares físicos de cada título, para controlar a localização precisa e o estado de cada unidade do acervo.
- Plataforma: Spring Boot + MySQL
- Exemplar é uma Class vinculada ao Título via ClassClass
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Vinculação obrigatória:** Todo exemplar deve estar vinculado a um título.
2. **RN-2 – Código único:** Cada exemplar tem código único dentro do tenant (ex: ISBN-001).
3. **RN-3 – Localização física:** Exemplar tem seção, corredor, prateleira e posição.
4. **RN-4 – Estado derivado:** Estado do exemplar é derivado do histórico de transações.
5. **RN-5 – Estados possíveis:** DISPONIVEL, EMPRESTADO, EM_PROCESSAMENTO, CONSERVACAO, BAIXADO.
6. **RN-6 – Múltiplos exemplares:** Um título pode ter N exemplares.
7. **RN-7 – Baixa definitiva:** Exemplar baixado é removido do acervo ativo permanentemente.
8. **RN-8 – Histórico preservado:** Mesmo exemplar baixado mantém histórico de transações.

---

## Critérios de Aceite (CA)
1. **CA-1 – Criar exemplar:** POST /api/titulos/{tituloId}/exemplares cria exemplar.
2. **CA-2 – Listar exemplares:** GET /api/titulos/{tituloId}/exemplares lista exemplares do título.
3. **CA-3 – Localização:** Exemplar criado com seção, corredor, prateleira e posição.
4. **CA-4 – Estado inicial:** Exemplar criado com estado DISPONIVEL.
5. **CA-5 – Código único:** Código duplicado no tenant → erro 409.
6. **CA-6 – Atualizar localização:** PUT /api/exemplares/{id}/localizacao atualiza posição.
7. **CA-7 – Baixar exemplar:** POST /api/exemplares/{id}/baixa registra baixa definitiva.
8. **CA-8 – Consultar estado:** GET /api/exemplares/{id} retorna estado atual derivado.

---

## Cenários de Teste (CT)
1. **CT-1 – Criar exemplar:** Vincular ao título → exemplar criado com estado DISPONIVEL.
2. **CT-2 – Código duplicado:** Criar com código existente → erro 409.
3. **CT-3 – Listar por título:** GET /api/titulos/123/exemplares → lista exemplares.
4. **CT-4 – Localização completa:** Criar com seção/corredor/prateleira/posição → dados salvos.
5. **CT-5 – Atualizar posição:** Mover exemplar para nova prateleira → localização atualizada.
6. **CT-6 – Baixar exemplar:** Registrar baixa → estado = BAIXADO.
7. **CT-7 – Estado derivado:** Após empréstimo → estado = EMPRESTADO.
8. **CT-8 – Buscar disponíveis:** GET /api/exemplares?estado=DISPONIVEL → apenas disponíveis.
9. **CT-9 – Histórico preservado:** Exemplar baixado → histórico de transações acessível.

---

## Observações
- Pré-requisito: HU-03 (Cadastro de Títulos)
- Estado é sempre calculado, nunca editado diretamente (RN-04 do DVN)
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 1 - MVP Backend
- Estimativa: 3 dias
- Prioridade: Alta
