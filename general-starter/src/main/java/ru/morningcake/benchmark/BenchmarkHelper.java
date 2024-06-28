package ru.morningcake.benchmark;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Помощник для отладки программы при разработке. Измерения по памяти носят приблизительный характер
 */
@Getter
public class BenchmarkHelper {
  /** Контрольные точки */
  private final Map<String, ControlPoint> controlPoints = new HashMap<>();
  /** Форматтер для вывода в логи */
  private final DecimalFormat formatter = new DecimalFormat("0.00",DecimalFormatSymbols.getInstance(Locale.ENGLISH));


  /**
   * Сохранить контрольную точку
   */
  public void saveControlPoint(String name) {
    controlPoints.put(
        name,
        ControlPoint.builder()
            .name(name)
            .timeMills(System.currentTimeMillis())
            .usedMemoryBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
            .build()
    );
  }

  /**
   * Вывести в консоль разницу по времени между двумя точками (в секундах)
   */
  public void printTimeDiffInSeconds(String pointFromName, String pointToName) {
    ControlPoint pointFrom = getControlPoint(pointFromName);
    ControlPoint pointTo = getControlPoint(pointToName);
    System.out.printf(
        "Между точками %s и %s прошло %.3f сек.%n" , pointFromName, pointToName,
             ((double) Math.abs(pointTo.timeMills - pointFrom.timeMills) / 1000)
    );
  }

  /**
   * Получить разницу по времени между двумя точками (в миллисекундах)
   */
  public long getTimeDiffInMills(String pointFromName, String pointToName) {
    ControlPoint pointFrom = getControlPoint(pointFromName);
    ControlPoint pointTo = getControlPoint(pointToName);
    return Math.abs(pointTo.timeMills - pointFrom.timeMills);
  }

  /**
   * Получить разницу по времени между двумя точками секундах, в виде форматированной строки (#.##sec)
   */
  public String getTimeDiffInSec(String pointFromName, String pointToName) {
    ControlPoint pointFrom = getControlPoint(pointFromName);
    ControlPoint pointTo = getControlPoint(pointToName);
    return formatter.format((double) Math.abs(pointTo.timeMills - pointFrom.timeMills) / 1000) + " sec";
  }

  /**
   * Получить используемую память в Мб в виде форматированной строки (#.##Mb)
   */
  public String getUsedMemoryInMB(String pointName) {
    ControlPoint point = getControlPoint(pointName);
    return formatter.format((double) point.usedMemoryBytes / (1024 * 1024)) + "Mb";
  }

  /**
   * Получить используемую память в точке (в МБ)
   */
  public double getPointUsedMemoryInMB(String pointName) {
    ControlPoint point = getControlPoint(pointName);
    return ((double) point.usedMemoryBytes / (1024 * 1024));
  }

  /**
   * Вывести в консоль используемую память в точке (в МБ)
   */
  public void printPointUsedMemoryInMB(String pointName) {
    ControlPoint point = getControlPoint(pointName);
    System.out.printf("Используемая память в точке %s: %.3f МБ%n", pointName,  ((double) point.usedMemoryBytes / (1024 * 1024)));
  }

  /**
   * Получить используемую память в точке (в КБ)
   */
  public double getPointUsedMemoryInKB(String pointName) {
    ControlPoint point = getControlPoint(pointName);
    return ((double) point.usedMemoryBytes / 1024);
  }

  /**
   * Вывести в консоль используемую память в точке (в КБ)
   */
  public void printPointUsedMemoryInKB(String pointName) {
    ControlPoint point = getControlPoint(pointName);
    System.out.printf("Используемая память в точке %s: %.3f КБ%n", pointName,  ((double) point.usedMemoryBytes / 1024));
  }

  /**
   * Получить контрольную точку
   */
  public ControlPoint getPoint(String pointName) {
    return getControlPoint(pointName);
  }

  private ControlPoint getControlPoint(String controlPointName) {
    if (!controlPoints.containsKey(controlPointName)) {
      throw new IllegalArgumentException("Контрольная точка " + controlPointName + " не найдена!");
    }
    return controlPoints.get(controlPointName);
  }

  /** Контрольная точка */
  @Builder
  @Getter
  @EqualsAndHashCode(onlyExplicitlyIncluded = true)
  @ToString
  public static class ControlPoint {

    /** Имя (ключ) контрольной точки */
    @EqualsAndHashCode.Include
    private final String name;

    /** Системное время, снятое в данной точке (в миллисекундах) */
    private final long timeMills;

    /** Используемая память в данной точке (в байтах) */
    private final long usedMemoryBytes;
  }
}
