package dev.ngb.base_stack.infrastructure.persistence.jdbc.entity.tenant;

import dev.ngb.base_stack.domain.tenant.constant.TenantStatus;
import dev.ngb.base_stack.infrastructure.persistence.jdbc.base.JdbcEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TenantEntity extends JdbcEntity<Long> {
    private String name;
    private String code;
    private String domain;
    private String contact;
    private String description;
    private TenantStatus status;
}
