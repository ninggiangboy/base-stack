package dev.ngb.base_stack.infrastructure.multi_tenancy.datasource;

import dev.ngb.base_stack.application.shared.port.TenantContext;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class MultiTenantDataSourceConfig {

    @Bean
    public DataSource dataSource(DataSourceProperties props, TenantContext tenantContext) {
        DataSource ds = props.initializeDataSourceBuilder().build();
        return new MultiTenantSchemaAwareDataSource(ds, tenantContext);
    }
}
