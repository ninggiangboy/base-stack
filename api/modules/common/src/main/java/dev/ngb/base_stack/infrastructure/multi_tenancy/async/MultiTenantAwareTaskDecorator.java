package dev.ngb.base_stack.infrastructure.multi_tenancy.async;

import dev.ngb.base_stack.application.shared.port.TenantContext;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.core.task.TaskDecorator;

@RequiredArgsConstructor
public class MultiTenantAwareTaskDecorator implements TaskDecorator {

    private final TenantContext tenantContext;

    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        String tenantId = tenantContext.getTenantId();
        return () -> {
            try {
                tenantContext.setTenantId(tenantId);
                runnable.run();
            } finally {
                tenantContext.clear();
            }
        };
    }
}
