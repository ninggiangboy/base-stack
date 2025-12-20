package dev.ngb.base_stack.infrastructure.persistence.jdbc.base;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for JDBC persistence entities.
 * <p>
 * This class provides common persistence-related fields and integrates with
 * Spring Data auditing and optimistic locking mechanisms. Equality and hash
 * code are based solely on the entity identifier.
 *
 * @param <ID> the type of the persistence entity identifier
 */
@Getter
@Setter
@SuperBuilder
public abstract class JdbcEntity<ID> {
    @Id
    protected ID id;
    @Version
    protected Integer version;
    @CreatedBy
    protected UUID createdBy;
    @LastModifiedBy
    protected UUID updatedBy;
    @CreatedDate
    protected Instant createdAt;
    @LastModifiedDate
    protected Instant updatedAt;
    protected Boolean isDeleted;

    /**
     * Compares this entity to another object based on identity.
     *
     * @param o the object to compare with
     * @return {@code true} if both objects are of the same type and have the same identifier
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JdbcEntity<?> that = (JdbcEntity<?>) o;
        return Objects.equals(id, that.id);
    }

    /**
     * Computes the hash code based on the entity identifier.
     *
     * @return the hash code of this entity
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
