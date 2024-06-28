package ru.morningcake.utils;

import lombok.*;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** Утилитный класс-хэлпер для CRUD-операций с БД */
@UtilityClass
public class CrudUtils {

  /** Проверка на эквивалентность двух коллекций */
  public <T> boolean checkCollectionsEquals (@Nullable Collection<T> first, @Nullable Collection<T> second) {
    Set<T> firstSet = convertCollectionToSet(first);
    Set<T> secondSet = convertCollectionToSet(second);
    return checkSetsEquals(firstSet, secondSet);
  }

  /** Проверка на эквивалентность двух множеств */
  public <T> boolean checkSetsEquals (@Nullable Set<T> first, @Nullable Set<T> second) {
    if (first == null && second == null) { return true; }
    else if (first == null || second == null) { return false; }
    else if (first.isEmpty() && second.isEmpty()) { return true; }
    else if (first.isEmpty() || second.isEmpty()) { return false; }
    else { return (first.containsAll(second) && second.containsAll(first)); }
  }

  /** При операции UPDATE - вычислить по сохраненному и обновляемому спискам, какие элементы необходимо сохранить, обновить и удалить */
  public <T> UpdateSets<T> calculateActionsToUpdateEntitySet(@Nullable Set<T> updated, @Nullable Set<T> old) {
    UpdateSets<T> updateResult = new UpdateSets<>();
    if (checkSetsEquals(updated, old) || (updated == null && isSetNotNullAndEmpty(old)) ||
        (old == null && isSetNotNullAndEmpty(updated)) ) {
      updateResult.setForUpdate(updated == null ? Set.of() : updated);
    } else if (isSetNullOrEmpty(old)) {
      updateResult.setForCreate(updated);
    } else if (isSetNullOrEmpty(updated)) {
      updateResult.setForDelete(old);
    } else {
      Objects.requireNonNull(updated);
      Objects.requireNonNull(old);
      // для создания - те которые есть в updated, но нет в old
      updateResult.setForCreate( updated.stream().filter(u -> !old.contains(u)).collect(Collectors.toSet()) );
      // для обновления - те которые есть в и updated, и в old
      updateResult.setForUpdate( updated.stream().filter(old::contains).collect(Collectors.toSet()) );
      // для удаления - те которые есть в old, но нет в updated
      updateResult.setForDelete( old.stream().filter(o -> !updated.contains(o)).collect(Collectors.toSet()) );
    }
    return updateResult;
  }

  /** Проверка, что Set не null, но пуст */
  public <T> boolean isSetNotNullAndEmpty(@Nullable Set<T> set) {
    return set != null && set.isEmpty();
  }

  /** Проверка, что Set null или пуст */
  public <T> boolean isSetNullOrEmpty(@Nullable Set<T> set) {
    return set == null || set.isEmpty();
  }

  /** Класс, содержащий массивы для сохранения, обновления и удаления */
  @Getter @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class UpdateSets<T> {
    private Set<T> forCreate = Set.of();
    private Set<T> forUpdate = Set.of();
    private Set<T> forDelete = Set.of();
  }

  @Nullable
  private <T> Set<T> convertCollectionToSet(@Nullable Collection<T> first) {
    Set<T> firstSet = null;
    if (!CollectionUtils.isEmpty(first)) {
      firstSet = new HashSet<>(first);
    }
    return firstSet;
  }
}
