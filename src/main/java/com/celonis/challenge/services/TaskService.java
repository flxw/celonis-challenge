package com.celonis.challenge.services;

import com.celonis.challenge.exceptions.NotFoundException;
import com.celonis.challenge.model.*;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.quartz.impl.matchers.EverythingMatcher.allJobs;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final SchedulerFactoryBean schedulerFactory;
    private JobListener progressPersisterListener;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, SchedulerFactoryBean schedulerFactory) {
        this.taskRepository = taskRepository;
        this.schedulerFactory = schedulerFactory;
        this.progressPersisterListener = new ProgressPersisterListener(taskRepository);

        try {
            ListenerManager lm = schedulerFactory.getScheduler().getListenerManager();
            lm.addJobListener(progressPersisterListener, allJobs());
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
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
        cancelTask(taskId);
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
            schedulerFactory.getScheduler().scheduleJob(job, trigger);
            taskRepository.setStateFor(Task.STATE.RUNNING, taskId);
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

3    public void cancelTask(String taskId) {
        JobKey jobToBeDeleted = new JobKey(taskId);
        try {
            schedulerFactory.getScheduler().deleteJob(jobToBeDeleted);
            taskRepository.setStateFor(Task.STATE.READY, taskId);
            taskRepository.setProgressFor(0.0, taskId);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void cleanUpJobs() {
        // TODO: refactor
        /*
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
        }*/
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}


