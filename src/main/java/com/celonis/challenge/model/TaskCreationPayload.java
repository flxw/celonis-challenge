package com.celonis.challenge.model;

import java.util.Date;

public class TaskCreationPayload {
    public enum TYPE {
        TIMER_TASK,
        PROJECTGENERATION_TASK
    };

    public String id;
    public String name;
    public Date creationDate;
    public TYPE type;
    public int x;
    public int y;

    public TYPE getType() {
        return type;
    }

    public Task generateTask() {
        Task t = null;

        switch (this.type) {
            case TIMER_TASK:
                t = new TimerTask(x, y);
                break;

            case PROJECTGENERATION_TASK:
                t = new ProjectGenerationTask();
                t.setId(id);
                t.setCreationDate(creationDate);
                t.setName(name);
                break;
        }

        return t;
    }
}
