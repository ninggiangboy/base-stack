package dev.ngb.base_stack.system.tenant.application.command.handler;

import dev.ngb.base_stack.application.command.dto.IdResult;
import dev.ngb.base_stack.application.command.hander.CommandHandler;
import dev.ngb.base_stack.base.BusinessException;
import dev.ngb.base_stack.domain.tenant.error.TenantError;
import dev.ngb.base_stack.domain.tenant.model.Tenant;
import dev.ngb.base_stack.domain.tenant.repository.TenantRepository;
import dev.ngb.base_stack.system.tenant.application.command.CreateTenantCommand;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class CreateTenantHandler implements CommandHandler<CreateTenantCommand, IdResult<Long>> {

    private final TenantRepository tenantRepository;

    @Override
    public IdResult<Long> handle(CreateTenantCommand command) {
        Optional<Tenant> existedByCode = tenantRepository.findByCode(command.code());
        if (existedByCode.isPresent()) {
            Map<String, Object> dataError = Map.of("existedId", existedByCode.get().getId());
            throw new BusinessException(TenantError.DUPLICATE_CODE, dataError);
        }

        Tenant tenant = Tenant.create(
                command.name(),
                command.code(),
                command.domain(),
                command.contact(),
                command.description()
        );

        tenant = tenantRepository.save(tenant);
        return new IdResult<>(tenant.getId());
    }
}
