package machinosoccerweb.infra;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Id;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TempCollectionRepository<T, ID extends Serializable> {
  private final AtomicLong ids = new AtomicLong();

  private final List<T> entities = new ArrayList<>();

  private FieldWrapper<T, ID> idValueResolver;

  private FieldWrapper<T, Long> familyIdValueResolver;

  protected TempCollectionRepository() {
    this(Collections.emptyList());
  }

  protected TempCollectionRepository(List<T> entities) {
    entities.forEach(this.entities::add);

    ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();

    @SuppressWarnings("unchecked")
    Class<T> entityClass = (Class<T>) type.getActualTypeArguments()[0];

    Field[] fields = entityClass.getDeclaredFields();
    Optional<Field> idField = Stream.of(fields)
        .filter(f -> f.getAnnotation(Id.class) != null)
        .findFirst();


    idValueResolver = idField
        .map(f -> new FieldWrapper<T, ID>(f))
        .orElseThrow(() -> new RuntimeException("no field found annotated with @Id "));

    log.debug("ID value resolver: {}, {}", idField, idValueResolver);

    Optional<Field> familyIdField = Stream.of(fields)
        .filter(f -> "familyId".equals(f.getName()))
        .findFirst();
    familyIdValueResolver = familyIdField
        .map(f -> new FieldWrapper<T, Long>(f))
        .orElse(new NullFieldWrapper<T, Long>());
  }

  public T findOne(ID id) {
    return entities.stream()
        .filter(e -> id.equals(idValueResolver.get(e)))
        .findFirst()
        .orElse(null);
  }

  public List<T> findAll() {
    return Collections.unmodifiableList(entities);
  }

  public T save(T entity) {
    Optional<ID> id = idValueResolver.getOpt(entity);
    if (id.isPresent()) {
      findIndex(id.get())
          .map(i -> entities.set(i, entity))
          .orElseGet(() -> addEntity(entity));
    } else {
      idValueResolver.set(entity, convert(incrementAndGetId()));
      entities.add(entity);
    }

    return entity;
  }

  public List<T> findByFamilyId(long familyID) {
    return entities.stream()
        .filter(e -> Long.valueOf(familyID).equals(familyIdValueResolver.get(e)))
        .collect(Collectors.toList());
  }

  protected Optional<Integer> findIndex(ID id) {
    for (int i = 0; i < entities.size(); i++) {
      T entity = entities.get(i);
      if (id.equals(idValueResolver.get(entity))) {
        return Optional.of(i);
      }
    }
    return Optional.empty();
  }

  protected long incrementAndGetId() {
    return ids.incrementAndGet();
  }

  protected abstract ID convert(long newId);

  private T addEntity(T entity) {
    entities.add(entity);
    return entity;
  }

  private class FieldWrapper<T, R> {
    private final Field field;

    private FieldWrapper() {
      field = null;
    }

    public FieldWrapper(Field field) {
      this.field = field;
      this.field.setAccessible(true);
    }

    public Optional<R> getOpt(T entity) {
      try {
        @SuppressWarnings("unchecked")
        R ret = (R) field.get(entity);
        return Optional.ofNullable(ret);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    public R get(T entity) {
      return getOpt(entity).orElse(null);
    }

    public void set(T entity, R value) {
      try {
        field.set(entity, value);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private class NullFieldWrapper<T, R> extends FieldWrapper<T, R> {
    @Override
    public Optional<R> getOpt(T entity) {
      return Optional.empty();
    }

    @Override
    public R get(T entity) {
      return null;
    }

    @Override
    public void set(T entity, R value) {
    }
  }
}