package ru.morningcake.test.timezone;

import org.junit.jupiter.api.BeforeEach;
import ru.morningcake.test.BaseTest;

import java.util.TimeZone;

public abstract class BaseTestDefaultTimezone extends BaseTest {

  @BeforeEach
  protected void setDefaultTimezone() {
    System.out.print("Default Timezone до: " + TimeZone.getDefault());
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
    System.out.println("; после: " + TimeZone.getDefault());
  }

}
