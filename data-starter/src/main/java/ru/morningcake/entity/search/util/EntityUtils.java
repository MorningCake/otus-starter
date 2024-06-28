package ru.morningcake.entity.search.util;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import ru.morningcake.entity.BaseEntity;
import ru.morningcake.exception.BaseStatusCode;
import ru.morningcake.exception.SystemException;
import ru.morningcake.exception.data.BadDataException;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class EntityUtils {

  /** Получить ID (UUID) Nullable сущности или null */
  @Nullable
  public static <T extends BaseEntity> UUID getNullableBaseEntityId(@Nullable T entity) {
    return entity == null ? null : entity.getId();
  }

  /** Получить ID (UUID) Nullable сущности или null через рефлексию. Сущность - не наследник BaseEntity, но имеет поле id */
  @Nullable
  public static <T> UUID getNullableNotBaseEntityId(@Nullable T entity) {
    if (entity == null) return null;
    try {
      Field idField = ReflectionUtils.findField(entity.getClass(), "id");
      if (idField == null) {
        throw new BadDataException(BaseStatusCode.BAD_DATA_EXCEPTION.getCode(), "В сущности нет поля 'id'!");
      }
      idField.setAccessible(true);
      return (UUID) idField.get(entity);
    } catch (IllegalAccessException e) {
      throw new SystemException(BaseStatusCode.INTERNAL_ERROR.getCode(), "Ошибка в доступе к полю id через рефлексию!");
    }
  }

  /**
   * Проверка того, нужно ли обновление nullable сущности для наследников BaseEntity
   * @param entity сущность или null
   * @param updatedId новое значение ID или null
   * @param <T> наследник BaseEntity
   */
  public static <T extends BaseEntity> boolean isNullableBaseEntityNeedToUpdate(@Nullable T entity, @Nullable UUID updatedId) {
    if (entity == null && updatedId == null) { return false; }
    else if (entity == null || updatedId == null) { return true; }
    else {
      return !Objects.requireNonNull(entity.getId()).equals(updatedId);
    }
  }

  /**
   * Проверка того, нужно ли обновление nullable сущности для не наследников BaseEntity, но имеющих поле id
   * @param entity сущность или null
   * @param updatedId новое значение ID или null
   * @param <T> класс, у которого точно есть поле id
   */
  public static <T> boolean isNullableNotBaseEntityNeedToUpdate(@Nullable T entity, @Nullable UUID updatedId) {
    if (entity == null && updatedId == null) {
      return false;
    } else if (entity == null || updatedId == null) {
      return true;
    } else {
      try {
        Field idField = ReflectionUtils.findField(entity.getClass(), "id");
        if (idField == null) {
          throw new BadDataException(BaseStatusCode.BAD_DATA_EXCEPTION.getCode(), "В сущности нет поля 'id'!");
        }
        idField.setAccessible(true);
        UUID id = (UUID) idField.get(entity);
        return !Objects.requireNonNull(id).equals(updatedId);
      } catch (IllegalAccessException e) {
        throw new SystemException(BaseStatusCode.INTERNAL_ERROR.getCode(), "Ошибка в доступе к полю id через рефлексию!");
      }
    }
  }

  /** Вернуть элемент из коллекции наследников BaseEntity по его ID */
  public static <T extends BaseEntity> Optional<T> getById(Collection<T> entities, UUID id) {
    return entities.stream().filter(e -> e.getId().equals(id)).findFirst();
  }

  /** Получить список ID из коллекции сущностей, унаследованных от BaseEntity */
  public static <T extends BaseEntity> List<UUID> getIdsListFromEntities(Collection<T> entities) {
    return entities.stream().map(BaseEntity::getId).collect(Collectors.toList());
  }

  /** Получить сет ID из коллекции сущностей, унаследованных от BaseEntity */
  public static <T extends BaseEntity> Set<UUID> getIdsSetFromEntities(Collection<T> entities) {
    return entities.stream().map(BaseEntity::getId).collect(Collectors.toSet());
  }

}
