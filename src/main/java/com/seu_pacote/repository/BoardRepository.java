package com.seuprojeto.board.repository;

import com.seuprojeto.board.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface de repositório para acesso aos dados da entidade Board.
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    // Você pode adicionar consultas customizadas aqui, se necessário
}
