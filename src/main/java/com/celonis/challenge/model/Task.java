package com.celonis.challenge.model;

import org.hibernate.annotations.GenericGenerator;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;

import javax.persistence.*;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Task {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    private Date creationDate;
    private boolean hasConsumableResult = false;
    private double progress = 0.0;
    private String type;

    public enum STATE { READY, QUEUED, RUNNING, DONE };
    private STATE state = STATE.READY;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean hasConsumableResult() {
        return this.hasConsumableResult;
    }

    public void setHasConsumableResult(boolean hasConsumableResult) {
        this.hasConsumableResult = hasConsumableResult;
    }

    public double getProgress() {
        return this.progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public STATE getState() {
        return state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public abstract JobDetail createTaskJobDetail();

    public abstract ScheduleBuilder createTaskTrigger();
}
