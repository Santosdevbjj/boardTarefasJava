## Criando seu Board de Tarefas com Java.

![TonnieJava0002](https://github.com/user-attachments/assets/3c8db5cd-403c-436c-a8a6-8a0dc04d429e)


**Bootcamp TONNIE - Java and AI in Europe**


**DESCRIÇÃO:**
Aprenda a criar um board de tarefas em Java, passando por todas as etapas do desenvolvimento, desde o planejamento e estruturação até a implementação de funcionalidades como gerenciamento de dados e integração entre camadas, seguindo boas práticas de programação.



**Board de Tarefas — Projeto Java (Spring Boot)**

> **Visão geral**

Este projeto implementa um Board de Tarefas (quadro/kanban) em Java usando Spring Boot, Spring Data JPA (Hibernate) e Thymeleaf como engine de templates.

O objetivo é ensinar passo a passo como criar entidades, repositórios, serviços, controllers, UI (templates Thymeleaf) e recursos como bloqueio/desbloqueio de tarefas com histórico, movimentação entre colunas e persistência em MySQL.




---

**Índice**

## Descrição rápida

## Tecnologias

## Estrutura de pastas

## Entidades (Model) — explicação detalhada

## Repositórios

## Serviços (Service) — métodos e responsabilidades

## Controllers — rotas e o que fazem

## Templates Thymeleaf (.html) — cada arquivo e propósito

## Configuração (application.properties / MySQL)

## Como rodar o projeto (passo a passo)

## Pontos importantes / erros comuns e soluções

## Melhorias futuras sugeridas

## Licença / Autor



---

**Descrição rápida**

**O sistema permite:**

- Criar múltiplos Boards (quadros) para diferentes projetos;

- Adicionar colunas (ex.: Inicial, Pendente, Final, Cancelado) em cada board;

- Criar tasks (cards) dentro de colunas;

- Mover tasks entre colunas (com registro de evento MOVE no histórico);

- Bloquear / Desbloquear tasks com registro de motivo e data no histórico (BLOCK / UNBLOCK);

Visualizar o histórico de eventos de cada task (criação, movimento, bloqueio, desbloqueio, cancelamento);

**Operações de persistência em MySQL via Spring Data JPA.**


O projeto também exemplifica uso de operações assíncronas com @Async (por ex.: salvar tarefas sem travar a UI) e tratamento para evitar LazyInitializationException pré-carregando histórico quando necessário.


---

**Tecnologias**

- Java 17+

- Spring Boot 3.x

- Spring Data JPA (Hibernate)

- Thymeleaf

- MySQL (driver)

- Bootstrap 5 (para UI)

- Bootstrap Icons

- Maven



---

**Estrutura de pastas**


<img width="1080" height="1689" alt="Screenshot_20250812-182610" src="https://github.com/user-attachments/assets/543e6fe9-3d62-4b0c-86da-65e9eda2fe12" />

 
      └── java/...                         # testes unitários

> Observação: board_view.html é a versão final integrada com modais de bloqueio e histórico em linha. Alguns arquivos como board_detail.html existem como versões anteriores/alternativas — mantidos para referência.




---

**Entidades (models) — explicação detalhada**

> **Pacote: com.seuprojeto.board.model**



**Board (Board.java)**

Campos principais:

Long id — PK

String name — nome do board

List<Column> columns — colunas associadas ao board


Relacionamentos: @OneToMany para Column (mappedBy = "board", cascade = ALL).

**Métodos relevantes:**

addColumn(Column column) — adiciona coluna e seta column.setBoard(this);

removeColumn(Column column) — remove coluna e limpa referência.


Responsabilidade: representar o quadro e gerenciar sua coleção de colunas.



---

**Column (Column.java)**

Campos:

Long id

String name — ex.: Inicial, Pendente, Final

Board board — @ManyToOne

List<Task> tasks — @OneToMany com cascade


**Métodos:**

addTask(Task task), removeTask(Task task) — cuidam das referências bidirecionais.


Responsabilidade: representar uma coluna do board e agrupar tasks.



---

**Task (Task.java)**

Campos principais:

Long id

String title

String description

LocalDateTime createdAt

LocalDateTime updatedAt

String status — por ex.: "Iniciado", "Em conclusão", "Concluído", "Cancelado", ou o nome da coluna

boolean blocked — flag indicando bloqueio

String blockReason, String unblockReason

Column column — @ManyToOne

List<TaskHistory> history — @OneToMany contendo eventos (BLOCK, UNBLOCK, MOVE...)


**Métodos relevantes:**

Getters/Setters com atualização de updatedAt sempre que o conteúdo relevante muda.

addHistory(TaskHistory h) — adiciona um registro histórico e seta referência.


Notas: createdAt inicializado no construtor; updatedAt atualizado nas alterações.



---

**TaskHistory (TaskHistory.java)**

Campos:

Long id

Task task — @ManyToOne (muitos históricos para uma task)

TaskEventType eventType — enum (CREATE, MOVE, BLOCK, UNBLOCK, CANCEL, UPDATE)

LocalDateTime eventAt — timestamp do evento

String reason — texto com motivo (quando aplicável)

String fromColumnName, String toColumnName — usados para MOVE

String actor — opcional, usuário que fez a ação


Responsabilidade: auditar tudo que acontece com a task (audit log).



---

**TaskEventType (enum)**

Valores: CREATE, UPDATE, MOVE, BLOCK, UNBLOCK, CANCEL.


---

**Repositórios (repository)**

> **Pacote: com.seuprojeto.board.repository**



BoardRepository extends JpaRepository<Board, Long>

ColumnRepository extends JpaRepository<Column, Long>

TaskRepository extends JpaRepository<Task, Long>

TaskHistoryRepository extends JpaRepository<TaskHistory, Long>

método custom: List<TaskHistory> findByTaskIdOrderByEventAtDesc(Long taskId)



Essas interfaces já dão acesso a métodos CRUD prontos (save, findById, findAll, deleteById, etc.).


---

**Serviços (service) — responsabilidades e métodos**

> **Pacote: com.seuprojeto.board.service**



**BoardService (exemplos de métodos implementados)**

Board createBoard(Board board) — salva um novo board.

List<Board> getAllBoards() — lista todos os boards.

Optional<Board> getBoardById(Long id) — busca board por id.

void deleteBoard(Long id) — exclui board.

Column addColumnToBoard(Long boardId, Column column) — adiciona coluna e persiste.

Task addTaskToColumn(Long columnId, Task task) — adiciona task em coluna.

Task moveTask(Long taskId, Long targetColumnId, String blockReason, String unblockReason, String actor) — move task entre colunas, registra TaskHistory com MOVE e, se aplicável, com BLOCK/UNBLOCK.

Task blockTask(Long taskId, String reason, String actor) — marca task como bloqueada e cria TaskHistory com BLOCK.

Task unblockTask(Long taskId, String reason, String actor) — marca task como desbloqueada e cria TaskHistory com UNBLOCK.

List<TaskHistory> getTaskHistory(Long taskId) — retorna histórico ordenado.

CompletableFuture<Task> saveTaskAsync(Task task) — exemplo de operação assíncrona com @Async.


> **Observação:** Há uso de @Transactional em operações que envolvem múltiplas alterações (ex.: moveTask) para garantir atomicidade.



**TaskService**

List<TaskHistory> getHistoryByTaskId(Long taskId) — delega ao TaskHistoryRepository para busca ordenada do histórico.



---

**Controllers — rotas e o que fazem**

> Pacote: com.seuprojeto.board.controller



**BoardController (rotas principais)**

GET /boards — lista boards (board_list.html).

**Método:** listBoards(Model model)


GET /boards/new — exibe formulário para criar board (board_form.html).

**Método:** showCreateForm(Board board)


POST /boards — cria board (processa submit de board_form).

**Método:** createBoard(...)


GET /boards/{id} — view detalhada do board (usar board_view.html — carrega colunas, tasks e pré-carrega histórico em memória para evitar LazyInitializationException).

Método: viewBoard(@PathVariable Long id, Model model)


GET /boards/delete/{id} — exclui um board e redireciona.

**Método:** deleteBoard(Long id)


GET /boards/{boardId}/columns/new & POST /boards/{boardId}/columns — criar colunas.

Métodos: showCreateColumnForm, createColumn


GET /boards/columns/{columnId}/tasks/new & POST /boards/columns/{columnId}/tasks — criar tasks em coluna.

**Métodos:** showCreateTaskForm, createTask


GET /boards/tasks/{taskId}/move & POST /boards/tasks/{taskId}/move — form + processamento para mover task entre colunas (pode receber blockReason e unblockReason).

POST /tasks/{id}/block — bloqueia task (usado pelo modal); cria histórico BLOCK.

GET /tasks/{id}/unblock or POST /tasks/{id}/unblock — desbloqueia task e grava UNBLOCK.

GET /boards/tasks/{taskId}/history — exibe página de histórico (alternativa de modal): task_history.html.


> **Observação:** Em versões finais algumas rotas foram levemente renomeadas (ex.: /boards/{id} ficou a rota central que retorna board_view.html). Ajuste conforme seu BoardController final.




---

**Templates Thymeleaf (.html) — descrição detalhada**

> Local: src/main/resources/templates/



**board_list.html**

Lista todos os boards com botões: Ver, Criar Novo, Excluir.

Contém tabela com boards (modelo enviado pelo controller).


**board_form.html**

Formulário simples com input para name do board.


**board_view.html (versão final)**

Página principal que exibe o board selecionado com suas colunas (em colunas Bootstrap) e cards (tasks) dentro de cada coluna.

Cada task tem botões: Bloquear (abre modal), Desbloquear (ação direta), Movimentar (abre formulário), Histórico (abre modal com lista de eventos).

**Possui os modais (um por task):**

Modal de Bloqueio — formulário para informar motivo (POST /tasks/{id}/block).

Modal de Histórico — tabela com os itens de TaskHistory.


Inclui Bootstrap CSS/JS e Bootstrap Icons via CDN.


column_form.html

Formulário para adicionar nova coluna ao board.


task_form.html

Formulário para criar nova tarefa (title, description, status).


task_move_form.html

Formulário para mover uma task para outra coluna (exibe select com as colunas do board).


task_history.html (view alternativa)

Página que mostra todo o histórico de uma task (se preferir abrir em página em vez de modal).



---

Configuração (application.properties)

**Exemplo mínimo (MySQL):**

spring.datasource.url=jdbc:mysql://localhost:3306/boarddb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=sua_senha
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.thymeleaf.cache=false

**Criar database:**

CREATE DATABASE boarddb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

> Recomendo usar profiles (ex.: application-dev.properties) em projetos maiores.




---

**Como rodar o projeto (passo a passo)**

1. Clonar o repositório (quando existir no GitHub):



git clone https://github.com/seu-usuario/board-tarefas.git
cd board-tarefas

2. Configurar o MySQL e atualizar application.properties com credenciais.


3. Criar o schema:



CREATE DATABASE boarddb;

4. Rodar via Maven:



mvn clean spring-boot:run

5. Acessar no navegador:



http://localhost:8080/boards

**6. Empacotar (opcional):**



mvn clean package
java -jar target/board-tarefas-0.0.1-SNAPSHOT.jar


---

**Pontos importantes / erros comuns e soluções**

LazyInitializationException: ocorre quando Thymeleaf tenta acessar coleções lazy após sessão/transaction fechada. Solução aplicada: pré-carregar o histórico no controller chamando um serviço (TaskService.getHistoryByTaskId(...)) e setando a lista em cada task antes de retornar a view.

Cascade / Orphan removal: usamos cascade = CascadeType.ALL e orphanRemoval = true em coleções para manter consistência entre entidades (ex.: remover coluna remove suas tasks).

Transações: operações que tocam múltiplas entidades (mover task entre colunas) devem estar anotadas com @Transactional para garantir atomicidade.

Async: métodos anotados com @Async retornam CompletableFuture e precisam de @EnableAsync na aplicação principal. Útil para operações demoradas (salvamentos, integrações externas).



---

**Testes e validações (sugestões)**

Unit tests com JUnit + Mockito para BoardService e TaskService.

Testes de integração com Spring Boot Test e um banco in-memory (H2) ou MySQL test container.

Validações: usar @Valid e @NotNull, @Size nas entidades / DTOs para validar inputs em formulários.



---

**Melhorias futuras sugeridas**

Implementar drag & drop no frontend (JS + fetch/AJAX) para mover tasks visualmente sem recarregar a página.

Criar API REST separada para o frontend (React/Vue) e uma UI dedicada.

Adicionar autenticação/autorização (Spring Security) para registrar actor no histórico.

Adicionar migrations com Flyway ou Liquibase.

Melhorar UI/UX: editar task via modal, filtros, busca, labels, prioridades e prazo.



---

**Observações finais**

Este README descreve a implementação passo a passo e a arquitetura do projeto que desenvolvemos. Se preferir, posso:

Gerar automaticamente o arquivo README.md (já disponível ao lado),

Gerar um script SQL de migração para as tabelas,

Criar exemplos de testes unitários,

Implementar endpoints REST para uso via AJAX/SPA,

Ou enviar um git diff com todas as alterações para commit.




---


**Projeto: Board de Tarefas em Java com Spring Boot e Thymeleaf**

**Descrição**

Este projeto implementa um Board de Tarefas no estilo Kanban, com gerenciamento de boards, colunas e cards (tarefas). Permite criar múltiplos boards, adicionar tarefas, mover, bloquear/desbloquear e consultar histórico de ações.


<img width="851" height="949" alt="Screenshot_20250812-183943" src="https://github.com/user-attachments/assets/adcdc537-0857-4a12-a706-a8f324d940c6" />



**Funcionalidades**

Criar e gerenciar múltiplos boards.

Adicionar colunas personalizadas.

Criar, mover, bloquear e desbloquear tarefas.

Registrar motivo de bloqueio/desbloqueio.

Histórico de ações de cada tarefa.


**Classes Principais**

**BoardController:** Controla rotas para criação e visualização de boards.

**TaskController:** Controla ações sobre tarefas.

**BoardService / TaskService: Lógica de negócio.**

Board / Column / Task / TaskHistory: Modelos JPA.

Repositories: Interfaces para persistência.


**Templates HTML**

board_list.html: Lista de boards.

board_view.html: Exibe colunas e tarefas.

modal_bloqueio.html: Formulário modal para bloqueio de tarefa.

modal_historico.html: Lista de histórico de uma tarefa.


**SQL de Criação de Tabelas**

CREATE TABLE boards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE columns (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    board_id BIGINT,
    FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE
);

CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50),
    column_id BIGINT,
    FOREIGN KEY (column_id) REFERENCES columns(id) ON DELETE CASCADE
);

