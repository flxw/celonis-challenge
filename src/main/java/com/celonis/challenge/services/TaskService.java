package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.InternalException;
import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.TaskCreationPayload;
import com.celonis.challenge.model.TaskRepository;
import com.celonis.challenge.model.Task;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private ThreadPoolTaskExecutor taskExecutor;
    private Map<String,Future> executedTaskMap;

    public TaskService(TaskRepository taskRepository, ThreadPoolTaskExecutor taskExecutor) {
        this.taskRepository = taskRepository;
        this.taskExecutor = taskExecutor;
        this.executedTaskMap = new HashMap<>();

        TaskCreationPayload tp = new TaskCreationPayload();
        tp.creationDate = new Date();
        tp.type = TaskCreationPayload.TYPE.TIMER_TASK;
        tp.x = 1;
        tp.y = 100;
        createTask(tp);
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

        existing.setState(Task.STATE.QUEUED);
        taskRepository.save(existing);

        TaskRunner runner = new TaskRunner(taskRepository, existing);
        Future f = this.taskExecutor.submit(runner);
        executedTaskMap.put(taskId, f);
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

    public void cancelTask(String taskId) {
        Future t = this.executedTaskMap.getOrDefault(taskId, null);

        if (t != null) {
            this.executedTaskMap.get(taskId).cancel(true);
            this.executedTaskMap.remove(taskId);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedDelay = 7*24*60*60*1000)
    public void cleanUpJobs() {
        Date today = new Date();
        List<Task> tasks = taskRepository
                .findAll()
                .stream()
                .filter(t -> getDateDiff(t.getCreationDate(), today, TimeUnit.DAYS) > 7)
                .collect(Collectors.toList());

        for (Task t : tasks) {
            String taskId = t.getId();

            if (executedTaskMap.containsKey(taskId)) {
                executedTaskMap.get(taskId).cancel(true);
                executedTaskMap.remove(taskId);
            }

            taskRepository.delete(t);
        }
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
