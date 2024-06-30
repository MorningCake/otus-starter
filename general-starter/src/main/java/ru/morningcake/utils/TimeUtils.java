package ru.morningcake.utils;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class TimeUtils {

  public ZoneOffset getDefaultZoneOffset() {
    return ZoneOffset.ofHours(3);
  }

  public long convertLocalDateTimeToExp(LocalDateTime dateTime, @Nullable ZoneOffset zoneOffset) {
    return dateTime.toEpochSecond(zoneOffset == null ? getDefaultZoneOffset() : zoneOffset);
  }

  public long getExpAfterMinutes(long expMinutes, @Nullable ZoneOffset zoneOffset) {
    return getExpAfter(expMinutes, ChronoUnit.MINUTES, zoneOffset);
  }
  public long getExpAfterHours(long expHours, @Nullable ZoneOffset zoneOffset) {
    return getExpAfter(expHours, ChronoUnit.HOURS, zoneOffset);
  }
  public long getExpAfterDays(long expDays, @Nullable ZoneOffset zoneOffset) {
    return getExpAfter(expDays, ChronoUnit.DAYS, zoneOffset);
  }
  private long getExpAfter(long expMinutes, ChronoUnit chronoUnit, @Nullable ZoneOffset zoneOffset) {
    return LocalDateTime.now().plus(expMinutes, chronoUnit).toEpochSecond(zoneOffset == null ? getDefaultZoneOffset() : zoneOffset);
  }

  public LocalDateTime getTimeFromExp(long exp, @Nullable ZoneOffset zoneOffset) {
    return LocalDateTime.ofEpochSecond(exp, 0, zoneOffset == null ? getDefaultZoneOffset() : zoneOffset);
  }
}
