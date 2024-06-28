package ru.morningcake.exception;

public class SecurityException extends BaseException {

  public static final BaseStatusCode CODE = BaseStatusCode.ACCESS_DENIED;

  public SecurityException(String systemMessage) {
    super(CODE.getCode(), systemMessage);
  }

  public SecurityException(String systemMessage, Throwable exception) {
    super(CODE.getCode(), systemMessage, exception);
  }
}
