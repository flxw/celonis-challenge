package com.celonis.challenge.model;

import org.quartz.*;

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
        setType("TIMER");
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public JobDetail createTaskJobDetail() {
        return JobBuilder.newJob(TimerTaskJob.class)
                         .withIdentity(getId(), getJobGroup())
                         .usingJobData("current", x)
                         .usingJobData("y", y)
                         .usingJobData("progress", 0.0)
                         .storeDurably()
                         .build();
    }

    @Override
    public Trigger createTaskTrigger(JobDetail job) {
        SimpleScheduleBuilder ssb = SimpleScheduleBuilder.simpleSchedule()
                                    .withIntervalInSeconds(1)
                                    .withRepeatCount(y-x);

        return TriggerBuilder.newTrigger()
                .forJob(job)
                .withIdentity("trigger", getId())
                .withSchedule(ssb)
                .build();
    }
}
