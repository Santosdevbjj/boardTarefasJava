package com.seuprojeto.board.service;

import com.seuprojeto.board.model.Board;
import com.seuprojeto.board.model.Column;
import com.seuprojeto.board.model.Task;
import com.seuprojeto.board.repository.BoardRepository;
import com.seuprojeto.board.repository.ColumnRepository;
import com.seuprojeto.board.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;



package com.seuprojeto.board.service;

import com.seuprojeto.board.model.*;
import com.seuprojeto.board.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;



/**
 * Bloqueia ou desbloqueia a tarefa e registra histórico.
 */
public void blockOrUnblockTask(Long taskId, String actionType, String reason) {
    Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

    if ("block".equalsIgnoreCase(actionType)) {
        task.setBlockReason(reason);
        task.setStatus("Bloqueada");
    } else if ("unblock".equalsIgnoreCase(actionType)) {
        task.setUnblockReason(reason);
        task.setBlockReason(null);
        task.setStatus("Desbloqueada");
    } else {
        throw new IllegalArgumentException("Ação inválida: " + actionType);
    }

    task.setUpdatedAt(java.time.LocalDateTime.now());
    taskRepository.save(task);

    // Registra histórico
    History history = new History();
    history.setTask(task);
    history.setAction(actionType.equals("block") ? "Bloqueada" : "Desbloqueada");
    history.setReason(reason);
    history.setActionDate(java.time.LocalDateTime.now());
    historyRepository.save(history);
}




/**
 * Serviço que gerencia a lógica de negócios relacionada aos Boards, Columns e Tasks.
 * Agora com suporte a histórico (TaskHistory), bloqueio e desbloqueio.
 */
@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    // --- Métodos já existentes (createBoard, getAllBoards, etc.) mantidos ---

    /**
     * Move uma tarefa de uma coluna para outra, atualizando status e criando um registro de histórico.
     */
    @Transactional
    public Task moveTask(Long taskId, Long targetColumnId, String blockReason, String unblockReason, String actor) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada: " + taskId));
        Column target = columnRepository.findById(targetColumnId)
                .orElseThrow(() -> new RuntimeException("Coluna destino não encontrada: " + targetColumnId));

        Column from = task.getColumn();
        String fromName = from != null ? from.getName() : null;
        String toName = target.getName();

        // remove da coluna atual (se houver) e adiciona na coluna destino
        if (from != null) {
            from.removeTask(task);
            columnRepository.save(from);
        }
        target.addTask(task);
        columnRepository.save(target);

        // atualiza status para o nome da coluna destino (padrão)
        task.setStatus(toName);

        // aplicar bloqueio / desbloqueio se enviados
        if (blockReason != null && !blockReason.isBlank()) {
            task.setBlocked(true);
            task.setBlockReason(blockReason);
            // cria registro de BLOCK
            TaskHistory hBlock = new TaskHistory(task, TaskEventType.BLOCK, blockReason, fromName, toName, actor);
            taskHistoryRepository.save(hBlock);
        }
        if (unblockReason != null && !unblockReason.isBlank()) {
            task.setBlocked(false);
            task.setUnblockReason(unblockReason);
            TaskHistory hUnblock = new TaskHistory(task, TaskEventType.UNBLOCK, unblockReason, fromName, toName, actor);
            taskHistoryRepository.save(hUnblock);
        }

        // registra o MOVE
        TaskHistory hMove = new TaskHistory(task, TaskEventType.MOVE, null, fromName, toName, actor);
        taskHistoryRepository.save(hMove);

        task.setUpdatedAt(java.time.LocalDateTime.now());
        taskRepository.save(task);

        return task;
    }

    /**
     * Bloqueia uma task e cria registro de histórico.
     */
    @Transactional
    public Task blockTask(Long taskId, String reason, String actor) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada: " + taskId));
        if (!task.isBlocked()) {
            task.setBlocked(true);
            task.setBlockReason(reason);
            task.setUpdatedAt(java.time.LocalDateTime.now());
            taskRepository.save(task);
        }
        TaskHistory h = new TaskHistory(task, TaskEventType.BLOCK, reason, task.getColumn() != null ? task.getColumn().getName() : null, null, actor);
        taskHistoryRepository.save(h);
        return task;
    }

    /**
     * Desbloqueia uma task e cria registro de histórico.
     */
    @Transactional
    public Task unblockTask(Long taskId, String reason, String actor) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task não encontrada: " + taskId));
        if (task.isBlocked()) {
            task.setBlocked(false);
            task.setUnblockReason(reason);
            task.setUpdatedAt(java.time.LocalDateTime.now());
            taskRepository.save(task);
        }
        TaskHistory h = new TaskHistory(task, TaskEventType.UNBLOCK, reason, task.getColumn() != null ? task.getColumn().getName() : null, null, actor);
        taskHistoryRepository.save(h);
        return task;
    }

    /**
     * Lista o histórico da task (mais recente primeiro).
     */
    public List<TaskHistory> getTaskHistory(Long taskId) {
        return taskHistoryRepository.findByTaskIdOrderByEventAtDesc(taskId);
    }

    /**
     * Salva uma tarefa assincronamente para não bloquear a UI.
     */
    @Async
    public CompletableFuture<Task> saveTaskAsync(Task task) {
        return CompletableFuture.completedFuture(taskRepository.save(task));
    }

    // outros métodos...
}




