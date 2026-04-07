# HU-21 – Tela Backoffice de Empréstimos e Devoluções | 🚫 ZERO HARDCODED

## Visão e Contexto
- Como bibliotecário, quero processar empréstimos e devoluções no balcão, para atender leitores presencialmente com agilidade e controle.
- Plataforma: React + TailwindCSS + shadcn/ui
- Interface de balcão para operações do Módulo 2 (Empréstimo de Livros Físicos)
- Detalhes técnicos: ver ITS correspondente

---

## Regras de Negócio (RN)
1. **RN-1 – Identificar leitor:** Buscar leitor por nome, login ou código.
2. **RN-2 – Verificar situação:** Exibir situação do leitor (pendências, limite).
3. **RN-3 – Leitura de código:** Ler código de barras do exemplar.
4. **RN-4 – Empréstimo múltiplo:** Processar vários exemplares em uma operação.
5. **RN-5 – Devolução rápida:** Processar devolução apenas com código do exemplar.
6. **RN-6 – Multa automática:** Exibir multa calculada em devoluções atrasadas.
7. **RN-7 – Recibo:** Gerar recibo de empréstimo/devolução.
8. **RN-8 – Histórico rápido:** Visualizar empréstimos ativos do leitor.

---

## Critérios de Aceite (CA)
1. **CA-1 – Buscar leitor:** Campo de busca encontra leitor rapidamente.
2. **CA-2 – Situação exibida:** Card mostra pendências, limite e empréstimos ativos.
3. **CA-3 – Adicionar exemplar:** Digitar/escanear código adiciona à lista.
4. **CA-4 – Confirmar empréstimo:** Botão processa todos os exemplares da lista.
5. **CA-5 – Devolução rápida:** Digitar código → devolução processada.
6. **CA-6 – Multa exibida:** Devolução atrasada → valor da multa exibido.
7. **CA-7 – Recibo gerado:** Após operação → opção de imprimir recibo.
8. **CA-8 – Bloqueio visual:** Leitor com pendência → alerta vermelho.

---

## Cenários de Teste (CT)
1. **CT-1 – Buscar leitor:** Digitar "João Silva" → leitor encontrado.
2. **CT-2 – Leitor com pendência:** Buscar leitor com multa → alerta exibido.
3. **CT-3 – Adicionar exemplar:** Escanear código → exemplar adicionado à lista.
4. **CT-4 – Exemplar indisponível:** Escanear exemplar emprestado → erro exibido.
5. **CT-5 – Empréstimo múltiplo:** Adicionar 3 exemplares → confirmar → 3 empréstimos.
6. **CT-6 – Devolução no prazo:** Devolver exemplar → sucesso sem multa.
7. **CT-7 – Devolução atrasada:** Devolver atrasado → multa calculada e exibida.
8. **CT-8 – Imprimir recibo:** Clicar "Imprimir" → recibo em PDF.
9. **CT-9 – Limite atingido:** Leitor no limite → alerta ao tentar emprestar.
10. **CT-10 – Histórico:** Clicar "Ver empréstimos" → lista de ativos.

---

## Observações
- Pré-requisito: HU-18 (Setup Frontend), HU-07 (Empréstimo), HU-08 (Devolução)
- Interface de balcão do Módulo 2 do DVN
- Otimizada para uso com leitor de código de barras
- Os detalhes técnicos de implementação estão descritos na ITS correspondente
- Fase: 2 - MVP Frontend
- Estimativa: 4 dias
- Prioridade: Alta
