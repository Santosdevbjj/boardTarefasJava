package com.seuprojeto.board.repository;

import com.seuprojeto.board.model.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface de repositório para acesso aos dados da entidade Column.
 */
@Repository
public interface ColumnRepository extends JpaRepository<Column, Long> {
}
