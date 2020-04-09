package com.celonis.challenge.model;

import org.quartz.*;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class TimerTaskJob implements Job {
    public TimerTaskJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
            JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();

            int current = dataMap.getInt("current");
            int y = dataMap.getInt("y");

            if (current < y) ++current;

            System.out.println(current + " - " +  y);

            dataMap.put("current", current);
            dataMap.put("progress", current/(double)y*100);
    }
}