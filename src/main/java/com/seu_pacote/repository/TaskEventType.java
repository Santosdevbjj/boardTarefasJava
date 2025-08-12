package com.seuprojeto.board.model;

/**
 * Enum que define os tipos de evento que podemos registrar no histórico de uma Task.
 */
public enum TaskEventType {
    CREATE,
    UPDATE,
    MOVE,
    BLOCK,
    UNBLOCK,
    CANCEL
}
