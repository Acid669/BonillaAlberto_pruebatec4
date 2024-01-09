package com.hackaboss.agenciaviajes.controller;

import com.hackaboss.agenciaviajes.dto.FlightDTO;
import com.hackaboss.agenciaviajes.model.Flight;
import com.hackaboss.agenciaviajes.service.IFlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class FlightController {

    @Autowired
    private IFlightService fligthSv;
    public static final String INVALID_ORIGIN_NAME = "Invalid origin name";
    public static final String INVALID_DESTINATION_NAME = "Invalid destination name";
    public static final String INVALID_SEAT_TYPE = "Invalid seat type";
    public static final String INVALID_FLIGHT_PRICE = "Invalid flight price";
    public static final String INVALID_DATE = "Invalid date";
    public static final String INVALID_ID_MESSAGE = "Invalid ID entered, only numerical characters allowed.";
    public static final String DATE_ORDER_ERROR_START = "Error: Disponibility start date must be before end date.";
    public static final String DATE_ORDER_ERROR_END = "Error: Disponibility end date must be after start date.";
    public static final String NO_FLIGHTS_FOUND_MESSAGE = "There are no flights registered with that ID";
    public static final String TEXT_PATTERN = "[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+";


    @Operation(summary = "Endpoint para crear un vuelo", description = "Este método maneja la creación de un vuelo. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid flight data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/flights/new")
    public ResponseEntity<?> addFlight(@Valid @RequestBody FlightDTO flightDTO) {

        if (flightDTO.getOrigin() == null || !flightDTO.getOrigin().matches(TEXT_PATTERN)) {
            return ResponseEntity.badRequest().body(INVALID_ORIGIN_NAME);
        }

        if (flightDTO.getDestination() == null || !flightDTO.getDestination().matches(TEXT_PATTERN)) {
            return ResponseEntity.badRequest().body(INVALID_DESTINATION_NAME);
        }

        if (flightDTO.getSeatType() == null || !flightDTO.getSeatType().matches(TEXT_PATTERN)) {
            return ResponseEntity.badRequest().body(INVALID_SEAT_TYPE);
        }

        if (flightDTO.getFlightPrice() == null || flightDTO.getFlightPrice() <= 0) {
            return ResponseEntity.badRequest().body(INVALID_FLIGHT_PRICE);
        }

        if (flightDTO.getDate() == null) {
            return ResponseEntity.badRequest().body(INVALID_DATE);
        }

        fligthSv.addFlight(flightDTO);
        return ResponseEntity.ok().body("Successfully created flight");
    }

    @Operation(summary = "Endpoint para consultar todos los vuelos", description = "Este método maneja la consulta de vuelos. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of flights successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "No registered flights found"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flights")
    public ResponseEntity<?> getAllFlights() {
        List<Flight> flights = fligthSv.getAllFlights();
        if (flights.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No registered flights found");
        }
        return ResponseEntity.ok(flights);
    }

    @Operation(summary = "Endpoint para encontrar un vuelo por ID", description = "Este método maneja la consulta de vuelos por ID. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación.")
    @GetMapping("/flights/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "No registered flights found"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    public ResponseEntity<?> findFlightById(@PathVariable String id) {

        if (!id.matches("\\d+")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_MESSAGE);
        }
        Long flightId = Long.parseLong(id);

        Flight flight = fligthSv.findFlightById(flightId);
        if (flight == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_FLIGHTS_FOUND_MESSAGE);
        }

        return ResponseEntity.ok(flight);
    }

    @Operation(summary = "Endpoint para eliminar un vuelo por ID", description = "Este método maneja la eliminación de vuelos por ID. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación.")
    @DeleteMapping("/flights/delete/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid flight ID"),
            @ApiResponse(responseCode = "404", description = "No registered flights found"),
            @ApiResponse(responseCode = "409", description = "Flight cannot be deleted because it has bookings"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    public ResponseEntity<?> deleteFlight(@PathVariable String id) {

        if (!id.matches("\\d+")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_MESSAGE);
        }

        Long flightID = Long.parseLong(id);

        Flight flight = fligthSv.findFlightById(flightID);
        if (flight == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_FLIGHTS_FOUND_MESSAGE);
        }

        // Verificar si el vuelo tiene reservas asociadas
        if (!fligthSv.canFlightBeDeleted(flightID)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Flight cannot be deleted because it has bookings");
        }

        fligthSv.deleteFlight(flightID);
        return ResponseEntity.ok().body("Flight successfully deleted");
    }


    @Operation(summary = "Endpoint para editar un vuelo por ID", description = "Este método maneja la modificación de vuelos por ID. Devuelve diferentes codigo de respuesta HTTP dependiendo del resultado de la operación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid flight ID"),
            @ApiResponse(responseCode = "404", description = "No registered flights found"),
            @ApiResponse(responseCode = "409", description = "Flight cannot be modified because it has bookings"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/flights/edit/{id}")
    public ResponseEntity<?> editFlight(@PathVariable String id,
                                        @RequestParam("seatType") String editSeatType,
                                        @RequestParam("flightPrice") Double editFlightPrice,
                                        @RequestParam("date") LocalDate editDate) {
        if (!id.matches("\\d+")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_MESSAGE);
        }

        if (editSeatType == null || !editSeatType.matches(TEXT_PATTERN)) {
            return ResponseEntity.badRequest().body(INVALID_SEAT_TYPE);
        }
        if (editFlightPrice == null || editFlightPrice <= 0) {
            return ResponseEntity.badRequest().body(INVALID_FLIGHT_PRICE);
        }
        if (editDate == null) {
            return ResponseEntity.badRequest().body(INVALID_DATE);
        }


        Long flightID = Long.parseLong(id);
        Flight flight = fligthSv.findFlightById(flightID);

        if (flight == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_FLIGHTS_FOUND_MESSAGE);
        }

        if (!fligthSv.canFlightBeModified(flightID)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Flight cannot be modified because it has bookings");
        }

        flight.setSeatType(editSeatType);
        flight.setFlightPrice(editFlightPrice);
        flight.setDate(editDate);

        fligthSv.saveFlight(flight);
        return ResponseEntity.ok().body("Flight successfully updated");
    }

    @Operation(summary = "Endpoint para buscar vuelos filtrados por fecha, origen y destino", description = "Este método maneja la consulta de vuelos por fecha, origen y destino. Devuelve differentes códigos de respuesta HTTP dependiendo del resultado de la operación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of flights successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "No registered flights found"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flights/search")
    public ResponseEntity<?> getFilteredFlights(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE) LocalDate date1,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE) LocalDate date2,
                                                @RequestParam String origin,
                                                @RequestParam String destination) {
        if (origin == null || !origin.matches(TEXT_PATTERN)) {
            return ResponseEntity.badRequest().body(INVALID_ORIGIN_NAME);
        }

        if (destination == null || !destination.matches(TEXT_PATTERN)) {
            return ResponseEntity.badRequest().body(INVALID_DESTINATION_NAME);
        }

        if (date1.isAfter(date2)) {
            return ResponseEntity.badRequest().body(DATE_ORDER_ERROR_START);
        }

        if (date2.isBefore(date1)) {
            return ResponseEntity.badRequest().body(DATE_ORDER_ERROR_END);
        }

        List<Flight> flights = fligthSv.getFilteredFlights(date1, date2, origin, destination);
        if (flights.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flights not found with those options.");
        }
        return ResponseEntity.ok(flights);
    }


    //Verifica si la excepción fue causada por un error en el formato de la fecha y precio
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();

        //Retorna una respuesta con estado 400 y un mensaje de error específico para el formato de fecha
        if ("date".equals(name) || "date1".equals(name) || "date2".equals(name)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the date format, it must be yyyy-MM-dd.");
            //Maneja errores en el formato del precio del vuelo
        } else if ("flightPrice".equals(name)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the price format, it must be numerical");
        }
        //Si la excepción no coincide con los casos anteriores, se retorna un error genérico de servidor
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
    }

    //Manejador de excepciones para errores al leer mensajes HTTP
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        // Obtén la causa raíz de la excepción
        Throwable rootCause = ex.getRootCause();

        // Verifica si la causa raíz es un error en el parseo de la fecha
        if (rootCause instanceof DateTimeParseException) {
            // Retorna una respuesta con estado 400 y un mensaje de error para el formato de fecha
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the date format, it must be yyyy-MM-dd.");
        }
        // Si la causa raíz no coincide con los casos anteriores, se retorna un error genérico de servidor
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
    }
}
