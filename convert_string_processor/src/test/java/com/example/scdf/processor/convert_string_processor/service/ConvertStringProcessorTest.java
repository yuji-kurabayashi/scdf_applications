package com.example.scdf.processor.convert_string_processor.service;

import com.example.scdf.processor.convert_string_processor.ConvertStringProcessorApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ConvertStringProcessorTest {

  @Test
  public void testConvertStringProcessor() {
    try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
        TestChannelBinderConfiguration.getCompleteConfiguration(ConvertStringProcessorApplication.class))
        .web(WebApplicationType.NONE)
        .run()) {

      InputDestination source = context.getBean(InputDestination.class);
      MessageConverter converter = context.getBean(CompositeMessageConverter.class);
      Map<String, Object> headers = new HashMap<>();
      MessageHeaders messageHeaders = new MessageHeaders(headers);
      String payload = "scdf_Test";
      Message<?> message = converter.toMessage(payload, messageHeaders);
      source.send(message);

      OutputDestination target = context.getBean(OutputDestination.class);
      // Message<byte[]> receivedMessage = target.receive(10000, "string-pipe-2");
      Environment environment = context.getEnvironment();
      String bindingName = environment.getProperty("spring.cloud.stream.bindings.output.destination");
      Message<byte[]> receivedMessage = target.receive(10000, bindingName);
      String actualPayload = (String) converter.fromMessage(receivedMessage, String.class);
      String expectedPayload = payload.toUpperCase();
      assertThat(actualPayload).isEqualTo(expectedPayload);
    }
  }
}