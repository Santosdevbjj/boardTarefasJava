package com.seuprojeto.board.repository;

import com.seuprojeto.board.model.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


package com.seuprojeto.board.repository;

import com.seuprojeto.board.model.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskIdOrderByActionDateDesc(Long taskId);
}


@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    // Busca histórico por task ordenado por data (descendente ou ascendente)
    List<TaskHistory> findByTaskIdOrderByEventAtDesc(Long taskId);
}
