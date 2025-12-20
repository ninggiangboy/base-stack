package dev.ngb.base_stack.infrastructure.persistence.jdbc.entity.event;

import lombok.Builder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table(value = "event_publications", schema = "public")
@Builder
public record EventPublicationEntity(
        @Id Long id,
        String typeClazz,
        String payload,
        String tenantId,
        Instant occurredAt,
        @CreatedBy String publishedBy
) {
}
