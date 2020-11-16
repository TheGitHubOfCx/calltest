package com.example.calltest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class CalltestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalltestApplication.class, args);
    }

}
