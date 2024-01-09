package com.hackaboss.agenciaviajes.controller;

import com.hackaboss.agenciaviajes.model.Room;
import com.hackaboss.agenciaviajes.repositories.HotelRepository;
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

@RestController
@RequestMapping("/agency")
public class RoomController {

    @Autowired
    private IRoomService roomSv;

    @Autowired
    private HotelRepository hotelRepo;

    //Constantes para validaciones
    private static final String ROOM_TYPE_PATTERN = "[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+";
    private static final String INVALID_ROOM_TYPE = "Invalid room type";
    private static final String INVALID_ROOM_PRICE = "Room price must be greater than 0.";
    private static final String INVALID_DATES = "Error: Both start and end disponibility dates are required.";
    private static final String DATE_ORDER_ERROR_START = "Error: Disponibility start date must be before end date.";
    private static final String DATE_ORDER_ERROR_END = "Error: Disponibility end date must be after start date.";
    private static final String ERROR_REQUIRED_ID = "Error: Hotel ID is required.";


    @Operation(summary = "Crea una habitación en un hotel",
            description = "Este método maneja la creación de una habitación en un hotel. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room successfully created"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid date format or null parameters"),
            @ApiResponse(responseCode = "404", description = "No registered hotels found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/rooms/new")
    public ResponseEntity<?> addRoom(@RequestBody Room room) {

        //Valida la habitación y devuelve un error si es necesario
        String validationError = validateRoom(room);
        if (validationError != null) {
            return ResponseEntity.badRequest().body(validationError);
        }
        roomSv.addRoom(room);
        return ResponseEntity.ok().body("The room has been successfully created");
    }

    private String validateRoom(Room room) {

        //Serie de validaciones para los datos de la habitación
        if (room.getHotel() == null || room.getHotel().getId() == null) {
            return ERROR_REQUIRED_ID;
        }
        if (!hotelRepo.existsById(room.getHotel().getId())) {
            return "Error: No hotel found with ID: " + room.getHotel().getId();
        }
        if (room.getRoomType() == null || !room.getRoomType().matches(ROOM_TYPE_PATTERN)) {
            return INVALID_ROOM_TYPE;
        }
        if (room.getRoomPrice() == null || room.getRoomPrice() <= 0) {
            return INVALID_ROOM_PRICE;
        }
        if (room.getDisponibilityDateFrom() == null || room.getDisponibilityDateTo() == null) {
            return INVALID_DATES;
        }
        if (room.getDisponibilityDateFrom().isAfter(room.getDisponibilityDateTo())) {
            return DATE_ORDER_ERROR_START;
        }

        if (room.getDisponibilityDateTo().isBefore(room.getDisponibilityDateFrom())) {
            return DATE_ORDER_ERROR_END;
        }
        return null; // Retorna null si no hay errores
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        //Identifica la causa raíz de la excepción
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof DateTimeParseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the date format, it must be yyyy-MM-dd.");
        } else if (rootCause instanceof NumberFormatException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the price format, it must be numerical.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
    }
}
