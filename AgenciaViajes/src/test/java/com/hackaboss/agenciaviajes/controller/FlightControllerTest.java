package com.hackaboss.agenciaviajes.controller;

import com.hackaboss.agenciaviajes.dto.FlightDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FlightControllerTest {

    @Autowired
    private FlightController flightController;

    @Test
    public void addFlight_withInvalidData_shouldReturnError() {

        // Arrange - Configuración de datos de prueba
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setOrigin("Invalid origin");
        flightDTO.setDestination("Invalid destination");
        flightDTO.setSeatType("Invalid seat type");
        flightDTO.setFlightPrice(-10.0);
        flightDTO.setDate(LocalDate.now());

        // Act - Acción: Llamada al método que se está probando
        ResponseEntity<?> response = flightController.addFlight(flightDTO);

        // Assert - Verificación: Comprobación de los resultados
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

    @Test
    public void addFlight_withValidData_shouldCreateFlight() {

        // Arrange - Configuración de datos de prueba
        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setOrigin("Origen prueba");
        flightDTO.setDestination("Destion prueba");
        flightDTO.setSeatType("Economy prueba");
        flightDTO.setFlightPrice(100.0);
        flightDTO.setDate(LocalDate.now());

        // Act - Acción: Llamada al método que se está probando
        ResponseEntity<?> response = flightController.addFlight(flightDTO);

        // Assert - Verificación: Comprobación de los resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully created flight", response.getBody());
    }
}
