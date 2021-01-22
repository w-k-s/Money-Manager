package io.wks.moneymanager.config;

import org.casbin.adapter.JdbcAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.spring.boot.autoconfigure.properties.CasbinExceptionProperties;
import org.postgresql.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class CasbinConfig {

    @Bean
    public Enforcer enforcer(JdbcAdapter adapter) {
        return new Enforcer("src/main/resources/casbin/model.conf", adapter);
    }

    @Bean
    public JdbcAdapter jdbcAdapter() throws Exception {
        return new JdbcAdapter(
                new JdbcTemplate(
                        new SimpleDriverDataSource(
                                new Driver(),
                                "jdbc:postgresql://localhost:5432/money-manager",
                                "pguser",
                                "pgpassword"
                        )),
                new CasbinExceptionProperties(),
                true);
    }
}
