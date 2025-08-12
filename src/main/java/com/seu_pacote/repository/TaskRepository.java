package com.seuprojeto.board.repository;

import com.seuprojeto.board.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface de repositório para acesso aos dados da entidade Task.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
