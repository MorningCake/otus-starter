package ru.morningcake.exception;

import lombok.Getter;

public class BaseException extends RuntimeException {
  @Getter
  private final String code;

  @Getter
  private final String systemMessage;

  public BaseException(String code, String systemMessage) {
    super(String.format("%s - %s", code, systemMessage));
    this.code = code;
    this.systemMessage = systemMessage;
  }

  public BaseException(String code, String systemMessage, Throwable exception) {
    super(systemMessage, exception);
    this.code = code;
    this.systemMessage = systemMessage;
  }
}
