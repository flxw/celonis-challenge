package com.celonis.challenge.model;

import java.util.Date;

public class TaskCreationPayload {
    public enum TYPE {
        TIMER_TASK,
        PROJECTGENERATION_TASK
    };

    public String name;
    public TYPE type;
    public int x;
    public int y;

    public Task generateTask() {
        Task t = null;

        switch (this.type) {
            case TIMER_TASK:
                t = new TimerTask(x,y);
                break;

            case PROJECTGENERATION_TASK:
                t = new ProjectGenerationTask();
                break;
        }

        t.setCreationDate(new Date());
        t.setName(name);

        return t;
    }
}
