package dev.ngb.base_stack.infrastructure.persistence.jdbc.adapter.tenant;

import dev.ngb.base_stack.domain.tenant.model.Tenant;
import dev.ngb.base_stack.domain.tenant.repository.TenantRepository;
import dev.ngb.base_stack.infrastructure.persistence.jdbc.base.JdbcRepository;
import dev.ngb.base_stack.infrastructure.persistence.jdbc.entity.tenant.TenantEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TenantJdbcRepository
        extends JdbcRepository<Tenant, TenantEntity, Long>
        implements TenantRepository {

    @Override
    public Optional<Tenant> findByCode(String code) {
        return findOneByField("code", code);
    }

    @Override
    protected Tenant toDomain(TenantEntity entity) {
        return Tenant.reconstruct(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getDomain(),
                entity.getContact(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getVersion(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    protected TenantEntity toJdbc(Tenant entity) {
        return TenantEntity.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .domain(entity.getDomain())
                .contact(entity.getContact())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .version(entity.getVersion())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
