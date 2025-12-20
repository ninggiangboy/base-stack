package dev.ngb.base_stack.infrastructure.multi_tenancy.context;

import dev.ngb.base_stack.application.shared.port.TenantContext;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ThreadLocalTenantContext implements TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT_ID = new ThreadLocal<>();

    @Override
    public void setTenantId(@Nullable String tenantId) {
        if (tenantId == null) {
            CURRENT_TENANT_ID.remove();
        } else {
            CURRENT_TENANT_ID.set(tenantId);
        }
    }

    @Override
    public @Nullable String getTenantId() {
        return CURRENT_TENANT_ID.get();
    }

    @Override
    public void clear() {
        CURRENT_TENANT_ID.remove();
    }
}
