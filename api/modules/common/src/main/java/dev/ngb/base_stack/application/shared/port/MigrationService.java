package dev.ngb.base_stack.application.shared.port;

public interface MigrationService {
    void migrateTenantSchema(String tenantId);

    void migrateAllTenantSchemas();
}