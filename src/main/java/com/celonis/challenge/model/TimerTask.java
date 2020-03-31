package com.celonis.challenge.model;

import javax.persistence.Entity;

@Entity
public class TimerTask extends Task {
    private int x;
    private int y;
    private int current = Integer.MIN_VALUE;

    public TimerTask() {
        super();
    }

    public TimerTask(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        setType("timer");
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public void executeStep() {
        if (current == Integer.MIN_VALUE) {
            current = x;
        } else if (current == y) {
            return;
        }

        current++;
        setProgress(current/(double) y * 100);
    }
}