CREATE TABLE task_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id BIGINT,
    action_type VARCHAR(50),
    action_reason TEXT,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

**Exemplos de Commits Git**

### Inicialização do projeto Spring Boot
git commit -m "Inicializa projeto Spring Boot com dependências básicas"

### Implementa modelos JPA
git commit -m "Adiciona entidades Board, Column, Task e TaskHistory com mapeamentos JPA"

### Cria serviços e controladores
git commit -m "Implementa BoardService, TaskService e controladores com rotas básicas"

### Adiciona interface HTML com Thymeleaf
git commit -m "Cria templates Thymeleaf para lista e visualização de boards"

### Implementa funcionalidade de bloqueio/desbloqueio com histórico
git commit -m "Adiciona modal de bloqueio e registro no histórico da tarefa"

### Refatora código e adiciona SQL de criação de tabelas no README
git commit -m "Refatora controllers e atualiza documentação com SQL detalhado"












---

**Licença / Autor**

Projeto criado como material didático e prático para aprendizado.

**Autor:** Sérgio Santos



---

**Contato:**


[![Portfólio Sérgio Santos](https://img.shields.io/badge/Portfólio-Sérgio_Santos-111827?style=for-the-badge&logo=githubpages&logoColor=00eaff)](https://santosdevbjj.github.io/portfolio/)
[![LinkedIn Sérgio Santos](https://img.shields.io/badge/LinkedIn-Sérgio_Santos-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/santossergioluiz) 

---





