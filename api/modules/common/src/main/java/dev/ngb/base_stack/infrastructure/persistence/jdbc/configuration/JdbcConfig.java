package dev.ngb.base_stack.infrastructure.persistence.jdbc.configuration;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

import java.util.Optional;

@Configuration
@EnableJdbcRepositories(basePackages = "dev.ngb.base_stack.infrastructure.persistence.jdbc")
@EnableJdbcAuditing
public class JdbcConfig {
    @Bean
    public AuditorAware<@NonNull String> auditorProvider() {
        return () -> Optional.of("system");
    }
}
