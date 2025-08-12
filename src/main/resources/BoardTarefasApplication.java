package com.seuprojeto.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Classe principal para rodar a aplicação Spring Boot.
 */
@SpringBootApplication
@EnableAsync
public class BoardTarefasApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardTarefasApplication.class, args);
    }

}
