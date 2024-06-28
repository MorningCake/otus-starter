package ru.morningcake.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "${sberbank.scheduler.shedlock.defaultLockAtMostFor}")
public class SchedulerConfig {
  @Value("${sberbank.scheduler.shedlock.tableName}")
  private String lockTableName;

  @Bean
  public LockProvider lockProvider(JdbcTemplate jdbcTemplate, LiquibaseProperties liquibaseProperties) {
    return new JdbcTemplateLockProvider(
        JdbcTemplateLockProvider.Configuration.builder()
            .withJdbcTemplate(jdbcTemplate)
            .withTableName(liquibaseProperties.getDefaultSchema() + "." + lockTableName)
            .usingDbTime()
            .build()
    );
  }
}
