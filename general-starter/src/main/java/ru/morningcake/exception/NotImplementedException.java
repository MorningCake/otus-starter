package ru.morningcake.exception;

/**
 * Исключение, выбрасываемое в случае не реализованного метода
 */
public class NotImplementedException extends BaseException {

    public NotImplementedException(String code, String systemMessage) {
        super(code, systemMessage);
    }

    public NotImplementedException(String code, String systemMessage, Throwable exception) {
        super(code, systemMessage, exception);
    }
}
