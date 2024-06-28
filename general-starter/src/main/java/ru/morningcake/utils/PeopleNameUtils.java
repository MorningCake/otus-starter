package ru.morningcake.utils;

import lombok.experimental.UtilityClass;

import java.util.Locale;

/**
 * Утилитный класс для преобразования ФИО в репрезентативный вид
 */
@UtilityClass
public class PeopleNameUtils {

  /**
   * Null safe преобразование ФИО к строке формата: Фамилия Имя Отчество
   * @param lastName - фамилия
   * @param firstName - имя
   * @param patronymicName - отчество
   * @return Фамилия Имя Отчество
   */
  public String getFullName(String lastName, String firstName, String patronymicName) {
    StringBuilder sb = new StringBuilder();
    if (lastName != null) {
      sb.append(lastName);
    }
    if (firstName != null) {
      sb.append(" ").append(firstName);
    }
    if (patronymicName != null) {
      sb.append(" ").append(patronymicName);
    }
    return sb.toString();
  }

  /**
   * Null safe преобразование ФИО к строке формата: ФАМИЛИЯ И.О.
   * @param lastName - фамилия
   * @param firstName - имя
   * @param patronymicName - отчество
   * @return ФАМИЛИЯ И.О.
   */
  public String getFIO(String lastName, String firstName, String patronymicName) {
    StringBuilder sb = new StringBuilder();
    if (lastName != null) {
      sb.append(lastName.toUpperCase(Locale.ROOT));
    }
    if (firstName != null) {
      sb.append(" ").append(firstName.substring(0, 1).toUpperCase(Locale.ROOT)).append(".");
    }
    if (patronymicName != null) {
      sb.append(" ").append(patronymicName.substring(0, 1).toUpperCase(Locale.ROOT)).append(".");
    }
    return sb.toString();
  }
}