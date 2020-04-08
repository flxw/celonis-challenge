package com.celonis.challenge.configuration;

import com.celonis.challenge.model.TaskExecutionJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfiguration {

    @Bean
    public JobDetail jobADetails() {
        return JobBuilder.newJob(TaskExecutionJob.class)
                         .withIdentity("sampleJobA")
                         .storeDurably()
                         .build();
    }

    @Bean
    public Trigger jobATrigger(JobDetail jobADetails) {
        SimpleScheduleBuilder ssb = SimpleScheduleBuilder
                                        .simpleSchedule()
                                        .withIntervalInSeconds(1)
                                        .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(jobADetails)
                .withIdentity("taskexec")
                .withSchedule(ssb)
                .build();
    }
}