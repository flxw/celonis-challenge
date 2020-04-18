package com.celonis.challenge.test;

import com.celonis.challenge.model.TaskCreationPayload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestTaskCreationPayload {
    TaskCreationPayload tpl;

    @BeforeEach
    public void resetPayload() {
         tpl = new TaskCreationPayload();
    }

    @Test
    public void invalidProjectGenerationPayload() {
        tpl.type = TaskCreationPayload.TYPE.PROJECTGENERATION;
        Assertions.assertFalse(tpl.isValid());
    }

    @Test
    public void validProjectGenerationPayload() {
        tpl.type = TaskCreationPayload.TYPE.PROJECTGENERATION;
        tpl.name = "Valid ProjectGenerationTask";
        Assertions.assertTrue(tpl.isValid());
    }

    @Test
    public void invalidTimerPayload() {
        tpl.type = TaskCreationPayload.TYPE.TIMER;
        tpl.name = "Invalid Timer";
        tpl.x = 100;
        tpl.y = 1;
        Assertions.assertFalse(tpl.isValid());
    }

    @Test
    public void validTimerPayload() {
        tpl.type = TaskCreationPayload.TYPE.TIMER;
        tpl.name = "Valid Timer";
        tpl.x = 1;
        tpl.y = 100;
        Assertions.assertTrue(tpl.isValid());
    }
}
