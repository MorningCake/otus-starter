package ru.morningcake.exception.data;

import lombok.Getter;
import ru.morningcake.exception.BaseException;
import ru.morningcake.exception.BaseStatusCode;
import ru.morningcake.handler.Problem;

import java.util.List;

public class CustomValidationException extends BaseException {
	@Getter
	private List<Problem> problems;
	public static final BaseStatusCode CODE = BaseStatusCode.BAD_DATA_EXCEPTION;

	public CustomValidationException(String systemMessage, List<Problem> problems) {
		super(CODE.getCode(), systemMessage);
		this.problems = problems;
	}

	public CustomValidationException(String systemMessage, Throwable exception) {
		super(CODE.getCode(), systemMessage, exception);
	}
}
