package com.celonis.challenge.model;

import javax.persistence.Entity;

@Entity
public class TimerTask extends Task {
    private int start;
    private int end;
    private int current;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    private double getPercentage() {
        return current/end * 100;
    }

    @Override
    public void execute() {
        System.out.println("Hello from TimerTask");
    }
}
