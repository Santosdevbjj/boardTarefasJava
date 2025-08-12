package com.seuprojeto.board.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma tarefa (card) dentro de uma coluna.
 * Contém título, descrição, data, status, e razão do bloqueio/desbloqueio.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título da tarefa
    @Column(nullable = false)
    private String title;

    // Descrição da tarefa
    @Column(length = 1000)
    private String description;

    // Data de criação da tarefa
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Data da última atualização da tarefa
    private LocalDateTime updatedAt;

    // Status da tarefa (ex: iniciado, em conclusão, concluído, cancelado)
    @Column(nullable = false)
    private String status;

    // Razão do bloqueio da tarefa, se houver
    private String blockReason;

    // Razão do desbloqueio da tarefa, se houver
    private String unblockReason;

    // Relacionamento com a coluna que contém essa tarefa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id")
    private Column column;

    public Task() {}

    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters e setters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setUpdatedAt(LocalDateTime.now());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(LocalDateTime.now());
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        setUpdatedAt(LocalDateTime.now());
    }

    public String getBlockReason() {
        return blockReason;
    }

    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
        setUpdatedAt(LocalDateTime.now());
    }

    public String getUnblockReason() {
        return unblockReason;
    }

    public void setUnblockReason(String unblockReason) {
        this.unblockReason = unblockReason;
        setUpdatedAt(LocalDateTime.now());
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }
}
