package com.veterinerklinik;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(title="Veterinary API",version = "1.0",description = "VetApp"))
public class VeterinerKlinikApplication {

    public static void main(String[] args) {
        SpringApplication.run(VeterinerKlinikApplication.class, args);
    }

}
