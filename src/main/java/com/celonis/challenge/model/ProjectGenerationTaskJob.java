package com.celonis.challenge.model;

import org.apache.commons.io.IOUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.*;
import java.net.URL;

public class ProjectGenerationTaskJob implements Job {
    public ProjectGenerationTaskJob() {}

    private void storeResult(URL url, String fileLocation) throws IOException {
        File outputFile = new File(fileLocation);

        try (InputStream is = url.openStream();
             OutputStream os = new FileOutputStream(outputFile)) {
            IOUtils.copy(is, os);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        URL url = Thread.currentThread().getContextClassLoader().getResource("file.zip");
        String fileLocation = dataMap.getString("fileLocation");

        if (url == null) {
            throw new JobExecutionException("Zip file not found");
        }

        try {
            storeResult(url, fileLocation);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }

        dataMap.put("progress", 100.0);
    }
}
