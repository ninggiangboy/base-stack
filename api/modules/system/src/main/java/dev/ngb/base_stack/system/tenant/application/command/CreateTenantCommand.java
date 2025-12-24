package dev.ngb.base_stack.system.tenant.application.command;

import dev.ngb.base_stack.application.command.Command;
import dev.ngb.base_stack.application.command.dto.IdResult;

public record CreateTenantCommand(
        String code,
        String name,
        String description,
        String domain,
        String contact,
        String adminEmail
) implements Command<IdResult<Long>> {
}