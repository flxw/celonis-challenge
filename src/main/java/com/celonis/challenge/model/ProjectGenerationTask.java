package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import javax.persistence.Entity;
import java.io.File;
import java.io.IOException;

@Entity
public class ProjectGenerationTask extends Task {
    @JsonIgnore
    private String storageLocation;

    public ProjectGenerationTask() {
        setType("PROJECTGENERATION");
        setHasConsumableResult(true);
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    @Override
    public JobDetail createTaskJobDetail() {
        // create the file before the task starts
        // this avoids creating a separate onJobFinish handler for now
        File outputFile = null;
        try {
            outputFile = File.createTempFile(getId(), ".zip");
            outputFile.deleteOnExit();
            setStorageLocation(outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JobBuilder.newJob(ProjectGenerationTaskJob.class)
                .withIdentity(getId(), getJobGroup())
                .usingJobData("progress", 0.0)
                .usingJobData("fileLocation", getStorageLocation())
                .storeDurably()
                .build();
    }

    @Override
    public Trigger createTaskTrigger(JobDetail job) {
        return TriggerBuilder.newTrigger()
                .forJob(job)
                .startNow()
                .build();
    }
}
