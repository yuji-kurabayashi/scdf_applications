package com.example.scdf.sink.logging_sink.service;

import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LoggingSink {

  // Spring Cloud Data Flow Stream Sink type application implementation needs Consumer type bean.
  // @Bean
  @Bean("consumer")
  public Consumer<String> process() {
    return input -> log.info("input: {}", input);
  }
}
