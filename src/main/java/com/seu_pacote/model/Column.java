package com.seuprojeto.board.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma coluna dentro de um Board.
 * Uma coluna possui um nome e uma lista de cards (tasks).
 */
@Entity
@Table(name = "columns")
public class Column {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome da coluna (ex: Inicial, Pendente, Final, Cancelado)
    @Column(nullable = false)
    private String name;

    // Relacionamento com o Board
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // Lista de tasks/carts que pertencem à essa coluna
    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    public Column() {}

    public Column(String name) {
        this.name = name;
    }

    // Getters e setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adiciona uma tarefa à coluna.
     * @param task tarefa a ser adicionada
     */
    public void addTask(Task task) {
        tasks.add(task);
        task.setColumn(this);
    }

    /**
     * Remove uma tarefa da coluna.
     * @param task tarefa a ser removida
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        task.setColumn(null);
    }
} 
