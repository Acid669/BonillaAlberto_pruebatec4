package com.hackaboss.agenciaviajes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration

public class Security {

    //Define un bean para la cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf().disable() // Deshabilita la protección CSRF, común en APIs REST
                .authorizeHttpRequests(authorize ->
                        authorize
                                // Permite acceso sin autenticación a las siguientes rutas
                                .requestMatchers("/agency/hotels").permitAll()
                                .requestMatchers("/agency/flights").permitAll()
                                .requestMatchers("/agency/flights/{id}").permitAll()
                                .requestMatchers("/agency/hotels/{id}").permitAll()
                                .requestMatchers("/agency/flights/search").permitAll()
                                .requestMatchers("/agency/hotels/search").permitAll()
                                .requestMatchers("/agency/flight-booking/new").permitAll()
                                .requestMatchers("/agency/hotel-booking/new").permitAll()

                                // Cualquier otra solicitud requiere autenticación
                                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        // Permite la configuración de un formulario de login
                        .permitAll()
                )
                .httpBasic().and() // Habilita la autenticación HTTP básica
                .build();
    }
}
