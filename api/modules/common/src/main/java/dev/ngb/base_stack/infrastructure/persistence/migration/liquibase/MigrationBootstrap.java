package dev.ngb.base_stack.infrastructure.persistence.migration.liquibase;

import dev.ngb.base_stack.application.shared.port.MigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MigrationBootstrap implements ApplicationRunner {

    private final MigrationService migrationService;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        log.info("Starting schema migration when application starts");
        migrationService.migrateAllTenantSchemas();
    }
}
