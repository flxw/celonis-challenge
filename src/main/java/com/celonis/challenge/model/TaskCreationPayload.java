package com.celonis.challenge.model;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class TaskCreationPayload {
    public enum TYPE {
        TIMER,
        PROJECTGENERATION
    };

    @NotNull
    public String name = "";

    @NotNull
    public TYPE type;

    public int x;
    public int y;

    public Task generateTask() {
        Task t = null;

        switch (this.type) {
            case TIMER:
                t = new TimerTask(x,y);
                break;

            case PROJECTGENERATION:
                t = new ProjectGenerationTask();
                break;
        }

        t.setCreationDate(new Date());
        t.setName(name);

        return t;
    }

    public boolean isValid() {
        boolean valid = !name.isEmpty();

        switch (this.type) {
            case PROJECTGENERATION:
                break;

            case TIMER:
                valid = valid && x < y;
                break;
        }

        return valid;
    }
}
