package dev.ngb.base_stack.infrastructure.persistence.jdbc.base;

import dev.ngb.base_stack.base.DomainEntity;
import dev.ngb.base_stack.base.Repository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public abstract class JdbcRepository<D extends DomainEntity<ID>, J extends JdbcEntity<ID>, ID>
        implements Repository<D, ID> {

    private static final String IS_DELETED_COLUMN = "is_deleted";
    private static final String ID_COLUMN = "id";

    @Autowired
    protected JdbcAggregateTemplate template;
    private final Class<J> clazz;
    protected final Criteria defaultCriteria = where(IS_DELETED_COLUMN).isFalse();

    @SuppressWarnings("unchecked")
    protected JdbcRepository() {
        this.clazz = (Class<J>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[1];
    }

    protected abstract D toDomain(J entity);

    protected abstract J toJdbc(D entity);

    protected Query fromCriteria(@Nullable Criteria criteria) {
        Criteria finalCriteria;
        if (criteria == null) {
            finalCriteria = defaultCriteria;
        } else {
            finalCriteria = defaultCriteria.and(criteria);
        }
        return query(finalCriteria);
    }

    protected List<D> findAllBy(@Nullable Criteria criteria) {
        return template.findAll(fromCriteria(criteria), clazz).stream().map(this::toDomain).toList();
    }

    protected Optional<D> findOneBy(@Nullable Criteria criteria) {
        return template.findOne(fromCriteria(criteria), clazz).map(this::toDomain);
    }

    protected Long countBy(@Nullable Criteria criteria) {
        return template.count(fromCriteria(criteria), clazz);
    }

    protected List<D> findAllByField(String fieldName, Object value) {
        Criteria criteria = where(fieldName).is(value);
        return findAllBy(criteria);
    }

    protected Optional<D> findOneByField(String fieldName, Object value) {
        Criteria criteria = where(fieldName).is(value);
        return findOneBy(criteria);
    }

    @Override
    public List<D> findAll() {
        return findAllBy(null);
    }

    @Override
    public Optional<D> findById(ID id) {
        Criteria findByIdCriteria = where(ID_COLUMN).is(id);
        return findOneBy(findByIdCriteria);
    }

    @Override
    public List<D> findByIds(List<ID> ids) {
        Criteria findByIdsCriteria = where(ID_COLUMN).in(ids);
        return findAllBy(findByIdsCriteria);
    }

    @Override
    public boolean existsById(ID id) {
        Criteria findByIdCriteria = where(ID_COLUMN).is(id);
        return countBy(findByIdCriteria) > 0;
    }

    @Override
    public D save(D entity) {
        J saved = template.save(toJdbc(entity));
        return toDomain(saved);
    }

    @Override
    public List<D> saveAll(List<D> entities) {
        List<J> saved = template.saveAll(entities.stream().map(this::toJdbc).toList());
        return saved.stream().map(this::toDomain).toList();
    }

    @Override
    public void delete(D entity) {
        J jdbcEntity = toJdbc(entity);
        jdbcEntity.setIsDeleted(true);
        template.save(jdbcEntity);
    }

    @Override
    public void deleteAll(List<D> entities) {
        List<J> jdbcEntity = entities.stream().map(this::toJdbc).toList();
        jdbcEntity.forEach(j -> j.setIsDeleted(true));
        template.saveAll(jdbcEntity);
    }
}
