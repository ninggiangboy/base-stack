package dev.ngb.base_stack.infrastructure.persistence.jdbc.repository.event;

import dev.ngb.base_stack.infrastructure.persistence.jdbc.entity.event.EventPublicationEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.ListCrudRepository;

public interface EventPublicationRepository
        extends ListCrudRepository<@NonNull EventPublicationEntity, @NonNull Long> {
}
