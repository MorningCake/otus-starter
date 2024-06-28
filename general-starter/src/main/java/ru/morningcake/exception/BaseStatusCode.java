package ru.morningcake.exception;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Коды ошибок системы
 */
public enum BaseStatusCode {
  ENTITY_NOT_FOUND("HL-0001"),
  DUPLICATE("HL-0002"),
  BAD_DATA_EXCEPTION("HL-0003"),
  INTERNAL_ERROR("HL-0004"),
  ACCESS_DENIED("HL-0005"),
  BAD_PROPERTY_EXCEPTION("HL-0006"),
  NOT_IMPLEMENTED("HL-0007"),
  FEIGN("HL-0008");

  private final String code;

  BaseStatusCode(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static BaseStatusCode getConstByCode(String code) {
    List<BaseStatusCode> enums = EnumSet.allOf(BaseStatusCode.class)
            .stream()
            .filter(e -> e.getCode().equals(code))
            .collect(Collectors.toList());
    if (enums.size() > 1) {
      throw new SystemException(BaseStatusCode.INTERNAL_ERROR.getCode(), "BaseStatusCode error: duplicate codes!");
    } else if (enums.size() == 0) {
      throw new SystemException(BaseStatusCode.INTERNAL_ERROR.getCode(), "BaseStatusCode error: code is not found!");
    }
    return enums.get(0);
  }
}
