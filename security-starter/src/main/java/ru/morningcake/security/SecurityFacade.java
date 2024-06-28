package ru.morningcake.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.morningcake.data.dto.security.CurrentUser;

/**
 * Текущий пользователь системы.
 */
@Service
@RequiredArgsConstructor
public class SecurityFacade {


  public CurrentUser getCurrentUser() {
    return null; // todo
  }

}
