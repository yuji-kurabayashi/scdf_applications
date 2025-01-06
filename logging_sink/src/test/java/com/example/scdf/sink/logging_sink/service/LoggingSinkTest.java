package com.example.scdf.sink.logging_sink.service;

import com.example.scdf.sink.logging_sink.LoggingSinkApplication;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(OutputCaptureExtension.class)
public class LoggingSinkTest {

  @Test
  public void testLoggingSink(CapturedOutput output) {
    try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
        TestChannelBinderConfiguration.getCompleteConfiguration(LoggingSinkApplication.class))
        .web(WebApplicationType.NONE)
        .run()) {

      MessageConverter converter = context.getBean(CompositeMessageConverter.class);
      InputDestination source = context.getBean(InputDestination.class);
      Map<String, Object> headers = new HashMap<>();
      MessageHeaders messageHeaders = new MessageHeaders(headers);
      String payload = "SCDF_TEST";
      Message<?> message = converter.toMessage(payload, messageHeaders);
      source.send(message);

      Awaitility.await().until(output::getOut, value -> value.contains(payload));
    }
  }
}