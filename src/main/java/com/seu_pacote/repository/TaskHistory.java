package com.seuprojeto.board.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que registra eventos relacionados a uma Task.
 * Cada registro descreve o que aconteceu (ex: BLOCK, MOVE), quando, quem (opcional)
 * e razão (se aplicável). Isso serve como histórico / audit log da tarefa.
 */
@Entity
@Table(name = "task_history")
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com a Task (muitos históricos para uma tarefa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    // Tipo do evento (BLOCK, UNBLOCK, MOVE, ...)
    @Enumerated(EnumType.STRING)
    private TaskEventType eventType;

    // Timestamp do evento
    private LocalDateTime eventAt;

    // Motivo/operação (ex: motivo do bloqueio, motivo do desbloqueio)
    private String reason;

    // Coluna origem (opcional, útil para MOVE)
    private String fromColumnName;

    // Coluna destino (opcional, útil para MOVE)
    private String toColumnName;

    // Quem realizou a operação (opcional)
    private String actor;

    public TaskHistory() {}

    public TaskHistory(Task task, TaskEventType eventType, String reason,
                       String fromColumnName, String toColumnName, String actor) {
        this.task = task;
        this.eventType = eventType;
        this.reason = reason;
        this.fromColumnName = fromColumnName;
        this.toColumnName = toColumnName;
        this.actor = actor;
        this.eventAt = LocalDateTime.now();
    }

    // Getters e setters (omitidos aqui por brevidade - gere no IDE)
    // ...
}
