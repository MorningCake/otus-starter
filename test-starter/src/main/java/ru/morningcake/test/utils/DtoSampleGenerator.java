package ru.morningcake.test.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

/**
 * Генератор ДТО для тестов. Структуры и типы данных - в рамках спеки OpenApi 3.0, заполненного образцами данных.
 * Заполняет UUID, Number, String, даты, BigDecimal, Boolean, Character, вложенные DTO (кроме циклических ссылок), List, енамы.
 * Не поддерживает структуру типа List of List.
 * Имя пакета служит для того, чтобы отделить DTO от неподдерживаемых генератором типов.
 */
@UtilityClass
public class DtoSampleGenerator {

  private final UUID _UUID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  private final String DEFAULT_PACKAGE_NAME = "ru.morningcake";

  private final Map<Class<?>, Object> simpleTypesAndValues = new HashMap<>(16);

  static {
    simpleTypesAndValues.put(UUID.class, _UUID);
    simpleTypesAndValues.put(Date.class, Date.from(Instant.parse("2022-01-01T12:00:00.00Z")));
    simpleTypesAndValues.put(LocalDate.class, LocalDate.of(2022, 1, 1));
    simpleTypesAndValues.put(LocalDateTime.class, LocalDateTime.of(2022, 1, 1, 12, 0));
    simpleTypesAndValues.put(ZonedDateTime.class, ZonedDateTime.ofInstant(Instant.parse("2022-01-01T12:00:00.00Z"), ZoneId.systemDefault()));
    simpleTypesAndValues.put(OffsetDateTime.class, OffsetDateTime.ofInstant(Instant.parse("2022-01-01T12:00:00.00Z"), ZoneId.systemDefault()));
    simpleTypesAndValues.put(Byte.class, 0b0001);
    simpleTypesAndValues.put(Short.class, 2);
    simpleTypesAndValues.put(Integer.class, 3);
    simpleTypesAndValues.put(Long.class, 4L);
    simpleTypesAndValues.put(Double.class, 1.1);
    simpleTypesAndValues.put(Float.class, 2.2f);
    simpleTypesAndValues.put(BigDecimal.class, BigDecimal.TEN);
    simpleTypesAndValues.put(Boolean.class, true);
    simpleTypesAndValues.put(Character.class, 'a');
    simpleTypesAndValues.put(String.class, "string");
  }

  /**
   * Сгенерировать DTO с указанием пакета, где он лежит
   * @param dtoClazz тип DTO
   * @param dtoPackageName пакет, где лежит DTO
   */
  @SneakyThrows
  public <T> T generateDto(@NonNull Class<T> dtoClazz, @NonNull String dtoPackageName) {
    return generate(dtoClazz, dtoPackageName);
  }

  /**
   * Сгенерировать DTO с пакетом по умолчанию - ru.morningcake
   * @param dtoClazz тип DTO
   */
  @SneakyThrows
  public <T> T generateDto(@NonNull Class<T> dtoClazz) {
    return generate(dtoClazz, DEFAULT_PACKAGE_NAME);
  }

  @SneakyThrows
  private <T> T generate(Class<T> dtoClazz, @Nullable String dtoPackageName) {
    T instance = dtoClazz.getConstructor().newInstance();
    List<Field> instanceFields = Arrays.asList(instance.getClass().getDeclaredFields());
    instanceFields.forEach(f -> f.setAccessible(true));
    instanceFields.forEach(f -> setFieldSampleToInstance(instance, f, dtoPackageName == null ? DEFAULT_PACKAGE_NAME : dtoPackageName));
    return instance;
  }

  @SneakyThrows
  private <T> void setFieldSampleToInstance(T instance, Field instanceField, String dtoPackageName) {
    Class<?> type = instanceField.getType();

    //примитивы
    if (simpleTypesAndValues.containsKey(type))  {
      if (type == Byte.class) {
        instanceField.set(instance, ((Integer) simpleTypesAndValues.get(type)).byteValue());
      } else if (type == Short.class) {
        instanceField.set(instance, ((Integer) simpleTypesAndValues.get(type)).shortValue());
      } else {
        instanceField.set(instance, simpleTypesAndValues.get(type));
      }
    }
    // enum
    else if (type.isEnum()) {
      instanceField.set(instance, type.getEnumConstants()[0]);
    }
    // список - рекурсивный вызов метода для каждого с записью (гет и add)
    else if (type.equals(List.class)) {
      ParameterizedType generic = (ParameterizedType) instanceField.getGenericType();
      Class<?> genericType = (Class<?>) generic.getActualTypeArguments()[0];

      if (simpleTypesAndValues.containsKey(genericType)) {
        if (genericType == Byte.class) {
          instanceField.set(instance, List.of(((Integer) simpleTypesAndValues.get(genericType)).byteValue()));
        } else if (genericType == Short.class) {
          instanceField.set(instance, List.of(((Integer) simpleTypesAndValues.get(genericType)).shortValue()));
        } else {
          instanceField.set(instance, List.of(simpleTypesAndValues.get(genericType)));
        }
      }
      else if (genericType.isEnum()) {
        instanceField.set(instance, List.of(genericType.getEnumConstants()[0]));
      }

      // todo если List<List<>> - ошибка, разобрать (через сигнатуру дженериков м.б скинуть в неподдерживаемые типы полей)
//      else if (genericType.equals(List.class)) {
//      }

      // дтошка - фильтр по package ru.morningcake - рекурсивный вызов
      else if (genericType.getPackageName().contains(dtoPackageName)) {
        // во избежание бесконечной рекурсии - null если класс инстанса совпал с типом дженерика (т.к. циклическая ссылка не может быть обязательным полем)
        if (genericType == instance.getClass()) {
          instanceField.set(instance, null);
        } else {
          instanceField.set(instance, List.of(generate(genericType, dtoPackageName)));
        }
      } else {
        // не разобранные в кейсах типы полей не поддерживаются, что достаточно в рамках DTO OpenApi3.0
        instanceField.set(instance, null);
      }
    }
    // дтошка - рекурсивный вызов и сет (фильтр по package ru.morningcake )
    else if (type.getPackageName().contains(dtoPackageName)) {
      // во избежание бесконечной рекурсии - null если класс инстанса совпал с типом дженерика (т.к. циклическая ссылка не может быть обязательным полем)
      if (type == instance.getClass()) {
        instanceField.set(instance, null);
      } else {
        instanceField.set(instance, generate(type, dtoPackageName));
      }
    } else {
      // не разобранные в кейсах типы полей не поддерживаются, что достаточно в рамках DTO OpenApi3.0
      instanceField.set(instance, null);
    }
  }
}
