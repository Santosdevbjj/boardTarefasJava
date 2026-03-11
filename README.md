![TonnieJava0002](https://github.com/user-attachments/assets/3c8db5cd-403c-436c-a8a6-8a0dc04d429e)

# 📋 Board de Tarefas — Java Spring Boot

> **Bootcamp TONNIE — Java and AI in Europe**

---

## 1. 🧩 Problema de Negócio

Equipes de desenvolvimento e gestão de projetos frequentemente perdem produtividade por falta de visibilidade sobre o status real das suas tarefas. Sem um sistema centralizado, é difícil responder perguntas simples como:

- **Quais tarefas estão bloqueadas e por quê?**
- **Quem moveu uma tarefa e quando?**
- **Qual o histórico de uma entrega que atrasou?**

O objetivo deste projeto é resolver esse problema construindo um **Board de Tarefas no estilo Kanban**, com rastreabilidade completa de eventos — desde a criação de uma tarefa até seu cancelamento.

---

## 2. 📌 Contexto

O projeto foi desenvolvido como parte do Bootcamp TONNIE — Java and AI in Europe, com foco em aplicar boas práticas de desenvolvimento Java em um sistema com regras de negócio reais:

- Múltiplos boards para diferentes projetos;
- Colunas personalizadas por board (ex.: Inicial, Em Progresso, Revisão, Concluído, Cancelado);
- Cards (tarefas) com estados, bloqueios e movimentações rastreáveis;
- Persistência em banco relacional com auditoria completa via `TaskHistory`.

A aplicação simula um cenário real de uma ferramenta interna de gestão, onde **responsabilidade, rastreabilidade e histórico de ações** são requisitos fundamentais.

---

## 3. 📐 Premissas da Solução

Para o desenvolvimento, as seguintes premissas foram adotadas:

- Toda ação relevante sobre uma tarefa (criação, movimentação, bloqueio, desbloqueio, cancelamento) gera um registro imutável em `TaskHistory`;
- O bloqueio de uma tarefa **exige motivo** — não existe bloqueio sem justificativa registrada;
- A movimentação entre colunas pode acionar bloqueio/desbloqueio de forma encadeada;
- A interface é **server-side rendering** com Thymeleaf, priorizando simplicidade e entrega rápida;
- O banco de dados é MySQL, com geração automática de schema via `ddl-auto=update` para fins didáticos.

---

## 4. ⚙️ Estratégia da Solução

A construção do projeto seguiu uma abordagem em camadas, do modelo ao frontend:

**Passo 1 — Modelagem das entidades**
Definição das entidades `Board`, `Column`, `Task`, `TaskHistory` e o enum `TaskEventType`, com mapeamentos JPA e relacionamentos bidirecionais.

**Passo 2 — Repositórios**
Interfaces `JpaRepository` para acesso CRUD. O `TaskHistoryRepository` recebeu query customizada para buscar histórico ordenado por data decrescente.

**Passo 3 — Camada de serviço**
`BoardService` centraliza toda a lógica de negócio: criar boards, adicionar colunas, mover tarefas, bloquear/desbloquear com registro de eventos. Uso de `@Transactional` para garantir atomicidade em operações que afetam múltiplas entidades.

**Passo 4 — Controllers**
`BoardController` e `TaskController` expõem as rotas para a UI Thymeleaf, pré-carregando histórico de cada task antes de renderizar a view (solução para `LazyInitializationException`).

**Passo 5 — Interface com Thymeleaf + Bootstrap**
Templates HTML dinâmicos com modais de bloqueio e histórico inline, sem necessidade de JavaScript pesado.

**Passo 6 — Operações assíncronas**
Implementação de `@Async` com `CompletableFuture` em `saveTaskAsync`, demonstrando como evitar bloqueio da UI em operações demoradas.

---

## 5. 💡 Insights Técnicos

Ao longo do desenvolvimento, alguns desafios revelaram aprendizados importantes:

- **`LazyInitializationException`** foi o erro mais recorrente. A causa: Thymeleaf tentava acessar coleções JPA lazy após o fechamento da transação. A solução foi pré-carregar o histórico no controller antes de passar o modelo para a view — uma decisão deliberada para manter a simplicidade sem usar `EAGER` em todos os relacionamentos.

- **Cascade e Orphan Removal** se mostraram essenciais para manter a consistência. Remover uma coluna sem `orphanRemoval = true` deixava tasks órfãs no banco — um bug silencioso que só aparece em produção.

- **`@Transactional` é inegociável** em operações como `moveTask`, que envolvem atualizar a task, registrar `MOVE` no histórico e possivelmente registrar `BLOCK` ou `UNBLOCK` em sequência. Sem a anotação, uma falha no meio deixa o banco em estado inconsistente.

- A **separação entre `BoardService` e `TaskService`** ficou mais clara ao longo do desenvolvimento. Começar com um único service e depois dividir mostrou na prática por que responsabilidade única importa.

---

## 6. 📊 Resultados

O sistema entregue permite:

| Funcionalidade | Status |
|---|---|
| Criar e gerenciar múltiplos boards | ✅ |
| Adicionar colunas personalizadas | ✅ |
| Criar, editar e mover tarefas entre colunas | ✅ |
| Bloquear/Desbloquear tarefas com motivo obrigatório | ✅ |
| Histórico completo de eventos por tarefa | ✅ |
| Interface responsiva com Bootstrap 5 | ✅ |
| Persistência em MySQL com Spring Data JPA | ✅ |
| Operações assíncronas com `@Async` | ✅ |

<img width="851" height="949" alt="Screenshot_20250812-183943" src="https://github.com/user-attachments/assets/adcdc537-0857-4a12-a706-a8f324d940c6" />

---

## 7. 🚀 Próximos Passos

O projeto tem base sólida para evoluir em direção a um produto real:

- [ ] **Drag & drop** no frontend para mover tasks visualmente via JavaScript/AJAX, sem recarregar a página;
- [ ] **API REST** desacoplada para servir um frontend em React ou Vue;
- [ ] **Autenticação com Spring Security** — hoje o campo `actor` do histórico é opcional; com segurança, ele passa a ser preenchido automaticamente pelo usuário logado;
- [ ] **Migrations com Flyway ou Liquibase** para controlar versionamento do schema em produção;
- [ ] **Testes automatizados** com JUnit + Mockito para os services e Spring Boot Test para integração;
- [ ] **Filtros e busca** por status, título e data na listagem de tasks.

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Função no projeto |
|---|---|
| Java 17+ | Linguagem principal |
| Spring Boot 3.x | Framework base |
| Spring Data JPA (Hibernate) | Persistência e ORM |
| Thymeleaf | Renderização de templates server-side |
| MySQL | Banco de dados relacional |
| Bootstrap 5 + Bootstrap Icons | UI responsiva |
| Maven | Gerenciamento de dependências |

---

## 🔧 Como Executar o Projeto

**Pré-requisitos:**
- Java 17+
- Maven 3.x
- MySQL 8+

**1. Clone o repositório**
```bash
git clone https://github.com/Santosdevbjj/boardTarefasJava.git
cd boardTarefasJava
```

**2. Crie o banco de dados**
```sql
CREATE DATABASE boarddb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**3. Configure o `application.properties`**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/boarddb?useSSL=false&serverTimezone=UTC
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.thymeleaf.cache=false
```

**4. Execute o projeto**
```bash
mvn clean spring-boot:run
```

**5. Acesse no navegador**
```
http://localhost:8080/boards
```

---

## 📚 Aprendizados

Este projeto foi minha imersão prática em Spring Boot com regras de negócio reais.

Os maiores aprendizados foram:

- **Entender `LazyInitializationException` de verdade** — não como um erro a ser contornado, mas como sinal de que a estratégia de carregamento de dados precisa ser consciente;
- **`@Transactional` não é opcional** em sistemas com auditoria — aprendi isso da forma difícil, debugando inconsistências no banco;
- **Cascade configuration importa** — uma decisão errada aqui cria bugs que só aparecem em cenários específicos de uso;
- **Separar responsabilidades desde o início** economiza horas de refatoração depois;
- Se fosse recomeçar, iniciaria com **testes unitários para os services** antes de subir os controllers — facilitaria muito a validação da lógica de negócio.

---

## 👤 Autor

**Sérgio Santos**



[![Portfólio](https://img.shields.io/badge/Portfólio-Sérgio_Santos-111827?style=for-the-badge&logo=githubpages&logoColor=00eaff)](https://portfoliosantossergio.vercel.app)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Sérgio_Santos-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/santossergioluiz)

---


