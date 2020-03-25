package com.celonis.challenge.model;

import javax.persistence.Entity;

public class TimerTask extends Task {
    private int start;
    private int end;
    private int current;

    public TimerTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    private double getPercentage() {
        return current/end * 100;
    }
}
