package com.example.calltest.entity;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sharing.datasource0")
public class DataBase0Config {

    private String dataBaseName;
    private String url;
    private String driverClassName;
    private String userName;
    private String passWord;

    public HikariDataSource createDataSource() {
        HikariDataSource dataSource2 = new HikariDataSource();
        dataSource2.setDriverClassName(driverClassName);
        dataSource2.setJdbcUrl(url);
        dataSource2.setUsername(userName);
        dataSource2.setPassword(passWord);
        return dataSource2;
    }
}
