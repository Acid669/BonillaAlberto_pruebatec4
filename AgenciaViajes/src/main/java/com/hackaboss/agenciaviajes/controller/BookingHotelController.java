package com.hackaboss.agenciaviajes.controller;

import com.hackaboss.agenciaviajes.dto.BookingHotelDTO;
import com.hackaboss.agenciaviajes.model.BookHotel;
import com.hackaboss.agenciaviajes.model.User;
import com.hackaboss.agenciaviajes.service.IBookingHotelService;
import com.hackaboss.agenciaviajes.service.IRoomService;
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
public class BookingHotelController {

    @Autowired
    private IBookingHotelService bookingHotelSv;

    @Autowired
    private IRoomService roomSv;

    //Constantes para validaciones
    private static final String TEXT_PATTERN = "[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+";
    private static final String INVALID_PLACE_NAME = "Invalid place name";
    private static final String REQUIRED_HOTEL_CODE = "Hotel code is required";
    private static final String REQUIRED_USER_INFO = "Error: User information is required.";
    private static final String INVALID_ROOM_TYPE = "Invalid room type";
    private static final String INVALID_USER_NAME = "Invalid user name";
    private static final String INVALID_USER_LASTNAME = "Invalid user lastname";
    private static final String INVALID_USER_EMAIL = "Invalid user email";
    private static final String INVALID_USER_PASSPORT = "Invalid user passport";
    private static final String INVALID_USER_AGE = "Invalid user age";
    private static final String DATE_ORDER_ERROR_START = "Error: Disponibility start date must be before end date.";
    private static final String DATE_ORDER_ERROR_END = "Error: Disponibility end date must be after start date.";
    private static final String DATES_ERROR = "Error: Both start and end dates are required.";
    private static final String PEOPLE_ERROR = "Error: Number of people must be greater than 0.";
    public static final String NO_BOOK_HOTELS_FOUND_MESSAGE = "There are no book hotels registered with that ID";

    @Operation(summary = "Crea una reserva de hotel", description = "Este método maneja la creación de una reserva de hotel. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid book hotel data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/hotel-booking/new")
    public ResponseEntity<?> createBookHotel(@RequestBody BookingHotelDTO bookingHotelDTO) {

        //Validación de los datos de la reserva
        String validationError = validateBookingHotelDTO(bookingHotelDTO);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }

        //Creación de la reserva de hotel
        BookHotel bookHotel = bookingHotelSv.createBookHotel(bookingHotelDTO);
        if (bookHotel == null) {
            return ResponseEntity.badRequest().body("The reservation could not be carried out or user already has a booking for this date and hotel, please check the reservation date");
        }

        //Cálculo del precio total de la reserva
        int nights = bookingHotelSv.getAllBookHotels().get(0).getNights();
        Double totalPrice = nights * roomSv.getAllRooms().get(0).getRoomPrice();
        return ResponseEntity.ok().body("The total price of the reservation is: " + totalPrice + " €");
    }

    @Operation(summary = "Obtiene la lista de reservas de hotel", description = "Este método maneja la obtención de la lista de reservas de hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid book hotel data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/hotel-booking/all")
    public ResponseEntity<?> getAllBookHotels() {

        List<BookHotel> hotels = bookingHotelSv.getAllBookHotels();
        if (hotels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No registered book hotels found");
        }
        return ResponseEntity.ok(hotels);

    }

    @Operation(summary = "Se elimina una reserva de hotel por id", description = "Este método maneja la eliminación de una reserva de hotel por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel reservation deleted successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid book hotel data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @DeleteMapping("/hotel-booking/delete/{id}")
    public ResponseEntity<?> deleteBookHotel(@PathVariable String id) {
        Long hotelId = Long.parseLong(id);

        BookHotel hotel = bookingHotelSv.findBookHotelById(hotelId);
        if (hotel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_BOOK_HOTELS_FOUND_MESSAGE);
        }
        bookingHotelSv.deleteBookHotel(hotelId);
        return ResponseEntity.ok().body("Book hotel successfully deleted");
    }

    // Método privado para validar los datos de la reserva
    private String validateBookingHotelDTO(BookingHotelDTO bookingHotelDTO) {

        // Serie de validaciones para los datos de la reserva de hotel
        if (bookingHotelDTO.getDateFrom() == null || bookingHotelDTO.getDateTo() == null) {
            return DATES_ERROR;
        }

        if (bookingHotelDTO.getPlace() == null || !bookingHotelDTO.getPlace().matches(TEXT_PATTERN)) {
            return INVALID_PLACE_NAME;
        }

        if (bookingHotelDTO.getHotelCode() == null || bookingHotelDTO.getHotelCode().isEmpty()) {
            return REQUIRED_HOTEL_CODE;
        }

        if (bookingHotelDTO.getRoomType() == null || !bookingHotelDTO.getRoomType().matches(TEXT_PATTERN)) {
            return INVALID_ROOM_TYPE;
        }

        if (bookingHotelDTO.getDateFrom().isAfter(bookingHotelDTO.getDateTo())) {
            return DATE_ORDER_ERROR_START;
        }

        if (bookingHotelDTO.getDateTo().isBefore(bookingHotelDTO.getDateFrom())) {
            return DATE_ORDER_ERROR_END;
        }

        if (bookingHotelDTO.getPeopleQ() <= 0) {
            return PEOPLE_ERROR;
        }

        return validateUser(bookingHotelDTO.getHost());
    }

    //Método privado para validar los datos del usuario que realiza la reserva
    private String validateUser(User user) {

        //Serie de validaciones para los datos del usuario
        if (user == null) {
            return REQUIRED_USER_INFO;
        }
        if (user.getName() == null || !user.getName().matches(TEXT_PATTERN)) {
            return INVALID_USER_NAME;
        }
        if (user.getLastName() == null || !user.getLastName().matches(TEXT_PATTERN)) {
            return INVALID_USER_LASTNAME;
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            return INVALID_USER_EMAIL;
        }
        if (user.getPassPort() == null || user.getPassPort().isEmpty()) {
            return INVALID_USER_PASSPORT;
        }
        if (user.getAge() <= 0) {
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
