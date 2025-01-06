package com.example.scdf.source.string_source.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Slf4j
@Configuration
public class StringSource {

  // Spring Cloud Data Flow Stream Source type application implementation needs Supplier type bean.
  @Bean
  public Supplier<String> sendEvents() {
    return () -> {
      String payload = "scdf_Test";
      log.info("output: {}", payload);
      return payload;
    };
  }
}
