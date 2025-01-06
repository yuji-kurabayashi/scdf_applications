package com.example.scdf.source.string_source.service;

import com.example.scdf.source.string_source.StringSourceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.MessageConverter;

import static org.assertj.core.api.Assertions.assertThat;

public class StringSourceTest {

  @Test
  public void testStringSource() {
    try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
        TestChannelBinderConfiguration.getCompleteConfiguration(StringSourceApplication.class))
        .web(WebApplicationType.NONE)
        .run()) {

      MessageConverter converter = context.getBean(CompositeMessageConverter.class);
      OutputDestination target = context.getBean(OutputDestination.class);
      // The argument 'bindingName' of the OutputDestination#receive method
      // must be the value of the spring.cloud.stream.bindings.output.destination setting in application.yml.
      // Message<byte[]> receivedMessage = target.receive(10000, "string-pipe-1");
      Environment environment = context.getEnvironment();
      String bindingName = environment.getProperty("spring.cloud.stream.bindings.output.destination");
      Message<byte[]> receivedMessage = target.receive(10000, bindingName);
      String actualPayload = (String) converter.fromMessage(receivedMessage, String.class);
      String expectedPayload = "scdf_Test";
      assertThat(actualPayload).isEqualTo(expectedPayload);
    }
  }
}
