package ru.morningcake.exception;

public class SystemException extends BaseException {
  public SystemException(String code, String systemMessage) {
    super(code, systemMessage);
  }

  public SystemException(String code, String systemMessage, Throwable exception) {
    super(code, systemMessage, exception);
  }
}