/**
 * Serviço que gerencia a lógica de negócios relacionada aos Boards, Columns e Tasks.
 */
@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * Cria um novo Board no banco de dados.
     * @param board Board a ser criado.
     * @return Board criado.
     */
    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }

    /**
     * Retorna todos os Boards.
     * @return Lista de Boards.
     */
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    /**
     * Encontra um Board pelo ID.
     * @param id ID do Board.
     * @return Optional com o Board encontrado ou vazio.
     */
    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    /**
     * Exclui um Board pelo ID.
     * @param id ID do Board.
     */
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    /**
     * Adiciona uma coluna a um Board existente.
     * @param boardId ID do Board.
     * @param column Coluna a ser adicionada.
     * @return Coluna salva.
     */
    public Column addColumnToBoard(Long boardId, Column column) {
        Optional<Board> boardOpt = boardRepository.findById(boardId);
        if (boardOpt.isPresent()) {
            Board board = boardOpt.get();
            board.addColumn(column);
            boardRepository.save(board); // salva o board com a nova coluna
            return column;
        } else {
            throw new RuntimeException("Board não encontrado com ID: " + boardId);
        }
    }

    /**
     * Cria uma nova tarefa em uma coluna.
     * @param columnId ID da coluna.
     * @param task Tarefa a ser criada.
     * @return Tarefa salva.
     */
    public Task addTaskToColumn(Long columnId, Task task) {
        Optional<Column> colOpt = columnRepository.findById(columnId);
        if (colOpt.isPresent()) {
            Column column = colOpt.get();
            column.addTask(task);
            columnRepository.save(column); // salva a coluna com a nova tarefa
            return task;
        } else {
            throw new RuntimeException("Coluna não encontrada com ID: " + columnId);
        }
    }

    /**
     * Move uma tarefa de uma coluna para outra.
     * @param taskId ID da tarefa.
     * @param targetColumnId ID da coluna destino.
     * @return Tarefa atualizada.
     */
   




    
    /**
   // public Task moveTask(Long taskId, Long targetColumnId) {
   // Optional<Task> taskOpt = taskRepository.findById(taskId);
      //  Optional<Column> targetColOpt = columnRepository.findById(targetColumnId);

        //.if (taskOpt.isPresent() && targetColOpt.isPresent()) {
//  Task task = taskOpt.get();
            Column currentColumn = task.getColumn();
            Column targetColumn = targetColOpt.get();

            if (currentColumn != null) {
                currentColumn.removeTask(task);
                columnRepository.save(currentColumn);
            }
            targetColumn.addTask(task);
            columnRepository.save(targetColumn);

            task.setStatus("Movido");
            taskRepository.save(task);

            return task;
        } else {
            throw new RuntimeException("Task ou coluna destino não encontrado");
        }
    }

*/


    /**
 * Move uma tarefa de uma coluna para outra, atualizando status e datas.
 *
 * @param taskId ID da tarefa a ser movida.
 * @param targetColumnId ID da coluna destino.
 * @param blockReason razão do bloqueio, caso a tarefa esteja sendo bloqueada.
 * @param unblockReason razão do desbloqueio, caso a tarefa esteja sendo desbloqueada.
 * @return Task atualizada.
 */
public Task moveTask(Long taskId, Long targetColumnId, String blockReason, String unblockReason) {
    Optional<Task> taskOpt = taskRepository.findById(taskId);
    Optional<Column> targetColOpt = columnRepository.findById(targetColumnId);

    if (taskOpt.isEmpty() || targetColOpt.isEmpty()) {
        throw new RuntimeException("Task ou coluna destino não encontrado");
    }

    Task task = taskOpt.get();
    Column currentColumn = task.getColumn();
    Column targetColumn = targetColOpt.get();

    if (currentColumn != null) {
        currentColumn.removeTask(task);
        columnRepository.save(currentColumn);
    }

    targetColumn.addTask(task);
    columnRepository.save(targetColumn);

    // Atualiza status baseado no nome da coluna destino (exemplo)
    task.setStatus(targetColumn.getName());

    // Atualiza razão de bloqueio/desbloqueio, se houver
    if (blockReason != null && !blockReason.isEmpty()) {
        task.setBlockReason(blockReason);
    }
    if (unblockReason != null && !unblockReason.isEmpty()) {
        task.setUnblockReason(unblockReason);
    }

    // Atualiza data da última modificação
    task.setUpdatedAt(java.time.LocalDateTime.now());

    taskRepository.save(task);

    return task;
}

    
    /**
     * Salva uma tarefa assincronamente para não bloquear a UI.
     * @param task Tarefa a ser salva.
     * @return CompletableFuture da tarefa salva.
     */
    @Async
    public CompletableFuture<Task> saveTaskAsync(Task task) {
        return CompletableFuture.completedFuture(taskRepository.save(task));
    }

    // Adicione outros métodos para bloquear, desbloquear, cancelar, etc.
} 



/**
 * Busca uma tarefa pelo seu ID.
 * @param id ID da tarefa.
 * @return Optional<Task>
 */
public Optional<Task> getTaskById(Long id) {
    return taskRepository.findById(id);
}




