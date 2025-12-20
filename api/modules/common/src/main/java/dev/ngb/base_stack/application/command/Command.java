package dev.ngb.base_stack.application.command;

import dev.ngb.base_stack.application.command.hander.CommandHandler;

/**
 * Marker interface representing an application command.
 * <p>
 * A command expresses an intention to perform an action that may change
 * the state of the system. Commands are typically handled by a
 * {@link CommandHandler}.
 *
 * @param <R> the type of result produced after the command is handled
 */
public interface Command<R> {
}
