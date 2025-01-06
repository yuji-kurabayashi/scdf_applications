package com.example.scdf.task.logging_task.service;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableTask
public class LoggingTask {

  // Spring Cloud Data Flow Task type application implementation needs CommandLineRunner type bean.
  @Bean
  public CommandLineRunner commandLineRunner() {
    return args -> log.info("datetime: {}, args: {}", LocalDateTime.now(), args);
  }
}
