package com.example.scdf.task.logging_task.service;

import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest
public class LoggingTaskTest {

  @Test
  public void testLoggingTask(CapturedOutput output) {
    Awaitility.await().atMost(10, TimeUnit.SECONDS)
        .until(output::getOut, value -> value.contains("datetime: "));
  }
}