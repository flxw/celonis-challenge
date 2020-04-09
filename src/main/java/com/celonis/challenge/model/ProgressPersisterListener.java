package com.celonis.challenge.model;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class ProgressPersisterListener implements JobListener {
    private final TaskRepository taskRepository;

    public ProgressPersisterListener(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public String getName() {
        return "ProgressPersisterListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) { }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) { }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        double progress = jobExecutionContext.getJobDetail().getJobDataMap().getDouble("progress");
        String taskId = jobExecutionContext.getJobDetail().getKey().getName();
        taskRepository.setProgressFor(progress, taskId);
    }
}
