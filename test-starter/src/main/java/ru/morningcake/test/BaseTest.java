package ru.morningcake.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Базовый класс для всех тестов приложения.
 *
 * @author m.gromov
 * @version 1.0
 * @since 1.0.0
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public abstract class BaseTest {

  protected String asJsonString(final Object obj) {
    try {
      ObjectMapper objectMapper = JsonMapper.builder()
          .addModule(new JavaTimeModule())
          .build();

      return objectMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected <T> T asObject(final String jsonString, Class<T> returnType) throws JsonProcessingException {
    ObjectMapper objectMapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .build();
    objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
    return objectMapper.readValue(jsonString, returnType);
  }

  protected <T> T asObject(final String jsonString, TypeReference<T> returnType) throws JsonProcessingException {
    if (StringUtils.isNotBlank(jsonString)) {
      ObjectMapper objectMapper = JsonMapper.builder()
          .addModule(new JavaTimeModule())
          .build();
      objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
      return objectMapper.readValue(jsonString, returnType);
    } else {
      return null;
    }
  }

  private String asString(Resource resource) throws IOException {
    try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
      return FileCopyUtils.copyToString(reader);
    }
  }

  /**
   * Берем файл из resource и получаем его как строку
   *
   * @param path имя файла
   * @return строка
   * @throws IOException если произошла ошибка во время чтения
   */
  protected String readFileAsString(String path) throws IOException {
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource resource = resourceLoader.getResource(path);
    return asString(resource);
  }

  /** Сравнение двух списков UUID */
  public static void assertEqualsUuidCollections(@Nullable Collection<UUID> expected, @Nullable Collection<UUID> actual) {
    if (expected != null && expected.isEmpty()) { expected = null; }
    if (actual != null && actual.isEmpty()) { actual = null; }
    if (actual == null && expected == null) { return; }
    else if ( (actual == null) || (expected == null) ) {
      throw new AssertionError("Одна коллекция равна null, тогда как другая - нет!");
    }
    else {
      Objects.requireNonNull(expected);
      assertEquals(expected.size(), actual.size(), "Коллекции не равны по размеру!");
      for (UUID actualId : actual) {
        expected.stream()
            .filter(expectedId -> expectedId.equals(actualId)).findFirst().orElseThrow(() ->
                new AssertionError("Актуальный элемент id " + actualId + " отсутствует в ожидаемых!"));
      }
    }
  }

  /** Сравнение по ID двух списков. У списков должно быть поле id типа UUID */
  public static void assertEqualsExpectedAndActualCollectionsByIds(@Nullable Collection<?> expected,
                                                                   @Nullable Collection<?> actual) {
    if (expected != null && expected.isEmpty()) { expected = null; }
    if (actual != null && actual.isEmpty()) { actual = null; }
    if (actual == null && expected == null) { return; }
    else if ( (actual == null) || (expected == null) ) {
       throw new AssertionError("Одна коллекция равна null, тогда как другая - нет!");
    }
    else {
      Objects.requireNonNull(expected);
      assertEquals(expected.size(), actual.size(), "Коллекции не равны по размеру!");
      for (Object act : actual) {
        final UUID actualId = getIdByReflection(act);
        expected.stream()
            .filter(ex -> getIdByReflection(ex).equals(actualId)).findFirst().orElseThrow(() ->
                new AssertionError("Актуальный элемент id " + actualId + " отсутствует в ожидаемых!"));
      }
    }
  }

  /** Получить значение поля id рефлексией */
  private static UUID getIdByReflection(Object object) {
    Class<?> clazz = object.getClass();
    try {
      Field id = ReflectionUtils.findField(clazz, "id");
      if (id == null) {
        throw new RuntimeException("Поле id отсутствует у объекта " + object + "!");
      }
      id.setAccessible(true);
      return (UUID) id.get(object);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("К полю id объекта " + object + " нет доступа!");
    }
  }

}
