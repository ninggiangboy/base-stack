package dev.ngb.base_stack.infrastructure.multi_tenancy.datasource;

import dev.ngb.base_stack.application.shared.port.TenantContext;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class MultiTenantSchemaAwareDataSource extends DelegatingDataSource {

    private final TenantContext tenantContext;

    public MultiTenantSchemaAwareDataSource(DataSource dataSource, TenantContext tenantContext) {
        super(dataSource);
        this.tenantContext = tenantContext;
    }

    @Override
    @NonNull
    public Connection getConnection() throws SQLException {
        Connection connection = super.getConnection();
        setSchema(connection);
        return connection;
    }

    @NonNull
    @Override
    public Connection getConnection(@NonNull String username, @NonNull String password) throws SQLException {
        Connection connection = super.getConnection(username, password);
        setSchema(connection);
        return connection;
    }

    private void setSchema(Connection connection) throws SQLException {
        String tenantId = tenantContext.getTenantId();
        connection.setSchema(Objects.requireNonNullElse(tenantId, "public"));
    }
}
