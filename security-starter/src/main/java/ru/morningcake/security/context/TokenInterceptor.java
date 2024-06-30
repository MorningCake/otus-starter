package ru.morningcake.security.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import ru.morningcake.jwt.JwtToken;
import ru.morningcake.utils.Const;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;

@Service
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

  private final TokenContext tokenContext;
  private final ObjectMapper objectMapper;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws JsonProcessingException {
    Enumeration<String> headers = request.getHeaders(Const.SEC_CONTEXT_HEADER);
    if (headers.hasMoreElements()) {
      String securityContext = headers.nextElement();
      String jwtData = new String(Base64.getUrlDecoder().decode(securityContext), StandardCharsets.UTF_8);
      JwtToken token = objectMapper.readValue(jwtData, JwtToken.class);
      tokenContext.setToken(token);
    }
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
    tokenContext.clearContext();
  }
}
