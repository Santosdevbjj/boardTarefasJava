package com.example.board.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    private String action; // Bloqueada ou Desbloqueada
    private String reason;
    private LocalDateTime actionDate;

    // Getters e Setters
}
