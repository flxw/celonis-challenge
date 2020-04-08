package com.celonis.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.io.IOUtils;
import org.quartz.*;

import javax.persistence.Entity;
import java.io.*;
import java.net.URL;

@Entity
public class ProjectGenerationTask extends Task {
    @JsonIgnore
    private String storageLocation;

    public ProjectGenerationTask() {
        setType("PROJECTGENERATION");
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public void storeResult(URL url) throws IOException {
        File outputFile = File.createTempFile(this.getId(), ".zip");

        outputFile.deleteOnExit();
        this.setStorageLocation(outputFile.getAbsolutePath());

        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }

    @Override
    public JobDetail createTaskJobDetail() {
        return null;
    }

    @Override
    public ScheduleBuilder createTaskTrigger() {
        return null;
    }

    private class ProjectGenerationTaskJob implements Job {
        public ProjectGenerationTaskJob() {
        }

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            /*URL url = Thread.currentThread().getContextClassLoader().getResource("file.zip");

            if (url == null) {
                throw new InternalException("Zip file not found");
            }

            try {
                storeResult(url);
            } catch (Exception e) {
                throw new InternalException(e);
            }

            setProgress(100);
            setHasConsumableResult(true);*/
        }
    }
}
