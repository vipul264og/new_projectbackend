package com.edulib;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EdulibApplication {
    public static void main(String[] args) {
        SpringApplication.run(EdulibApplication.class, args);
    }
}
