package com.celonis.challenge.model;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class TaskExecutionJob implements Job {
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            List<Task> runningTasks = taskRepository.findByState(Task.STATE.RUNNING);

            for (Task t : runningTasks) {
                t.executeStep();

                if (t.getProgress() >= 100.0) {
                    t.setState(Task.STATE.DONE);
                }

                taskRepository.save(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}