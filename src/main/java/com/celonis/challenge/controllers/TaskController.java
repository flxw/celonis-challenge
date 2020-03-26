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
    public Task createTask(@RequestBody TaskCreationPayload taskCreationPayload) {
        return taskService.createTask(taskCreationPayload);
    }

    @GetMapping("/{taskId}")
    public Task getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId);
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable String taskId,
                           @RequestBody @Valid Task projectGenerationTask) {
        return taskService.update(taskId, projectGenerationTask);
    }

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
