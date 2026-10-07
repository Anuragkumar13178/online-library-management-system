package com.librarymanagement.config;

import com.librarymanagement.review1.JdbcReview1Dao;
import com.librarymanagement.review1.Review1Servlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Review1ServletConfig {
    @Bean
    public ServletRegistrationBean<Review1Servlet> review1Servlet(JdbcReview1Dao jdbcDao) {
        return new ServletRegistrationBean<>(new Review1Servlet(jdbcDao), "/review1/library");
    }
}