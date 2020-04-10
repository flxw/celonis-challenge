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
import java.util.List;
import java.util.Optional;

import static org.quartz.impl.matchers.GroupMatcher.jobGroupEquals;

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
            lm.addJobListener(progressPersisterListener, jobGroupEquals(Task.getJobGroup()));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        scheduleJanitor();
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
        Trigger trigger = existing.createTaskTrigger(job);

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

    public void cancelTask(String taskId) {
        JobKey jobToBeDeleted = new JobKey(taskId);
        try {
            schedulerFactory.getScheduler().deleteJob(jobToBeDeleted);
            taskRepository.setStateFor(Task.STATE.READY, taskId);
            taskRepository.setProgressFor(0.0, taskId);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void scheduleJanitor() {
        JobDetail janitorDetail = JobBuilder.newJob(JanitorJob.class)
                    .storeDurably()
                    .build();

        Trigger janitorTrigger = TriggerBuilder.newTrigger()
                    .forJob(janitorDetail)
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 ? * SUN *"))
                    .build();

        try {
            schedulerFactory.getScheduler().scheduleJob(janitorDetail, janitorTrigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}


