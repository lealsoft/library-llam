# Documento de Visão Negocial (DVN)
## LLAM Biblioteca — Sistema de Gestão de Acervo, Empréstimos e Venda Digital

**Versão:** 2.0  
**Data:** Março/2026  
**Autor:** LLAM Tecnologia do Brasil Ltda  
**Classificação:** Documento de Negócio — Sem dados técnicos de implementação

---

## 1. Visão Geral do Sistema

O **LLAM Biblioteca** é uma plataforma SaaS de gestão de acervo bibliográfico desenvolvida pela LLAM Tecnologia do Brasil Ltda, projetada para ser licenciada a bibliotecas públicas, privadas, escolares, universitárias e institucionais. O sistema opera no modelo **White Label multi-tenant**: cada biblioteca contratante opera com sua própria identidade visual (logo, cores e domínio próprio), seu próprio acervo e suas próprias regras de negócio, sem interferência entre os tenants.

O Instituto LLAM é o primeiro tenant da plataforma, servindo simultaneamente como cliente fundador e ambiente de validação contínua do produto.

A plataforma cobre o ciclo completo de vida do livro dentro de uma instituição: catalogação, localização física, empréstimo de exemplares físicos — gratuito ou tarifado — e venda de conteúdo digital. Cada biblioteca contratante escolhe quais modelos de operação deseja ativar, compondo sua oferta de acordo com sua realidade e estratégia.

### 1.1 Motivação

Sistemas convencionais de biblioteca controlam o acervo por contagem simples: o livro tem status `DISPONÍVEL` ou `EMPRESTADO`. Esse modelo, embora funcional para operações básicas, apresenta fragilidades quando surgem perguntas como:

- Quem esteve com esse exemplar nos últimos 6 meses?
- Qual o histórico completo de movimentação do título X?
- Quantas unidades circularam em determinado período?
- Qual a receita gerada por empréstimos tarifados neste mês?
- Quantas licenças digitais foram vendidas por categoria?

O LLAM Biblioteca responde todas essas perguntas de forma nativa, porque toda movimentação é tratada como uma **transação de ativo bibliográfico** — um registro permanente, rastreável e auditável, nunca uma simples atualização de status.

### 1.2 Objetivos do Sistema

- Oferecer uma plataforma White Label completa para gestão de bibliotecas
- Controlar empréstimos de livros físicos como movimentações transacionais entre contas de ativos
- Suportar múltiplos modelos de monetização configuráveis por tenant
- Permitir a venda de conteúdo digital (PDF) como transação permanente e auditável
- Servir como vitrine tecnológica da capacidade de modelagem e engenharia da LLAM Tecnologia
- Atender os requisitos do desafio técnico proposto pela Capgemini, superando-os em arquitetura e profundidade

---

## 2. Modelo de Negócio LLAM Biblioteca

### 2.1 A LLAM como Fornecedora da Plataforma

A LLAM Tecnologia disponibiliza o LLAM Biblioteca para bibliotecas contratantes em dois modelos de licenciamento:

**Modelo SaaS (Software como Serviço):** A biblioteca contratante acessa a plataforma via internet, hospedada na infraestrutura da LLAM. Paga uma mensalidade fixa de acordo com o plano contratado (número de leitores, volume de acervo, módulos ativos). Não requer infraestrutura própria.

**Modelo On-Premise (Licença Local):** A biblioteca contratante instala a plataforma em sua própria infraestrutura. Paga uma licença de uso, com opção de contrato de suporte e atualização. Indicado para bibliotecas com requisitos de privacidade de dados ou que já possuem infraestrutura própria consolidada.

Em ambos os modelos, a biblioteca opera com sua própria identidade visual: logotipo, paleta de cores e domínio web próprio (ex: `biblioteca.minhainstituicao.edu.br`).

### 2.2 A Biblioteca Contratante como Operadora

Cada biblioteca contratante é um **tenant independente** dentro da plataforma. Ela é responsável por:

- Cadastrar e gerenciar seu próprio acervo
- Cadastrar e gerenciar seus próprios leitores
- Definir suas regras de operação (prazos, limites, multas)
- Escolher e configurar seus modelos de monetização junto ao leitor
- Gerir sua própria operação financeira junto aos leitores

A LLAM não interfere na operação interna de nenhum tenant. Cada biblioteca é soberana sobre seus dados e sua operação.

### 2.3 Modelos de Monetização Disponíveis para o Leitor

Cada biblioteca contratante pode ativar, de forma independente, um ou mais dos seguintes modelos:

