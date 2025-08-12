package com.seuprojeto.board.controller;

import com.seuprojeto.board.model.Board;
import com.seuprojeto.board.model.Column;
import com.seuprojeto.board.model.Task;
import com.seuprojeto.board.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;



// imports adicionais necessários
import com.seuprojeto.board.model.TaskHistory;

import java.util.List;

/**
 * Exibe a página de histórico de uma task.
 */
@GetMapping("/tasks/{taskId}/history")
public String viewTaskHistory(@PathVariable("taskId") Long taskId, Model model) {
    List<TaskHistory> history = boardService.getTaskHistory(taskId);
    model.addAttribute("history", history);
    model.addAttribute("taskId", taskId);
    return "task_history";
}

/**
 * Bloqueia a task com um motivo informado.
 */
@PostMapping("/tasks/{taskId}/block")
public String blockTask(@PathVariable("taskId") Long taskId,
                        @RequestParam("reason") String reason,
                        @RequestParam(value = "actor", required = false) String actor) {
    boardService.blockTask(taskId, reason, actor);
    Task task = boardService.getTaskById(taskId).orElseThrow();
    Long boardId = task.getColumn().getBoard().getId();
    return "redirect:/boards/" + boardId;
}

/**
 * Desbloqueia a task com um motivo informado.
 */
@PostMapping("/tasks/{taskId}/unblock")
public String unblockTask(@PathVariable("taskId") Long taskId,
                          @RequestParam("reason") String reason,
                          @RequestParam(value = "actor", required = false) String actor) {
    boardService.unblockTask(taskId, reason, actor);
    Task task = boardService.getTaskById(taskId).orElseThrow();
    Long boardId = task.getColumn().getBoard().getId();
    return "redirect:/boards/" + boardId;
}




/**
 * Controller para gerenciar as rotas e interações relacionadas a Boards.
 */
@Controller
@RequestMapping("/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    /**
     * Lista todos os boards cadastrados.
     */
    @GetMapping
    public String listBoards(Model model) {
        model.addAttribute("boards", boardService.getAllBoards());
        return "board_list"; // nome do template Thymeleaf
    }

    /**
     * Exibe o formulário para criação de um novo Board.
     */
    @GetMapping("/new")
    public String showCreateForm(Board board) {
        return "board_form";
    }

    /**
     * Processa a criação de um novo Board.
     */
    @PostMapping
    public String createBoard(@Valid Board board, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "board_form";
        }
        boardService.createBoard(board);

        // Após criar, redireciona para a lista de boards
        return "redirect:/boards";
    }

    /**
     * Exibe detalhes de um board específico, incluindo suas colunas e tarefas.
     */
    @GetMapping("/{id}")
    public String getBoardDetail(@PathVariable("id") Long id, Model model) {
        Optional<Board> boardOpt = boardService.getBoardById(id);
        if (boardOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Board não encontrado");
            return "error";
        }
        model.addAttribute("board", boardOpt.get());
        return "board_detail";
    }

    /**
     * Exclui um board pelo id.
     */
    @GetMapping("/delete/{id}")
    public String deleteBoard(@PathVariable("id") Long id) {
        boardService.deleteBoard(id);
        return "redirect:/boards";
    }

    /**
     * Exibe formulário para adicionar coluna ao Board.
     */
    @GetMapping("/{boardId}/columns/new")
    public String showCreateColumnForm(@PathVariable("boardId") Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        model.addAttribute("column", new Column());
        return "column_form";
    }

    /**
     * Processa a criação da coluna no board.
     */
    @PostMapping("/{boardId}/columns")
    public String createColumn(@PathVariable("boardId") Long boardId, @Valid Column column, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("boardId", boardId);
            return "column_form";
        }
        boardService.addColumnToBoard(boardId, column);
        return "redirect:/boards/" + boardId;
    }

    /**
     * Exibe formulário para adicionar uma nova tarefa em uma coluna.
     */
    @GetMapping("/columns/{columnId}/tasks/new")
    public String showCreateTaskForm(@PathVariable("columnId") Long columnId, Model model) {
        model.addAttribute("columnId", columnId);
        model.addAttribute("task", new Task());
        return "task_form";
    }

    /**
     * Processa a criação da tarefa em uma coluna.
     */
    @PostMapping("/columns/{columnId}/tasks")
    public String createTask(@PathVariable("columnId") Long columnId, @Valid Task task, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("columnId", columnId);
            return "task_form";
        }
        boardService.addTaskToColumn(columnId, task);

        // Obter o boardId para redirecionar
        Long boardId = task.getColumn().getBoard().getId();
        return "redirect:/boards/" + boardId;
    }

    // Futuramente, métodos para mover tarefas, bloquear, desbloquear, etc.
} 



/**
 * Exibe formulário para mover tarefa para outra coluna.
 */
@GetMapping("/tasks/{taskId}/move")
public String showMoveTaskForm(@PathVariable("taskId") Long taskId, Model model) {
    Optional<Task> taskOpt = boardService.getTaskById(taskId);
    if (taskOpt.isEmpty()) {
        model.addAttribute("errorMessage", "Tarefa não encontrada");
        return "error";
    }
    Task task = taskOpt.get();
    Board board = task.getColumn().getBoard();

    model.addAttribute("task", task);
    model.addAttribute("columns", board.getColumns());
    return "task_move_form";
}

/**
 * Processa a movimentação da tarefa.
 */
@PostMapping("/tasks/{taskId}/move")
public String moveTask(
        @PathVariable("taskId") Long taskId,
        @RequestParam("targetColumnId") Long targetColumnId,
        @RequestParam(value = "blockReason", required = false) String blockReason,
        @RequestParam(value = "unblockReason", required = false) String unblockReason) {

    boardService.moveTask(taskId, targetColumnId, blockReason, unblockReason);

    // Redireciona para a página do board da tarefa
    Task task = boardService.getTaskById(taskId).orElseThrow();
    Long boardId = task.getColumn().getBoard().getId();

    return "redirect:/boards/" + boardId;
}

    
