package ru.morningcake.exception.data;

import ru.morningcake.exception.BaseException;
import ru.morningcake.exception.BaseStatusCode;

public class MethodNotAllowedException extends BaseException {
  public static final BaseStatusCode CODE = BaseStatusCode.NOT_IMPLEMENTED;

  public MethodNotAllowedException(String systemMessage) {
    super(CODE.getCode(), systemMessage);
  }

  public MethodNotAllowedException(String systemMessage, Throwable exception) {
    super(CODE.getCode(), systemMessage, exception);
  }
}
