package com.celonis.challenge.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import java.util.Date;

@Entity
@Inheritance
public abstract class Task {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    private Date creationDate;
    private boolean hasConsumableResult = false;
    private double progress = 0.0;

    public enum STATE { READY, QUEUED, CANCELED, DONE };
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

    public double getProgress() {
        return this.progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public void executeStep() throws InterruptedException {
        this.progress = 100;
    }

    public void setState(STATE state) {
        this.state = state;
    }
}
