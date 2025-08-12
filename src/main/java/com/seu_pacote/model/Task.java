package com.seuprojeto.board.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma tarefa (card) dentro de uma coluna.
 * Agora com suporte a bloqueio (blocked) e histórico de eventos.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;

    // Razão do bloqueio (último bloqueio)
    private String blockReason;

    // Razão do desbloqueio (último desbloqueio)
    private String unblockReason;

    // Flag indicando se a task está bloqueada
    private boolean blocked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id")
    private Column column;

    // Histórico de eventos relacionados a esta task
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskHistory> history = new ArrayList<>();

    public Task() {
        this.createdAt = LocalDateTime.now();
    }

    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters e setters...
    // Exemplos:
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; setUpdatedAt(LocalDateTime.now()); }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; setUpdatedAt(LocalDateTime.now()); }
    public List<TaskHistory> getHistory() { return history; }
    public void addHistory(TaskHistory h) {
        history.add(h);
        h.setTask(this);
    }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    // ... complete os getters e setters no seu IDE
}
