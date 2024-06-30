package ru.morningcake.security.context;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.morningcake.jwt.JwtToken;

@Service
public class TokenContext {

  private static final ThreadLocal<JwtToken> tokenThreadLocal = new InheritableThreadLocal<>();

  public void setToken(JwtToken currentUser) {
    TokenContext.tokenThreadLocal.set(currentUser);
  }

  @Nullable
  public JwtToken getToken() {
    return TokenContext.tokenThreadLocal.get();
  }

  public void clearContext() {
    TokenContext.tokenThreadLocal.remove();
  }
}