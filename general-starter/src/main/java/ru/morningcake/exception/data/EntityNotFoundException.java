package ru.morningcake.exception.data;

import lombok.Getter;
import ru.morningcake.exception.BaseException;
import ru.morningcake.exception.BaseStatusCode;

public class EntityNotFoundException extends BaseException {

  public static final BaseStatusCode CODE = BaseStatusCode.ENTITY_NOT_FOUND;

  @Getter
  private String entityName;

  public EntityNotFoundException(String systemMessage, String entityName) {
    super(CODE.getCode(), systemMessage);
    this.entityName = entityName;
  }

  public EntityNotFoundException(String code, String systemMessage, String entityName) {
    super(code, systemMessage);
    this.entityName = entityName;
  }

  public EntityNotFoundException(String systemMessage, Throwable exception) {
    super(CODE.getCode(), systemMessage, exception);

  }
}
