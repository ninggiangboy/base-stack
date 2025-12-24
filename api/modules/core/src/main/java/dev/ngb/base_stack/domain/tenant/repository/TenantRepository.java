package dev.ngb.base_stack.domain.tenant.repository;

import dev.ngb.base_stack.base.Repository;
import dev.ngb.base_stack.domain.tenant.model.Tenant;

import java.util.Optional;

public interface TenantRepository extends Repository<Tenant, Long> {
    Optional<Tenant> findByCode(String code);
}
