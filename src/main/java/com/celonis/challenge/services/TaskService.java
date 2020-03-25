package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.TaskCreationPayload;
import com.celonis.challenge.model.TaskRepository;
import com.celonis.challenge.model.Task;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private TaskExecutor taskExecutor;

    public TaskService(TaskRepository taskRepository, TaskExecutor taskExecutor) {
        this.taskRepository = taskRepository;
        this.taskExecutor = taskExecutor;
    }

    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(TaskCreationPayload tp) {
        Task t = tp.generateTask();
        return taskRepository.save(t);
    }

    public Task update(String taskId, Task deltaTask) {
        Task existing = getTask(taskId);
        existing.setCreationDate(deltaTask.getCreationDate());
        existing.setName(deltaTask.getName());
        return taskRepository.save(existing);
    }

    public void delete(String taskId) {
        taskRepository.delete(taskId);
    }

    public void executeTask(String taskId) {
        Task existing = getTask(taskId);

        Runnable runner = () -> {
            existing.execute();
            taskRepository.save(existing);
        };

        this.taskExecutor.execute(runner);
    }

    public Task getTask(String taskId) {
        Task projectGenerationTask = taskRepository.findOne(taskId);
        if (projectGenerationTask == null) {
            throw new NotFoundException();
        }
        return projectGenerationTask;
    }

    public ResponseEntity getTaskResult(ProjectGenerationTask existing) {
        File inputFile = new File(existing.getStorageLocation());

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");

        return new ResponseEntity<>(new FileSystemResource(inputFile), respHeaders, HttpStatus.OK);
    }
}
