package dev.ngb.base_stack.base;

import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all domain entities.
 * <p>
 * A domain entity is defined by its identity rather than its attributes.
 * Equality and hash code are therefore based solely on the entity identifier.
 *
 * @param <ID> the type of the entity identifier
 */
@Getter
public abstract class DomainEntity<ID> {

    protected ID id;
    protected Integer version;
    protected UUID createdBy;
    protected UUID updatedBy;
    protected Instant createdAt;
    protected Instant updatedAt;

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
        DomainEntity<?> that = (DomainEntity<?>) o;
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
