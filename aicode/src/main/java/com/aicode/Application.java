package com.aicode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@SpringBootApplication
public class Application {


    public static void main(String[] args) {
        while (true){
            System.out.println(System.currentTimeMillis());
        }
//        SpringApplication.run(Application.class, args);
    }

}

