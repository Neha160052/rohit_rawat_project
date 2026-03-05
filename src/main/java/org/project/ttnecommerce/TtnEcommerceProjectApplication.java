package org.project.ttnecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TtnEcommerceProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtnEcommerceProjectApplication.class, args);
    }

}
