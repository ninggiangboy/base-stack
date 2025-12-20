package dev.ngb.base_stack.infrastructure.persistence.migration.liquibase;

import dev.ngb.base_stack.application.shared.port.MigrationService;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LiquibaseMigrationService implements MigrationService {

    private final DataSource dataSource;
    private final String TENANT_SCHEMA_PREFIX = "tenant_";
    private static final String CHANGELOG_PATH = "db.changelog/schema/db.changelog-master.yaml";

    @Override
    public void migrateTenantSchema(@NonNull String tenantId) {
        String schema = TENANT_SCHEMA_PREFIX + tenantId;
        log.info("Starting schema migration for schema={}", schema);

        try (Connection connection = dataSource.getConnection()) {

            if (schemaExists(connection, schema)) {
                throw new IllegalStateException("Schema already exists for schema=" + schema);
            }

            createSchema(connection, schema);
            runLiquibase(connection, schema);

            log.info("Migration completed successfully for schema {}", schema);

        } catch (SQLException | LiquibaseException e) {
            log.error("Migration failed for schema={}", schema, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void migrateAllTenantSchemas() {
        log.info("Starting migration for ALL schemas");

        try (Connection connection = dataSource.getConnection()) {

            List<String> schemas = findAllTargetSchemas(connection);
            log.info("Found {} schemas to migrate: {}", schemas.size(), schemas);

            for (String schema : schemas) {
                try {
                    log.info("Running migration for schema={}", schema);
                    runLiquibase(connection, schema);
                    log.info("Schema {} migrated successfully", schema);
                } catch (LiquibaseException e) {
                    log.error("Migration failed for schema={}", schema, e);
                    throw new RuntimeException(e);
                }
            }

            log.info("Migration completed for all schemas");

        } catch (SQLException e) {
            log.error("Failed during global migration", e);
            throw new RuntimeException(e);
        }
    }

    private boolean schemaExists(@NonNull Connection connection, @NonNull String schema) throws SQLException {
        String sql = """
                    SELECT schema_name
                    FROM information_schema.schemata
                    WHERE schema_name = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, schema);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void createSchema(@NonNull Connection connection, @NonNull String schema) throws SQLException {
        log.info("Creating schema {}", schema);

        String safeName = "\"" + schema.replace("\"", "\"\"") + "\"";
        String sql = "CREATE SCHEMA IF NOT EXISTS " + safeName;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.execute();
        }
    }

    private void runLiquibase(@NonNull Connection connection, @NonNull String schema)
            throws SQLException, LiquibaseException {

        connection.setSchema(schema);

        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        Liquibase liquibase = new Liquibase(
                CHANGELOG_PATH,
                new ClassLoaderResourceAccessor(),
                database
        );

        liquibase.update("");
        connection.setSchema("public");
    }

    private List<String> findAllTargetSchemas(@NonNull Connection connection) throws SQLException {
        String sql = """
                SELECT schema_name
                FROM information_schema.schemata
                WHERE schema_name LIKE '%s%%'
                """.formatted(TENANT_SCHEMA_PREFIX);

        List<String> schemas = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                schemas.add(rs.getString("schema_name"));
            }
        }
        return schemas;
    }
}
