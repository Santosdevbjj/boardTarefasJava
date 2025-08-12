package com.seuprojeto.board.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um quadro (Board) de tarefas.
 * Um Board possui um nome e uma lista de colunas.
 */
@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome do board
    @Column(nullable = false, unique = true)
    private String name;

    // Lista de colunas pertencentes a este board
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Column> columns = new ArrayList<>();

    public Board() {}

    public Board(String name) {
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

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    /**
     * Método auxiliar para adicionar uma coluna ao board.
     * @param column Coluna a ser adicionada.
     */
    public void addColumn(Column column) {
        columns.add(column);
        column.setBoard(this);
    }

    /**
     * Método auxiliar para remover uma coluna do board.
     * @param column Coluna a ser removida.
     */
    public void removeColumn(Column column) {
        columns.remove(column);
        column.setBoard(null);
    }
} 
