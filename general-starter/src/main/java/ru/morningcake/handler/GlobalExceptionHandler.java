package ru.morningcake.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.morningcake.data.model.base.BaseResponse;
import ru.morningcake.exception.SecurityException;
import ru.morningcake.exception.*;
import ru.morningcake.exception.data.*;

import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.morningcake.exception.BaseStatusCode.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(Error.class)
    protected ResponseEntity<BaseResponse> handleAllErrors(Error error) {
        log.error("Internal Error", error);
        return ResponseEntity.internalServerError()
                .body(new BaseResponse()
                        .reason(INTERNAL_ERROR.name())
                        .code(INTERNAL_ERROR.getCode())
                        .success(false));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<BaseResponse> handleAllException(Exception exception) {
        return ResponseEntity.internalServerError().body(logAndGetResponse(exception, INTERNAL_ERROR));
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<BaseResponse> handleAccessDeniedException(Exception exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body((logAndGetResponse(exception, ACCESS_DENIED)));
    }

    @ExceptionHandler(SecurityException.class)
    protected ResponseEntity<BaseResponse> handleSecurityException(SecurityException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body((logAndGetResponse(exception, ACCESS_DENIED)));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<BaseResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body((logAndGetResponse(exception, ENTITY_NOT_FOUND)));
    }

    @ExceptionHandler(BadDataException.class)
    protected ResponseEntity<BaseResponse> handleBadDataException(BaseException exception) {
        return ResponseEntity
                .badRequest()
                .body(logAndGetResponse(exception, BaseStatusCode.getConstByCode(exception.getCode())));
    }

    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<BaseResponse> handleFeignException(FeignException ex) {
        return logAndGetResponseFromFeignException(ex);
    }

    @ExceptionHandler(NotImplementedException.class)
    protected ResponseEntity<BaseResponse> handleNotImplementedException(BaseException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_IMPLEMENTED)
                .body(logAndGetResponse(exception, BaseStatusCode.getConstByCode(exception.getCode())));
    }

    @ExceptionHandler(MethodNotAllowedException.class)
    protected ResponseEntity<BaseResponse> handleMethodNotAllowedException(BaseException exception) {
        return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(logAndGetResponse(exception, BaseStatusCode.getConstByCode(exception.getCode())));
    }

    @ExceptionHandler(EntityAlreadyExist.class)
    protected ResponseEntity<BaseResponse> handleDuplicateException(BaseException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(logAndGetResponse(exception, DUPLICATE));
    }

    @ExceptionHandler(SystemException.class)
    protected ResponseEntity<BaseResponse> handleSystemException(BaseException exception) {
        return ResponseEntity
                .internalServerError()
                .body(logAndGetResponse(exception, BaseStatusCode.getConstByCode(exception.getCode())));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ExceptionBody> handleConstraintValidationException(ConstraintViolationException ex) {
        List<Problem> problems = new ArrayList<>(ex.getConstraintViolations().size());
        ex.getConstraintViolations().forEach(cv ->
                problems.add(
                        Problem.builder()
                                .message(cv.getMessage())
                                .invalidValue(cv.getInvalidValue().toString())
                                .propertyPath(cv.getPropertyPath().toString())
                                .build()
                )
        );
        return ResponseEntity.badRequest().body(
                ExceptionBody.builder()
                        .problems(problems)
                        .count(problems.size())
                        .code(BaseStatusCode.BAD_DATA_EXCEPTION.getCode())
                        .reason(BaseStatusCode.BAD_DATA_EXCEPTION.name())
                        .build()
        );
    }

    @Override
    protected @NonNull
    ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            HttpStatus status,
            @NonNull WebRequest request
    ) {
        var model = ex.getBindingResult().getModel();
        List<Problem> problems = new ArrayList<>();

        for (var key : model.keySet()) {
            var value = model.get(key);
            if (value instanceof BeanPropertyBindingResult) {
                var result = (BeanPropertyBindingResult) value;
                for (var error : result.getAllErrors()) {
                    if (error instanceof FieldError) {
                        var fieldError = (FieldError) error;
                        problems.add(
                                Problem.builder()
                                        .propertyPath(fieldError.getField())
                                        .invalidValue(
                                                fieldError.getRejectedValue() == null
                                                        ? "null"
                                                        : fieldError.getRejectedValue().toString())
                                        .message(fieldError.getDefaultMessage())
                                        .build()
                        );
                    } else if (error instanceof ObjectError) {
                        var fieldsNamesBuilder = new StringBuilder();
                        for (var argument : Objects.requireNonNull(error.getArguments())) {
                            if (argument.getClass().getName().equals("org.springframework.validation.beanvalidation" +
                                    ".SpringValidatorAdapter$ResolvableAttribute")) {
                                var fieldName = argument.toString();
                                fieldsNamesBuilder.append(fieldName).append(" - ");
                            }
                        }
                        var problem = Problem.builder()
                                .propertyPath(fieldsNamesBuilder.substring(0, fieldsNamesBuilder.length() - 3))
                                .message(error.getDefaultMessage())
                                .invalidValue(error.getCode())
                                .build();
                        problems.add(problem);
                    }
                }
            }
        }
        return ResponseEntity.badRequest().body(
                ExceptionBody.builder()
                        .problems(problems)
                        .count(problems.size())
                        .code(BaseStatusCode.BAD_DATA_EXCEPTION.getCode())
                        .reason(BaseStatusCode.BAD_DATA_EXCEPTION.name())
                        .build()
        );
    }

    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<ExceptionBody> handleCustomValidationException(CustomValidationException ex) {
        log.error("Error", ex);
        return ResponseEntity.badRequest().body(
                ExceptionBody.builder()
                        .problems(ex.getProblems())
                        .count(ex.getProblems().size())
                        .code(CustomValidationException.CODE.getCode())
                        .reason(CustomValidationException.CODE.name())
                        .build()
        );
    }

    private BaseResponse logAndGetResponse(Throwable exception, BaseStatusCode statusCode) {
        log.error("Error", exception);
        return new BaseResponse()
                .code(statusCode.getCode())
                .reason(statusCode.name())
                .errorUserMessage(exception.getMessage())
//                .stackTrace(Arrays.toString(exception.getStackTrace()))
                .success(false);
    }

    private ResponseEntity<BaseResponse> logAndGetResponseFromFeignException(FeignException feignException) {
        log.error("Error", feignException);
        var notImplementedCaseResponse = ResponseEntity
            .status(HttpStatus.NOT_IMPLEMENTED)
            .body(
                new BaseResponse()
                    .code(INTERNAL_ERROR.getCode())
                    .reason(FEIGN.name())
                    .errorUserMessage("Вызов на сервис завершен неизвестной ошибкой")
                    .success(false)
            );
        var optionalRB = feignException.responseBody();
        if (optionalRB.isPresent()) {
            try {
                var responseBody = objectMapper.readValue(new String(optionalRB.get().array()), BaseResponse.class);
                return ResponseEntity
                    .status(feignException.status())
                    .body(
                        new BaseResponse()
                            .code(responseBody.getCode())
                            .reason(FEIGN.name())
                            .errorUserMessage(responseBody.getErrorUserMessage())
                            .success(responseBody.getSuccess())
                    );
            } catch (JsonProcessingException e) {
                return notImplementedCaseResponse;
            }
        } else {
            return notImplementedCaseResponse;
        }
    }
}
