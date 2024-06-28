package ru.morningcake.data.dto.security;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Текущий пользователь системы
 */
@Data
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
public class CurrentUser {

  private String employeeNumber;
  private String login;
  private String email;
  private String firstName;
  private String lastName;
  private String middleName;
  private String userId;

  /** Роли */
  private Set<String> realmRoles;

  /** Не используются */
  private Map<String, Set<String>> resourceRoles;

  /** Время протухания токена */
  private String expiredTime;

  /** Сообщение для логирования ошибки */
  private String errorMessage;

  public boolean containsRealmRole(String role) {

    return realmRoles.contains(role);
  }

  public boolean containsOneOfRealmRole(String[] role) {
    var roleList = Arrays.asList(role);
    return realmRoles.stream()
        .anyMatch(roleList::contains);
  }

  public boolean containsResourceRoles(String role) {
    return resourceRoles.containsKey(role);
  }

  public boolean containsOneOfResourceRole(String[] role) {
    var roleList = Arrays.asList(role);
    return resourceRoles.values().stream()
        .flatMap(Set::stream)
        .anyMatch(roleList::contains);
  }

  public Set<String> getAccessForRorResourceRole(String resourceRole) {
    var resourceAccess = resourceRoles.get(resourceRole);
    if (resourceAccess == null) {
      throw new NullPointerException("Values with this key does not contains");
    }
    return  resourceAccess;
  }
}
