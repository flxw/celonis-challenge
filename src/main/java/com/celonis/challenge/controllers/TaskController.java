package com.celonis.challenge.controllers;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskCreationPayload;
import com.celonis.challenge.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public List<Task> listTasks() {
        return taskService.listTasks();
    }

    @PostMapping("/")
    public ResponseEntity createTask(@RequestBody @Valid TaskCreationPayload taskCreationPayload) {
        ResponseEntity returnEntity;

        if (taskCreationPayload.isValid()) {
            Task t = taskService.createTask(taskCreationPayload);
            returnEntity = ResponseEntity.status(HttpStatus.OK).body(t);
        } else {
            returnEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return returnEntity;
    }

    @GetMapping("/{taskId}")
    public Task getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity updateTask(@PathVariable String taskId,
                           @RequestBody @Valid TaskCreationPayload payload) {
        ResponseEntity returnEntity;

        if (payload.isValid()) {
            Task t = taskService.update(taskId, payload);
            returnEntity = ResponseEntity.status(HttpStatus.OK).body(t);
        } else {
            returnEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return returnEntity;
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        taskService.delete(taskId);
    }

    @PostMapping("/{taskId}/execute")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void executeTask(@PathVariable String taskId) {
        taskService.executeTask(taskId);
    }

    @PostMapping("/{taskId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelTask(@PathVariable String taskId) {
        taskService.cancelTask(taskId);
    }

    @GetMapping("/{taskId}/result")
    public ResponseEntity getResult(@PathVariable String taskId) {
        Task t = taskService.getTask(taskId);

        if (t.hasConsumableResult()) {
            return taskService.getTaskResult((ProjectGenerationTask) t);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

}

// HALLO IHR WURSTKÖPFE