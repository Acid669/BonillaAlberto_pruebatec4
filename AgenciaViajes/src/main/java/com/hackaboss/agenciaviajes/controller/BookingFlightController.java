package com.hackaboss.agenciaviajes.controller;

import com.hackaboss.agenciaviajes.dto.BookingFlightDTO;
import com.hackaboss.agenciaviajes.model.User;
import com.hackaboss.agenciaviajes.service.IBookingFlightService;
import com.hackaboss.agenciaviajes.model.BookFlight;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class BookingFlightController {

    @Autowired
    private IBookingFlightService bookingFlightSv;

    //Constantes para validaciones
    public static final String TEXT_PATTERN = "[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+";
    public static final String INVALID_ORIGIN_NAME = "Invalid origin name";
    public static final String REQUIRED_FLIGHT_CODE = "Flight code is required";
    public static final String INVALID_DESTINATION_NAME = "Invalid destination name";
    public static final String INVALID_SEAT_TYPE = "Invalid seat type";
    public static final String INVALID_USER_NAME = "Invalid user name";
    public static final String INVALID_USER_LASTNAME = "Invalid user lastname";
    public static final String INVALID_USER_EMAIL = "Invalid user email";
    public static final String INVALID_USER_PASSPORT = "Invalid user passport";
    public static final String INVALID_USER_AGE = "Invalid user age";
    public static final String DATE_REQUIRED = "Error: Date is required.";
    public static final String PASSENGER_INFO_REQUIRED = "Error: Passenger information is required.";
    public static final String NO_BOOK_FLIGHTS_FOUND_MESSAGE = "There are no book flights registered with that ID";

    @Operation(summary = "Crea una reserva de vuelo",
            description = "Este método maneja la creación de una reserva de vuelo. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight reservation created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid book flight data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/flight-booking/new")
    public ResponseEntity<?> createBookFlight(@RequestBody BookingFlightDTO bookingFlightDTO)  {

        //Validación de los datos de la reserva
        String validationError = validateBookingFlightDTO(bookingFlightDTO);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }
        //Creación de la reserva de vuelo
        BookFlight bookFlight = bookingFlightSv.createBookingFlight(bookingFlightDTO);
        if (bookFlight == null) {
            return ResponseEntity.badRequest().body("The reservation dates must be within the range of available dates or user already has a booking for this date and flight.");
        }

        //Cálculo del precio total de la reserva
        Double price = bookingFlightSv.getAllBookFlights().get(0).getPrice();
        return ResponseEntity.ok().body("The total price of the reservation is: " + price);
    }


    @Operation(summary = "Obtiene la lista de reservas de vuelo", description = "Este método maneja la consulta de la lista de reservas de vuelos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of  book flights successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid book flight data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/flight-booking/all")
    public ResponseEntity<?> getAllBookFlights() {
        List<BookFlight> flights = bookingFlightSv.getAllBookFlights();
        if (flights.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No registered book flights found");
        }
        return ResponseEntity.ok(flights);
    }


    @Operation(summary = "Se elimina una reserva de vuelo por id", description = "Este método maneja la eliminación de una reserva de vuelo por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book flight successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid book flight ID"),
            @ApiResponse(responseCode = "404", description = "No registered  book flights found"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/flight-booking/delete/{id}")
    public ResponseEntity<?> deleteBookFlight(@PathVariable String id) {
        Long flightId = Long.parseLong(id);

        BookFlight flight = bookingFlightSv.findFlightById(flightId);
        if (flight == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_BOOK_FLIGHTS_FOUND_MESSAGE);
        }

        bookingFlightSv.deleteBookFlight(flightId);
        return ResponseEntity.ok().body("Book flight successfully deleted");
    }

    //Método privado para validar los datos de la reserva de vuelo
    private String validateBookingFlightDTO(BookingFlightDTO bookingFlightDTO) {

        // Serie de validaciones para los datos de la reserva de vuelo
        if (bookingFlightDTO.getDate() == null) {
            return DATE_REQUIRED;
        }

        if (bookingFlightDTO.getOrigin() == null || !bookingFlightDTO.getOrigin().matches(TEXT_PATTERN)) {
            return INVALID_ORIGIN_NAME;
        }

        if (bookingFlightDTO.getDestination() == null || !bookingFlightDTO.getDestination().matches(TEXT_PATTERN)) {
            return INVALID_DESTINATION_NAME;
        }

        if (bookingFlightDTO.getFlightCode() == null || bookingFlightDTO.getFlightCode().isEmpty()) {
            return REQUIRED_FLIGHT_CODE;
        }

        if (bookingFlightDTO.getSeatType() == null || !bookingFlightDTO.getSeatType().matches(TEXT_PATTERN)) {
            return INVALID_SEAT_TYPE;
        }

        return validatePassenger(bookingFlightDTO.getPassenger());
    }

    //Método privado para validar los datos del pasajero
    private String validatePassenger(User passenger) {

        //Serie de validaciones para los datos del pasajero
        if (passenger == null) {
            return PASSENGER_INFO_REQUIRED;
        }

        if (passenger.getName() == null || !passenger.getName().matches(TEXT_PATTERN)) {
            return INVALID_USER_NAME;
        }

        if (passenger.getLastName() == null || !passenger.getLastName().matches(TEXT_PATTERN)) {
            return INVALID_USER_LASTNAME;
        }

        if (passenger.getEmail() == null || passenger.getEmail().isEmpty() || !passenger.getEmail().contains("@")) {
            return INVALID_USER_EMAIL;
        }

        if (passenger.getPassPort() == null || passenger.getPassPort().isEmpty()) {
            return INVALID_USER_PASSPORT;
        }

        if (passenger.getAge() <= 0) {
            return INVALID_USER_AGE;
        }

        return null; // Retorna null si no hay errores
    }

    //Manejador de excepciones para errores al leer mensajes HTTP
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        // Identifica la causa raíz de la excepción y retorna una respuesta apropiada
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the date format, it must be yyyy-MM-dd.");
        } else if (rootCause instanceof NumberFormatException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering a numerical value.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
    }
}
