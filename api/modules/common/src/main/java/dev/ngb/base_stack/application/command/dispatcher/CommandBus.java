package dev.ngb.base_stack.application.command.dispatcher;

import dev.ngb.base_stack.application.command.Command;
import dev.ngb.base_stack.application.command.hander.CommandHandler;

/**
 * Dispatches {@link Command commands} to their corresponding handlers.
 * <p>
 * The command bus acts as an entry point to the application layer,
 * decoupling command senders from command handlers. Implementations
 * are responsible for locating the appropriate handler and invoking it.
 *
 * @see CommandHandler
 */
public interface CommandBus {
    /**
     * Dispatches the given command for handling.
     *
     * @param command the command to dispatch
     * @param <R>     the type of result produced by the command
     * @return the result returned by the command handler
     * @throws IllegalStateException if no suitable handler is found
     */
    <R> R dispatch(Command<R> command);
}
