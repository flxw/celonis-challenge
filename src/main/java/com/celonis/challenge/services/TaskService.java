package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskCreationPayload;
import com.celonis.challenge.model.TaskRepository;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final SchedulerFactoryBean scheduler;

    private Map<String,Future> executedTaskMap;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, SchedulerFactoryBean scheduler) {
        this.taskRepository = taskRepository;
        this.scheduler = scheduler;
        this.executedTaskMap = new HashMap<>();
    }

    public List<Task> listTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(TaskCreationPayload tp) {
        Task t = tp.generateTask();
        return taskRepository.save(t);
    }

    public Task update(String taskId, TaskCreationPayload tpl) {
        Task deltaTask = tpl.generateTask();
        deltaTask.setId(taskId);
        return taskRepository.save(deltaTask);
    }

    public void delete(String taskId) {
        taskRepository.deleteById(taskId);
    }

    public void executeTask(String taskId) {
        Task existing = getTask(taskId);
        JobDetail job = existing.createTaskJobDetail();
        ScheduleBuilder ssb = existing.createTaskTrigger();
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(job)
                .withIdentity("trigger", taskId)
                .withSchedule(ssb)
                .build();

        try {
            scheduler.getScheduler().scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public Task getTask(String taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);

        if (!taskOpt.isPresent()) {
            throw new NotFoundException();
        }

        return taskOpt.get();
    }

    public ResponseEntity getTaskResult(ProjectGenerationTask existing) {
        File inputFile = new File(existing.getStorageLocation());

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentDispositionFormData("attachment", "challenge.zip");

        return new ResponseEntity<>(new FileSystemResource(inputFile), respHeaders, HttpStatus.OK);
    }

    public void cancelTask(String taskId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);

        if (!optionalTask.isPresent()) {
            return;
        }

        Task t = optionalTask.get();

        t.setState(Task.STATE.READY);
        taskRepository.save(t);

        // scheduler.deleteJob(jobName, jobGroup);
        // TODO: when deleting, cancel first!
    }

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


