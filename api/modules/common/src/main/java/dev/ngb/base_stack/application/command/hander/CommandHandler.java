package dev.ngb.base_stack.application.command.hander;

import dev.ngb.base_stack.application.command.Command;
import dev.ngb.base_stack.application.shared.ApplicationService;

/**
 * Handles a specific {@link Command} type.
 * <p>
 * Implementations contain the application-level logic required to process
 * a command and return a result. Each command should have exactly one
 * corresponding handler.
 *
 * @param <C> the type of command this handler supports
 * @param <R> the type of result returned after handling the command
 */
public interface CommandHandler<C extends Command<R>, R> extends ApplicationService {

    /**
     * Handles the given command.
     * <p>
     * This method contains the core application logic for executing
     * the command. Implementations may perform validation, interact
     * with domain models, and coordinate persistence or external services.
     *
     * @param command the command to handle
     * @return the result produced after handling the command
     */
    R handle(C command);
}
