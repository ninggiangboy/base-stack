package dev.ngb.base_stack.application.command.dto;

/**
 * Generic result object containing an identifier produced by a command.
 * <p>
 * Commonly used for commands that create or persist a new entity and
 * need to return its generated identifier.
 *
 * @param <ID>  the type of the identifier
 * @param value the generated or resulting identifier
 */
public record IdResult<ID>(
        ID id
) {
}
