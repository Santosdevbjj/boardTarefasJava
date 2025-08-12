package com.example.board.repository;

import com.example.board.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> findByTaskIdOrderByActionDateDesc(Long taskId);
}
