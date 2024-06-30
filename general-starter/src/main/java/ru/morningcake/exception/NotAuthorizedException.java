package ru.morningcake.exception;

public class NotAuthorizedException extends BaseException {

  public static final BaseStatusCode CODE = BaseStatusCode.ACCESS_DENIED;

  public NotAuthorizedException(String systemMessage) {
    super(CODE.getCode(), systemMessage);
  }

  public NotAuthorizedException(String systemMessage, Throwable exception) {
    super(CODE.getCode(), systemMessage, exception);
  }
}
