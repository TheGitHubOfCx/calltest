package com.example.calltest.controller;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TessConfig {


    @Bean
    @Scope("prototype")
    public Tesseract tesseract() {
        return new Tesseract();
    }
}
