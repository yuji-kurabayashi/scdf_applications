package com.example.scdf.processor.convert_string_processor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import java.util.function.Function;

@Slf4j
@Configuration
public class ConvertStringProcessor {

  // Spring Cloud Data Flow Stream Processor type application implementation needs Function type bean.
  @Bean
  public Function<Message<String>, Message<String>> processConvertString() {
    return (input) -> {
      Message<String> output = new GenericMessage<>(input.getPayload().toUpperCase(), input.getHeaders());
      log.info("input: {}, output: {}", input.getPayload(), output.getPayload());
      return output;
    };
  }
//  public Function<String, String> processConvertString() {
//    return (input) -> {
//      String payload = input.toUpperCase();
//      log.info("input: {}, output: {}", input, payload);
//      return payload;
//    };
//  }
}
