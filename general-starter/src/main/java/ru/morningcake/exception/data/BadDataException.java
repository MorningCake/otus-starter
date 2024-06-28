package ru.morningcake.exception.data;

import ru.morningcake.exception.BaseException;
import ru.morningcake.exception.BaseStatusCode;

public class BadDataException extends BaseException {
  public static final BaseStatusCode CODE = BaseStatusCode.BAD_DATA_EXCEPTION;

  public BadDataException(String systemMessage) {
    super(CODE.getCode(), systemMessage);
  }

  public BadDataException(String code, String systemMessage) {
    super(code, systemMessage);
  }

  public BadDataException(String code, String systemMessage, Throwable exception) {
    super(code, systemMessage, exception);
  }
}