**Empréstimo Gratuito:** O empréstimo de livros físicos não tem custo para o leitor. Modelo típico de bibliotecas públicas, escolares ou institucionais que oferecem o serviço como benefício. O controle transacional permanece ativo para fins de rastreabilidade e auditoria, mesmo sem cobrança.

**Taxa por Empréstimo (Avulso):** Cada empréstimo realizado gera uma cobrança avulsa ao leitor. O valor pode ser fixo por empréstimo, variável por título, por prazo ou por categoria de livro. O lançamento financeiro é registrado como parte da mesma transação do empréstimo bibliográfico.

**Mensalidade (Assinatura do Leitor):** O leitor paga uma mensalidade que lhe dá direito a um número determinado de empréstimos simultâneos e/ou um volume mensal de empréstimos. Planos configuráveis pela biblioteca (básico, padrão, premium). Indicado para bibliotecas que desejam receita recorrente e previsível.

**Venda de Conteúdo Digital (PDF):** A biblioteca comercializa versões digitais de títulos do seu acervo. O leitor adquire o direito de acesso permanente ao conteúdo. A transação é definitiva — não há devolução de conteúdo digital após liberação do acesso.

---

## 3. Arquitetura de Tenants (Multi-Tenancy)

### 3.1 Isolamento por Tenant

Cada biblioteca contratante opera em seu próprio espaço isolado dentro da plataforma. Os dados de acervo, leitores, transações e configurações de um tenant não são visíveis nem acessíveis por outro tenant. O isolamento é garantido estruturalmente, não apenas por configuração de acesso.

### 3.2 Personalização White Label

Cada tenant configura sua identidade visual:

- **Logotipo:** imagem da instituição exibida na interface e nos documentos gerados
- **Paleta de cores:** cores primárias e secundárias da interface
- **Domínio próprio:** a plataforma é acessada pelo leitor através do domínio da própria instituição, sem referência visual à LLAM

O leitor final não precisa saber que a plataforma é fornecida pela LLAM. A experiência é integralmente da biblioteca contratante.

### 3.3 Configuração por Tenant

Cada biblioteca configura de forma independente:

- Modelos de monetização ativos
- Valores de taxas e mensalidades
- Prazos padrão de empréstimo por categoria
- Limite máximo de livros emprestados por leitor
- Número máximo de renovações permitidas
- Política de multas por atraso
- Categorias e classificações do próprio acervo

---

## 4. O Acervo como Ativo

### 4.1 Conceito Central

No LLAM Biblioteca, o livro não é apenas um registro com status. Cada exemplar físico e cada licença digital é tratado como uma unidade de ativo patrimonial da biblioteca. A biblioteca possui uma conta corrente de ativos bibliográficos. O leitor também possui sua conta. As operações de empréstimo e venda são transferências entre essas contas.

Esse modelo é análogo ao funcionamento de um sistema financeiro de dupla entrada:

- A biblioteca tem saldo positivo de exemplares (o que possui em seu acervo)
- Quando empresta, debita de si mesma e credita no leitor
- Quando o leitor devolve, a operação se inverte
- Em nenhum momento uma unidade some ou aparece do nada — ela sempre está em alguma conta

### 4.2 Vantagens do Modelo Transacional

**Rastreabilidade completa:** Cada exemplar tem histórico de onde esteve e com quem, desde a entrada no acervo.

**Saldo real a qualquer momento:** O saldo disponível é a soma de todas as transações do ativo, não uma coluna de quantidade sujeita a inconsistência.

**Auditoria nativa:** As próprias transações são o log. Não é possível alterar retroativamente sem deixar rastro.

**Atomicidade:** O empréstimo de múltiplos livros em um único ato é um lote transacional — ou todos os lançamentos são confirmados, ou nenhum é. Não existe estado inconsistente.

**Base financeira integrada:** Quando o empréstimo é tarifado, o lançamento financeiro faz parte da mesma transação bibliográfica. Não há descasamento entre o registro do empréstimo e o registro da cobrança.

**Escalabilidade de negócio:** O mesmo modelo suporta, sem alteração estrutural, empréstimos gratuitos, empréstimos tarifados, vendas digitais, doações, devoluções com multa, renovações e qualquer operação futura.

---

## 5. Módulo 1 — Gestão do Acervo

### 5.1 Cadastro de Títulos

O sistema mantém o catálogo de títulos com suas informações editoriais: título, autor(es), ISBN, editora, edição, idioma, ano de publicação, sinopse e categoria temática. A categorização é dinâmica — novas classificações são criadas pelo administrador do tenant sem alteração no sistema.

### 5.2 Exemplares Físicos

Cada título pode ter múltiplos exemplares físicos. Um exemplar é uma unidade individual do título com localização física precisa:

- **Seção:** área temática (ex: Tecnologia, Romance, Ciências Exatas)
- **Corredor:** identificação do corredor dentro da seção
- **Prateleira:** número ou código da prateleira no corredor
- **Posição:** slot ou código dentro da prateleira

### 5.3 Estado do Exemplar

O estado é sempre derivado do histórico de transações:

- **Disponível:** em seu lugar na prateleira, apto para empréstimo
- **Emprestado:** em posse de um leitor, com prazo ativo
- **Em processamento:** recebido mas ainda não devolvido ao acervo
- **Conservação:** em manutenção ou restauro
- **Baixado:** retirado definitivamente do acervo

---

## 6. Módulo 2 — Empréstimo de Livros Físicos

### 6.1 Fluxo de Empréstimo

**Solicitação:** O leitor indica os títulos que deseja emprestar presencialmente ou pelo sistema.

**Verificação:** O sistema verifica, para cada título: disponibilidade de exemplar, situação cadastral do leitor (pendências, limite de empréstimos) e elegibilidade conforme o modelo de monetização ativo (assinatura válida, saldo disponível, etc.).

**Lançamento transacional:** Para cada exemplar confirmado: débito na conta da biblioteca e crédito na conta do leitor. Se o empréstimo for tarifado, o lançamento financeiro correspondente é registrado na mesma operação. O conjunto forma um lote atômico.

**Registro de prazo:** O prazo de devolução é registrado como atributo da transação.

### 6.2 Fluxo de Devolução

**Recebimento:** O leitor devolve os exemplares.

**Verificação de prazo:** Atrasos geram cálculo automático de multa conforme as regras do tenant.

**Lançamento de devolução:** Débito na conta do leitor e crédito na conta da biblioteca. O exemplar retorna ao estado "em processamento" até ser inspecionado e reposto na prateleira.

### 6.3 Renovação e Reserva

**Renovação:** Prolongamento do prazo de uma transação ativa sem devolução física. Regras de limite de renovações configuráveis por tenant.

**Reserva:** O leitor pode reservar um exemplar atualmente emprestado. Ao ser devolvido, o sistema notifica o leitor reservante e bloqueia o exemplar por um período configurável.

---

## 7. Módulo 3 — Venda de Conteúdo Digital (PDF)

### 7.1 Catálogo Digital

O catálogo digital é mantido separadamente do acervo físico, mas pode estar vinculado ao mesmo título. Um livro pode existir apenas fisicamente, apenas digitalmente, ou em ambos os formatos.

### 7.2 Fluxo de Compra

**Seleção e pagamento:** O leitor seleciona o título digital e realiza o pagamento pelo meio configurado pelo tenant.

**Lançamento transacional:** Confirmado o pagamento: débito na conta digital da biblioteca (saída de uma licença) e crédito permanente na conta do leitor. Não há prazo — o crédito é definitivo.

**Liberação de acesso:** O leitor recebe acesso imediato ao download e o título aparece em sua biblioteca pessoal.

### 7.3 Biblioteca Pessoal do Leitor

Cada leitor possui uma área "Minha Biblioteca" contendo: títulos digitais adquiridos com acesso permanente, histórico completo de empréstimos físicos e saldo de empréstimos disponíveis conforme o plano ativo.

---

## 8. Módulo 4 — Gestão de Usuários

### 8.1 Cadastro de Leitor

Dados do leitor: nome completo, login único, credencial de acesso segura (senha nunca armazenada ou trafegada em texto puro), data de cadastro, situação (ativo, suspenso, bloqueado) e vínculo institucional.

### 8.2 Autenticação Segura

O acesso ao sistema exige autenticação. A consulta de dados de um leitor, incluindo histórico de empréstimos e títulos adquiridos, só é liberada após validação das credenciais. Credenciais incorretas resultam em erro sem exposição de informações do cadastro.

### 8.3 Planos e Limites por Leitor

Cada leitor pode estar associado a um plano de assinatura (quando o tenant utiliza esse modelo), com regras específicas: quantidade máxima de livros simultâneos, prazo padrão, número máximo de renovações e benefícios na compra de conteúdo digital.

---

## 9. Atores e Responsabilidades

| Ator | Nível | Responsabilidades |
|---|---|---|
| LLAM Tecnologia | Plataforma | Fornece, mantém e evolui o produto; suporta os tenants contratantes |
| Administrador do Tenant | Biblioteca | Configura regras, modelos de monetização, gerencia acervo e usuários, acessa relatórios |
| Bibliotecário / Operador | Biblioteca | Processa empréstimos e devoluções no balcão, gerencia acervo físico |
| Leitor / Aluno | Usuário final | Realiza empréstimos, devoluções, reservas e compras digitais |

