package dev.ngb.base_stack.application.command.dispatcher;

import dev.ngb.base_stack.application.command.Command;
import dev.ngb.base_stack.application.command.dto.JobId;

import java.time.Duration;

public interface CommandScheduler {
    JobId schedule(Command<Void> command);

    JobId schedule(Command<Void> command, Duration delay);

    void cancel(JobId jobId);
}
