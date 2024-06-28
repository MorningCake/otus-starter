package ru.morningcake.test;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import ru.morningcake.test.timezone.BaseTestDefaultTimezone;

/**
 * Базовый класс для всех тестов приложения.
 */
//@DirtiesContext
@TestInstance(Lifecycle.PER_CLASS)
@AutoConfigureEmbeddedDatabase(refresh = RefreshMode.AFTER_EACH_TEST_METHOD, provider = DatabaseProvider.ZONKY)
public abstract class BaseTestWithPostgreSQL extends BaseTestDefaultTimezone {

}
