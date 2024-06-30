package ru.morningcake.security;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.morningcake.jwt.JwtToken;
import ru.morningcake.security.context.TokenContext;

/**
 * Текущий пользователь системы.
 */
@Service
@RequiredArgsConstructor
public class SecurityFacade {

  private final TokenContext context;

  @Nullable
  public JwtToken getToken() {
    return context.getToken();
  }

}
