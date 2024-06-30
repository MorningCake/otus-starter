package ru.morningcake.security.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import ru.morningcake.jwt.JwtToken;

import java.util.Base64;

@UtilityClass
public class SecurityUtils {

  public String getJwtEncoded(JwtToken token, ObjectMapper objectMapper, String key) {
    return getBase64String("{\"typ\":\"JWT\",\"alg\":\"HS256\"}") + "." +
        getBase64String(JwtToken.tokenObjectToJson(token, objectMapper)) + "." + getBase64String(key);
  }

  private String getBase64String(String source) {
    return Base64.getUrlEncoder().encodeToString(source.getBytes());
  }



}
