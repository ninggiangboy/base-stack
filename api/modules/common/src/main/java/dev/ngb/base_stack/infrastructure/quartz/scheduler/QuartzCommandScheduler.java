package dev.ngb.base_stack.infrastructure.quartz.scheduler;

import dev.ngb.base_stack.application.command.Command;
import dev.ngb.base_stack.application.command.dispatcher.CommandScheduler;
import dev.ngb.base_stack.application.command.dto.JobId;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QuartzCommandScheduler implements CommandScheduler {

    private static final String COMMAND_KEY = "command";

    private final Scheduler scheduler;

    @Override
    public JobId schedule(Command<Void> command) {
        return schedule(command, Duration.ZERO);
    }

    @Override
    public JobId schedule(Command<Void> command, Duration delay) {
        try {
            String jobKey = UUID.randomUUID().toString();

            JobDetail jobDetail = JobBuilder.newJob(QuartzCommandExecutor.class)
                    .withIdentity(jobKey)
                    .usingJobData(new JobDataMap())
                    .build();

            jobDetail.getJobDataMap().put(COMMAND_KEY, command);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobKey + "-trigger")
                    .startAt(DateBuilder.futureDate(
                            (int) delay.toMillis(),
                            DateBuilder.IntervalUnit.MILLISECOND
                    ))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

            return new JobId(jobKey);
        } catch (SchedulerException e) {
            throw new IllegalStateException("Failed to schedule command", e);
        }
    }

    @Override
    public void cancel(JobId jobId) {
        try {
            scheduler.deleteJob(JobKey.jobKey(jobId.value()));
        } catch (SchedulerException e) {
            throw new IllegalStateException("Failed to cancel job " + jobId.value(), e);
        }
    }
}
