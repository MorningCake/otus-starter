package ru.morningcake.exception.data;

import ru.morningcake.exception.BaseException;
import ru.morningcake.exception.BaseStatusCode;

public class EntityAlreadyExist extends BaseException {
  public static final BaseStatusCode CODE = BaseStatusCode.DUPLICATE;

  public EntityAlreadyExist(String systemMessage) {
    super(CODE.getCode(), systemMessage);
  }

  public EntityAlreadyExist(String systemMessage, BaseStatusCode statusCode) {
    super(statusCode.getCode(), systemMessage);
  }

  public EntityAlreadyExist(String systemMessage, Throwable exception) {
    super(CODE.getCode(), systemMessage, exception);
  }
}
