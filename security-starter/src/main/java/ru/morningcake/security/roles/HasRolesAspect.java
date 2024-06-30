package ru.morningcake.security.roles;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.morningcake.data.model.base.BaseResponse;
import ru.morningcake.data.model.base.Role;
import ru.morningcake.exception.BaseStatusCode;
import ru.morningcake.exception.SecurityException;
import ru.morningcake.jwt.JwtToken;
import ru.morningcake.security.SecurityFacade;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import static ru.morningcake.exception.BaseStatusCode.ACCESS_DENIED;

@Aspect
@Order(1) // запуск перед controller advice
@Component
@RequiredArgsConstructor
public class HasRolesAspect {

  private final SecurityFacade securityFacade;

  @Before(value = "@annotation(ru.morningcake.security.roles.HasRoles)")
  public void auditException(JoinPoint joinPoint) {
    final Class<?> clazz = joinPoint.getSignature().getDeclaringType();
    HasRoles annotation = getAnnotation(joinPoint, clazz);
    Role[] needRoles = annotation.roles();
    JwtToken token = securityFacade.getToken();
    if (token != null) {
      Set<Role> tokenRoles = token.getRoles();
      StringBuilder builder = new StringBuilder();
      for (Role need : needRoles) {
        if (!tokenRoles.contains(need)) builder.append(need.name()).append(" ");
      }
      if (builder.length() > 0) throw new SecurityException("Need roles: " + builder);
    } else {
      throw new SecurityException("Need authorization");
    }
  }

  private HasRoles getAnnotation(JoinPoint joinPoint, Class<?> clazz) {
    Method[] methods = ReflectionUtils.getDeclaredMethods(clazz);
    Method method = Arrays.stream(methods)
        .filter(met -> met.getName().equals(joinPoint.getSignature().getName()))
        .findAny()
        .get();
    return method.getAnnotation(HasRoles.class);
  }

  @RestControllerAdvice
  @Order(3)
  public static class WebAdvice {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<BaseResponse> handle(SecurityException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body((logAndGetResponse(e, ACCESS_DENIED)));
    }

    private BaseResponse logAndGetResponse(Throwable exception, BaseStatusCode statusCode) {
      return new BaseResponse()
          .code(statusCode.getCode())
          .reason(statusCode.name())
          .errorUserMessage(exception.getMessage())
          .success(false);
    }
  }
}
