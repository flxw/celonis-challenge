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
        while (t.getProgress() < 100) {
            try {
                t.executeStep();
            } catch (InterruptedException e) {
                t.setState(Task.STATE.CANCELED);
                taskRepository.save(t);
                return;
            }
            taskRepository.save(t);
        }
    }
}
