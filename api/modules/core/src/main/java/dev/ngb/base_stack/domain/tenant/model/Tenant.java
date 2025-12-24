package dev.ngb.base_stack.domain.tenant.model;

import dev.ngb.base_stack.base.DomainEntity;
import dev.ngb.base_stack.domain.tenant.constant.TenantStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Tenant extends DomainEntity<Long> {

    private String name;
    private String code;
    private String domain;
    private String contact;
    private String description;
    private TenantStatus status;

    private Tenant() {
    }

    public static Tenant reconstruct(
            Long id,
            String name,
            String code,
            String domain,
            String contact,
            String description,
            TenantStatus status,
            Integer version,
            UUID createdBy,
            UUID updatedBy,
            Instant createdAt,
            Instant updatedAt
    ) {
        Tenant tenant = new Tenant();
        tenant.reconstruct(id, version, createdBy, updatedBy, createdAt, updatedAt);
        tenant.name = name;
        tenant.code = code;
        tenant.domain = domain;
        tenant.contact = contact;
        tenant.description = description;
        tenant.status = status;
        return tenant;
    }

    public static Tenant create(
            String name,
            String code,
            String domain,
            String contact,
            String description) {
        Tenant tenant = new Tenant();
        tenant.name = name;
        tenant.code = code;
        tenant.domain = domain;
        tenant.contact = contact;
        tenant.description = description;
        tenant.status = TenantStatus.INITIALIZING;
        return tenant;
    }

    public void finishInitialization() {
        this.status = TenantStatus.ACTIVE;
    }
}
