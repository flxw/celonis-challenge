package com.celonis.challenge.services;

import com.celonis.challenge.model.Task;
import com.celonis.challenge.model.TaskRepository;

public class TaskRunner implements Runnable {
    private TaskRepository taskRepository;
    private Task t;

    public TaskRunner(TaskRepository taskRepository, Task t) {
        this.taskRepository = taskRepository;
        this.t = t;
    }

    @Override
    public void run() {
        t.setState(Task.STATE.RUNNING);
        
        while (t.getProgress() < 100) {
            try {
                t.executeStep();
            } catch (InterruptedException e) {
                t.setState(Task.STATE.READY);
                taskRepository.save(t);
                return;
            }
            taskRepository.save(t);
        }

        t.setState(Task.STATE.DONE);
        taskRepository.save(t);
    }
}
