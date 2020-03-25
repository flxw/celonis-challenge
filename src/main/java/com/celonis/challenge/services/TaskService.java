package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.TaskRepository;
import com.celonis.challenge.model.Task;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
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

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {
        task.setId(null);
        task.setCreationDate(new Date());
        return taskRepository.save(task);
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

        // TODO: put stuff into thread of its own here
        /*URL url = Thread.currentThread().getContextClassLoader().getResource("challenge.zip");
        if (url == null) {
            throw new InternalException("Zip file not found");
        }
        try {
            storeResult(taskId, url);
        } catch (Exception e) {
            throw new InternalException(e);
        }*/
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

    public void storeResult(String taskId, URL url) throws IOException {
        ProjectGenerationTask existing = (ProjectGenerationTask) getTask(taskId);
        File outputFile = File.createTempFile(taskId, ".zip");

        outputFile.deleteOnExit();
        existing.setStorageLocation(outputFile.getAbsolutePath());
        taskRepository.save(existing);

        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }
}
