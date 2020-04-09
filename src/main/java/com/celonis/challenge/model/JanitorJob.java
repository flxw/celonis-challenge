package com.celonis.challenge.model;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JanitorJob implements Job {
    final int ageThreshold = 7;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            ApplicationContext applicationContext = (ApplicationContext) jobExecutionContext
                    .getScheduler().getContext().get("applicationContext");

            TaskRepository taskRepository = applicationContext.getBean(TaskRepository.class);
            Date today = new Date();

            List<Task> tasks = taskRepository
                    .findAll()
                    .stream()
                    .filter(t -> getDateDiff(t.getCreationDate(), today, TimeUnit.DAYS) > ageThreshold)
                    .collect(Collectors.toList());

            for (Task t : tasks) {
                String taskId = t.getId();
                taskRepository.delete(t);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
