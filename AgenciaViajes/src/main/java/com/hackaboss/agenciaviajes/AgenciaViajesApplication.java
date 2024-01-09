package com.hackaboss.agenciaviajes;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AgenciaViajesApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgenciaViajesApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info()
				.title("API para Gestión de Reservas en Hoteles y Vuelos")
				.version("1.0.0")
				.description("Esta es una API diseñada para facilitar la creación de hoteles, habitaciones y vuelos. Permite hacer reservas tanto de habitaciones como de vuelos de manera sencilla, además de guardar la información de los usuarios que realizan estas reservas."));
	}

}
