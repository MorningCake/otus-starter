package ru.morningcake.validation;

import ru.morningcake.exception.data.CustomValidationException;
import ru.morningcake.handler.Problem;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Утилитный класс для валидации дат
 *
 * @author r.galiev
 * @version 1.0.0
 * @since 1.0.0
 */
public class DateValidationUtils {
	private static final String AFTER_FORMAT = "%s %s should be after the current date %s";
	private static final String BEFORE_FORMAT = "%s %s should be before the current date %s";
	private static final String NOT_AFTER_GIVEN_DATE = "%s %s should be before or equal to the given date %s";
	private static final String SYSTEM_MESSAGE = "Date validation is fail";

	/**
	 * Валидация прошедшей даты. В случае передачи будущей даты возникает исключение
	 *
	 * @param dateTime     								- Дата для валидации
	 * @param propertyPath 								- Условный путь атрибута в формате subentity.propertyName
	 * @throws CustomValidationException 	- Исключение, возникающее при передаче невалидной даты
	 */
	public static void checkPastDateTime(OffsetDateTime dateTime, String propertyPath) throws CustomValidationException {
		OffsetDateTime currentDate = OffsetDateTime.now();
		if (dateTime != null && dateTime.isAfter(currentDate)) {
			throwExceptionWithProblem(BEFORE_FORMAT, propertyPath, dateTime.toString(), currentDate.toString());
		}
	}

	/**
	 * Валидация двух дат, первый(1) аргумент не должен быть больше второго(2) аргумента, в ином случае возникает исключение
	 *
	 * @param targetDate - цельная дата (1)
	 * @param afterDate - дата для сравнения (2)
	 * @param propertyPath - Условный путь атрибута в формате subentity.propertyName
	 * @throws CustomValidationException 	- Исключение, возникающее при передаче невалидной даты
	 */
	public static void checkTargetDateIsNotAfter(LocalDate targetDate, LocalDate afterDate, String propertyPath) {
		if (!targetDate.isBefore(afterDate) && !targetDate.isEqual(afterDate)) {
			throwExceptionWithProblem(NOT_AFTER_GIVEN_DATE, propertyPath, targetDate.toString(), afterDate.toString());
		}
	}

	/**
	 * Валидация прошедшей даты. В случае передачи будущей даты возникает исключение
	 *
	 * @param date		     								- Дата для валидации
	 * @param propertyPath 								- Условный путь атрибута в формате subentity.propertyName
	 * @throws CustomValidationException 	- Исключение, возникающее при передаче невалидной даты
	 */
	public static void checkPastDateTime(LocalDate date, String propertyPath) throws CustomValidationException {
		LocalDate currentDate = LocalDate.now();
		if (date != null && date.isAfter(currentDate)) {
			throwExceptionWithProblem(BEFORE_FORMAT, propertyPath, date.toString(), currentDate.toString());
		}
	}

	/**
	 * Валидация будущей даты. В случае передачи прошедшей даты возникает исключение
	 *
	 * @param dateTime     								- Дата для валидации
	 * @param propertyPath 								- Условный путь атрибута в формате subentity.propertyName
	 * @throws CustomValidationException 	- Исключение, возникающее при передаче невалидной даты
	 */
	public static void checkFutureDateTime(OffsetDateTime dateTime, String propertyPath) throws CustomValidationException {
		OffsetDateTime currentDate = OffsetDateTime.now();
		if (dateTime != null && dateTime.isBefore(currentDate)) {
			throwExceptionWithProblem(AFTER_FORMAT, propertyPath, dateTime.toString(), currentDate.toString());
		}
	}

	/**
	 * Валидация будущей даты. В случае передачи прошедшей даты возникает исключение
	 *
	 * @param date		     								- Дата для валидации
	 * @param propertyPath 								- Условный путь атрибута в формате subentity.propertyName
	 * @throws CustomValidationException 	- Исключение, возникающее при передаче невалидной даты
	 */
	public static void checkFutureDateTime(LocalDate date, String propertyPath) throws CustomValidationException {
		LocalDate currentDate = LocalDate.now();
		if (date != null && date.isBefore(currentDate)) {
			throwExceptionWithProblem(AFTER_FORMAT, propertyPath, date.toString(), currentDate.toString());
		}
	}

