package dev.ngb.base_stack.application.shared.port;

import org.jspecify.annotations.Nullable;

public interface TenantContext {
    void setTenantId(@Nullable String tenantId);

    @Nullable String getTenantId();

    void clear();
}
