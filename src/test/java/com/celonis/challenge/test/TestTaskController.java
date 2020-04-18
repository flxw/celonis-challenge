package com.celonis.challenge.test;

import com.celonis.challenge.controllers.TaskController;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskCreationPayload;
import com.celonis.challenge.model.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class TestTaskController {
    String taskName = "Task 1";
    String taskId;

    @Autowired
    private TaskController tc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void taskCreationRejectsInvalid() throws Exception {
        TaskCreationPayload tpl = new TaskCreationPayload();
        tpl.type = TaskCreationPayload.TYPE.PROJECTGENERATION;

        ResponseEntity re = tc.createTask(tpl);
        Assertions.assertNotEquals(HttpStatus.OK, re.getStatusCode());
    }

    @Test
    public void testTaskCRUD() throws Exception {
        TaskCreationPayload tpl = new TaskCreationPayload();
        tpl.type = TaskCreationPayload.TYPE.PROJECTGENERATION;
        tpl.name = taskName;

        ResponseEntity res = tc.createTask(tpl);
        Task createdTask = (Task) res.getBody();

        Assertions.assertEquals(taskName, createdTask.getName());
        Assertions.assertNotNull(createdTask.getId());

        this.taskId = createdTask.getId();

        // READ -----
        Task retrieved = tc.getTask(taskId);
        Assertions.assertEquals(taskName, retrieved.getName());

        // UPDATE -----
        tpl = new TaskCreationPayload();
        tpl.name = taskName + "2";
        tpl.type = TaskCreationPayload.TYPE.PROJECTGENERATION;
        taskName = tpl.name;

        res = tc.updateTask(taskId, tpl);
        Assertions.assertEquals(HttpStatus.OK, res.getStatusCode());

        Task updated = (Task) res.getBody();
        Assertions.assertEquals(taskId, updated.getId());
        Assertions.assertEquals(taskName, updated.getName());

        // DELETE -----
        tc.deleteTask(taskId);
        Assertions.assertFalse(taskRepository.findById(taskId).isPresent());
    }
}