	/**
	 * Валидация списка будущих дат типа LocalDate. В случае передачи прошедшей даты возникает исключение
	 *
	 * @param datesMap - Map, где ключ - условный путь атрибута в формате subentity.propertyName, значение - дата для валидации
	 * @throws CustomValidationException 	- Исключение, возникающее при передаче невалидной даты
	 */
	public static void checkFutureLocalDates(Map<String, LocalDate> datesMap) {
		if (datesMap != null) {
			LocalDate currentDate = LocalDate.now();
			var nonValidDatesMap = getNonNullDatesStream(datesMap)
							.filter(entry -> entry.getValue().isBefore(currentDate))
							.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().toString()));
			if (!nonValidDatesMap.isEmpty()) {
				throwExceptionWithProblemList(AFTER_FORMAT, nonValidDatesMap, currentDate.toString());
			}
		}
	}

	/**
	 * Валидация списка прошедших дат типа LocalDate. В случае передачи будущей даты возникает исключение
	 *
	 * @param datesMap - Map, где ключ - условный путь атрибута в формате subentity.propertyName, значение - дата для валидации
	 * @throws CustomValidationException 	- Исключение, возникающее при передаче невалидной даты
	 */
	public static void checkPastLocalDates(Map<String, LocalDate> datesMap) {
		if (datesMap != null) {
			LocalDate currentDate = LocalDate.now();
			var nonValidDatesMap = getNonNullDatesStream(datesMap)
							.filter(entry -> entry.getValue().isAfter(currentDate))
							.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().toString()));
			if (!nonValidDatesMap.isEmpty()) {
				throwExceptionWithProblemList(BEFORE_FORMAT, nonValidDatesMap, currentDate.toString());
			}
		}
	}

	/**
	 * Валидация списка будущих дат типа OffsetDateTime. В случае передачи прошедшей даты возникает исключение
	 *
	 * @param datesMap - Map, где ключ - условный путь атрибута в формате subentity.propertyName, значение - дата для валидации
	 * @throws CustomValidationException 	- Исключение, возникающее при передаче невалидной даты
	 */
	public static void checkFutureOffsetDateTimes(Map<String, OffsetDateTime> datesMap) {
		if (datesMap != null) {
			OffsetDateTime currentDate = OffsetDateTime.now();
			var nonValidDatesMap = getNonNullDatesStream(datesMap)
							.filter(entry -> entry.getValue().isBefore(currentDate))
							.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().toString()));
			if (!nonValidDatesMap.isEmpty()) {
				throwExceptionWithProblemList(AFTER_FORMAT, nonValidDatesMap, currentDate.toString());
			}
		}
	}

	/**
	 * Валидация списка прошедших дат типа OffsetDateTime. В случае передачи будущей даты возникает исключение
	 *
	 * @param datesMap 										- Map, где ключ - условный путь атрибута в формате subentity.propertyName, значение - дата для валидации
	 * @throws CustomValidationException 	- Исключение, возникающее при передаче невалидной даты
	 */
	public static void checkPastOffsetDateTimes(Map<String, OffsetDateTime> datesMap) {
		if (datesMap != null) {
			OffsetDateTime currentDate = OffsetDateTime.now();
			var nonValidDatesMap = getNonNullDatesStream(datesMap)
							.filter(entry -> entry.getValue().isAfter(currentDate))
							.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().toString()));
			if (!nonValidDatesMap.isEmpty()) {
				throwExceptionWithProblemList(BEFORE_FORMAT, nonValidDatesMap, currentDate.toString());
			}
		}
	}

  /**
   * * Проверка на пересечения интервалов дат
   * @param startDates - даты начала
   * @param finishDates - даты конца
   */
	public static boolean isIntervalIntersections(List<LocalDate> startDates, List<LocalDate> finishDates) {
		for (int i = 0; i < startDates.size(); i++) {
      var start = startDates.get(i);
      var end = finishDates.get(i);
      start = start == null ? LocalDate.MIN : start;
      end = end == null ? LocalDate.MAX : end;
      checkTargetDateIsNotAfter(start, end, "not property");
      for (int j = i + 1; j < startDates.size(); j++) {
        var comparingStart = startDates.get(j);
        var comparingFinish = finishDates.get(j);
        comparingStart = comparingStart == null ? LocalDate.MIN : comparingStart;
        comparingFinish = comparingFinish == null ? LocalDate.MAX : comparingFinish;
        checkTargetDateIsNotAfter(comparingStart, comparingFinish, "not property");
        if ((start.isAfter(comparingStart) || start.isEqual(comparingStart)) && (start.isAfter(comparingFinish) || start.isEqual(comparingFinish))) {
          continue;
        }
        if ((end.isBefore(comparingStart) || end.isEqual(comparingStart)) && (end.isBefore(comparingFinish) || end.isEqual(comparingFinish))) {
          continue;
        }
        return true;
      }
    }
    return false;
	}

	private static <T> Stream<Map.Entry<String, T>> getNonNullDatesStream(Map<String, T> datesMap) {
		return datesMap.entrySet().stream()
						.filter(entry -> entry.getValue() != null);
	}

	private static void throwExceptionWithProblem(String format, String propertyPath, String date, String currentDate) {
		throw new CustomValidationException(SYSTEM_MESSAGE, List.of(Problem.builder()
						.message(String.format(format, propertyPath, date, currentDate))
						.invalidValue(date)
						.propertyPath(propertyPath)
						.build()));
	}

	private static void throwExceptionWithProblemList(String format, Map<String, String> nonValidDates, String currentDate) {
		List<Problem> problemsList = nonValidDates.entrySet().stream()
						.map(e -> Problem.builder()
										.message(String.format(format, e.getKey(), e.getValue(), currentDate))
										.invalidValue(e.getValue())
										.propertyPath(e.getKey()).build())
						.collect(Collectors.toList());
		throw new CustomValidationException(SYSTEM_MESSAGE, problemsList);
	}
}
