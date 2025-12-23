package dev.ngb.base_stack.infrastructure.quartz.scheduler;

import dev.ngb.base_stack.application.command.Command;
import dev.ngb.base_stack.application.command.dispatcher.CommandBus;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@RequiredArgsConstructor
public class QuartzCommandExecutor implements Job {

    private static final String COMMAND_KEY = "command";

    private final CommandBus commandBus;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            Command<Void> command =
                    (Command<Void>) context.getMergedJobDataMap().get(COMMAND_KEY);

            commandBus.dispatch(command);
        } catch (Exception e) {
            throw new JobExecutionException("Command execution failed", e, false);
        }
    }
}
