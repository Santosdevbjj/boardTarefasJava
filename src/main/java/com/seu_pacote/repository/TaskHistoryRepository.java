package com.seuprojeto.board.repository;

import com.seuprojeto.board.model.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    // Busca histórico por task ordenado por data (descendente ou ascendente)
    List<TaskHistory> findByTaskIdOrderByEventAtDesc(Long taskId);
}