---

## 10. Regras de Negócio Centrais

**RN-01:** Todo empréstimo gera lançamentos transacionais pareados (débito biblioteca / crédito leitor). Não existe empréstimo sem lançamento.

**RN-02:** Toda devolução gera lançamentos transacionais inversos (débito leitor / crédito biblioteca). Não existe devolução sem lançamento.

**RN-03:** O empréstimo simultâneo de múltiplos exemplares é um lote atômico — ou todos os lançamentos são confirmados, ou nenhum é.

**RN-04:** O saldo disponível de um exemplar é sempre calculado a partir do histórico de transações. Não há campo de status editável diretamente.

**RN-05:** Quando o empréstimo é tarifado, o lançamento financeiro é parte integrante da transação bibliográfica — ambos confirmados ou ambos revertidos.

**RN-06:** A venda de conteúdo digital é uma transação permanente. Não existe estorno ou devolução após liberação do acesso.

**RN-07:** A senha do leitor jamais é armazenada ou trafegada em texto puro.

**RN-08:** Leitor com empréstimos em atraso não pode realizar novos empréstimos até regularizar a situação.

**RN-09:** Leitor com pendência financeira não pode realizar novos empréstimos ou compras digitais.

**RN-10:** Todo o histórico de transações é imutável — registros não podem ser deletados, apenas complementados.

**RN-11:** Os dados de cada tenant são completamente isolados dos demais. Nenhum operador ou leitor de um tenant acessa dados de outro tenant.

**RN-12:** As configurações de regras, valores e modelos de monetização são definidas por tenant e não interferem em outros tenants.

---

## 11. Diferenciais em Relação ao Modelo Convencional

| Característica | Modelo Convencional | LLAM Biblioteca |
|---|---|---|
| Controle de estoque | Contador de quantidade | Saldo por movimentações transacionais |
| Rastreabilidade | Status atual | Histórico completo desde a entrada no acervo |
| Auditoria | Log separado (pode ser omitido) | Transações são o próprio log — imutável |
| Consistência | Depende de atualização correta de status | Garantida pela natureza do modelo de dupla entrada |
| Empréstimo múltiplo | Múltiplas atualizações independentes | Lote atômico — tudo ou nada |
| Monetização | Geralmente não integrada ao controle de acervo | Lançamento financeiro parte da transação bibliográfica |
| Multi-tenant | Instalação separada por cliente | Plataforma única, tenants isolados estruturalmente |
| White Label | Não disponível | Logo, cores e domínio próprio por tenant |
| Escalabilidade | Requer mudança estrutural para novos casos de uso | Novos tipos de operação são apenas novas classificações |

---

## 12. Alinhamento com o Desafio Técnico

O desafio proposto solicita um sistema de biblioteca com CRUD de usuários, CRUD de livros e gestão de empréstimos, utilizando Java 17, Spring Boot e banco de dados embarcado.

O LLAM Biblioteca atende integralmente esses requisitos e os supera:

**Gestão de usuários:** CRUD completo com autenticação segura por credenciais criptografadas. A leitura de dados exige validação de login e senha, retornando histórico completo de empréstimos e aquisições digitais.

**Gestão de livros:** Catálogo com exemplares físicos (localização em prateleira) e conteúdo digital, com classificação dinâmica sem listas fixas.

**Gestão de empréstimos:** Implementada via modelo transacional de dupla entrada, superando o modelo de simples atualização de status. O empréstimo é um evento registrado e imutável.

**Coleção de requisições:** Collection completa (Postman/Insomnia) cobrindo todos os fluxos do desafio: CRUD de usuário, autenticação segura, empréstimo, devolução e compra digital.

---

## 13. Visão de Evolução do Produto

**Fase 1 — Atendimento ao desafio técnico:** Sistema funcional com H2, cobrindo todos os requisitos da avaliação com arquitetura transacional completa.

**Fase 2 — Implantação no Instituto LLAM:** Migração para MySQL, integração com cadastro de alunos existente, interface web, módulo de venda digital ativo.

**Fase 3 — Produto comercial:** Plataforma multi-tenant White Label, onboarding de primeiros clientes externos, gateway de pagamento integrado, planos SaaS e On-Premise disponíveis.

**Fase 4 — Integração plena com ecossistema LLAM:** Conexão com o SCPA para controle de acesso, autenticação SSO via Keycloak, dashboards no modelo HAPI, API pública para integrações de terceiros.

---

*Documento produzido pela LLAM Tecnologia do Brasil Ltda — Março/2026*
*Este documento é de visão negocial. As especificações técnicas de implementação são tratadas nas Histórias de Usuário (HU) correspondentes.*