package dev.ngb.base_stack.domain.tenant.error;

import dev.ngb.base_stack.base.DomainError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TenantError implements DomainError {
    DUPLICATE_CODE("Tenant code already exists");

    private final String message;
}
