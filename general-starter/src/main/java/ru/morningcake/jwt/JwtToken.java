package ru.morningcake.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.morningcake.data.model.base.Role;
import ru.morningcake.data.model.base.Sex;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtToken {
  private UUID id;
  private String firstName;
  private String secondName;
  private LocalDate birthdate;
  private Sex sex;
  private String biography;
  private String city;
  private Set<Role> roles;
  private String username;
  private UUID accessId;
  private Long exp;

  public static String tokenObjectToJson(JwtToken token, ObjectMapper objectMapper) {
    try {
      return objectMapper.writeValueAsString(token);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static JwtToken getJwtTokenFromJson(String jwtData, ObjectMapper objectMapper) {
    JwtToken token;
    try {
      token = objectMapper.readValue(jwtData, JwtToken.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return token;
  }
}
