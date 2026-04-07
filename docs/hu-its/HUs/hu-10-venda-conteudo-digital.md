# HU-10 – Venda de Conteúdo Digital (PDF) | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como leitor, quero comprar versões digitais de títulos do acervo, para ter acesso permanente ao conteúdo em formato PDF.
- Transação definitiva — não há devolução após liberação do acesso (RN-06 DVN)
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Catálogo digital separado:** Catálogo digital é mantido separadamente do físico.
2. **RN-2 – Vinculação opcional:** Título digital pode ou não estar vinculado a título físico.
3. **RN-3 – Transação permanente:** Venda é definitiva, sem estorno após liberação (RN-06 DVN).
4. **RN-4 – Débito de licença:** Venda gera débito na conta digital da biblioteca.
5. **RN-5 – Crédito permanente:** Leitor recebe crédito permanente (acesso vitalício).
6. **RN-6 – Pendência financeira:** Leitor com pendência não pode comprar (RN-09 DVN).
7. **RN-7 – Pagamento confirmado:** Acesso só é liberado após confirmação de pagamento.
8. **RN-8 – Download ilimitado:** Leitor pode baixar o PDF quantas vezes quiser.

---

## Critérios de Aceite (CA)
1. **CA-1 – Listar catálogo digital:** GET /api/catalogo-digital lista títulos disponíveis.
2. **CA-2 – Iniciar compra:** POST /api/compras/digital inicia processo de compra.
3. **CA-3 – Confirmar pagamento:** POST /api/compras/{id}/confirmar libera acesso.
4. **CA-4 – Biblioteca pessoal:** GET /api/leitores/{id}/biblioteca lista títulos adquiridos.
5. **CA-5 – Download:** GET /api/downloads/{tituloId} retorna URL de download.
6. **CA-6 – Transação registrada:** Compra gera transação tipo VENDA_DIGITAL.
7. **CA-7 – Pendência bloqueia:** Leitor com pendência → erro 403.
8. **CA-8 – Sem estorno:** Tentativa de estorno → erro 405.

---

## Cenários de Teste (CT)
1. **CT-1 – Listar catálogo:** GET /api/catalogo-digital → lista títulos com preço.
2. **CT-2 – Compra válida:** Leitor ativo + pagamento confirmado → acesso liberado.
3. **CT-3 – Pendência financeira:** Leitor com multa pendente → erro 403.
4. **CT-4 – Biblioteca pessoal:** Após compra → título aparece em "Minha Biblioteca".
5. **CT-5 – Download:** Acessar download → URL assinada gerada.
6. **CT-6 – Download múltiplo:** Baixar 5 vezes → todas bem-sucedidas.
7. **CT-7 – Tentativa de estorno:** POST /api/compras/{id}/estorno → erro 405.
8. **CT-8 – Título já adquirido:** Tentar comprar novamente → erro 409.
9. **CT-9 – Pagamento pendente:** Compra sem confirmação → acesso não liberado.
10. **CT-10 – Transação registrada:** Compra → transação VENDA_DIGITAL no histórico.

---

## Observações
- Pré-requisito: HU-06 (Modelo Transacional), HU-05 (Cadastro de Leitores)
- Módulo 3 do DVN (seção 7)
- Integração com gateway de pagamento na Fase 3
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - Funcionalidades Avançadas
- Estimativa: 5 dias
- Prioridade: Média
