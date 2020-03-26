package com.celonis.challenge.model;

import javax.persistence.Entity;

@Entity
public class TimerTask extends Task {
    private int start;
    private int end;
    private int current = Integer.MIN_VALUE;

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public void executeStep() throws InterruptedException {
        if (current == Integer.MIN_VALUE) {
            current = start;
        } else if (current == end) {
            return;
        }

        current++;
        setProgress(current/(double)end * 100);
        Thread.sleep(1000);
    }
}
