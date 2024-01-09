package com.hackaboss.agenciaviajes.controller;

import com.hackaboss.agenciaviajes.dto.HotelDTO;
import com.hackaboss.agenciaviajes.model.Hotel;
import com.hackaboss.agenciaviajes.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class HotelController {

    private static final String INVALID_HOTEL_NAME = "Invalid hotel name";
    private static final String INVALID_PLACE_NAME = "Invalid place name, must contain alphabetical characters.";
    private static final String INVALID_ID_MESSAGE = "Invalid ID entered, only numerical characters allowed.";
    private static final String PLACE_PATTERN = "[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+";
    private static final String HOTEL_NAME_PATTERN = "[a-zA-ZáéíóúÁÉÍÓÚñÑ0-9 ]+";
    private static final String DATE_ORDER_ERROR_START = "Error: Disponibility start date must be before end date.";
    private static final String DATE_ORDER_ERROR_END = "Error: Disponibility end date must be after start date.";
    public static final String NO_HOTELS_FOUND_MESSAGE = "There are no hotels registered with that ID";
    public static final String NO_REGISTERED_HOTELS_MESSAGE = "No registered hotels found";

    @Autowired
    private HotelService hotelSv;

    @Operation(summary = "Endpoint para crear un nuevo hotel", description = "Este método maneja la creación de un nuevo hotel. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid hotel data"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PostMapping("/hotels/new")
    public ResponseEntity<String> addHotel(@RequestBody HotelDTO hotelDTO) {

        if(hotelDTO.getName() == null || !hotelDTO.getName().matches(HOTEL_NAME_PATTERN)) {
            return ResponseEntity.badRequest().body(INVALID_HOTEL_NAME);
        }

        if(hotelDTO.getPlace() == null ||!hotelDTO.getPlace().matches(PLACE_PATTERN)) {
            return ResponseEntity.badRequest().body(INVALID_PLACE_NAME);
        }
        hotelSv.addHotel(hotelDTO);
        return ResponseEntity.ok().body("Hotel successfully created");
    }

    @Operation(summary = "Endpoint para obtener todos los hoteles", description = "Este método maneja la consulta de todos los hoteles registrados en la base de datos. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of hotels successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "No registered hotels found"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/hotels")
    public ResponseEntity<?> getAllHotels() {
        List<Hotel> hotels = hotelSv.getAllHotels();
        if (hotels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_REGISTERED_HOTELS_MESSAGE);
        }
        return ResponseEntity.ok(hotels);
    }

    @Operation(summary = "Endpoint para encontrar un hotel por ID", description = "Este método maneja la busqueda de un hotel por id. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación")
    @GetMapping("/hotels/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "No registered hotels found"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    public ResponseEntity<?> findHotelById(@PathVariable String id) {

        if (!id.matches("\\d+")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_MESSAGE);
        }

        Long hotelID = Long.parseLong(id);

        if (hotelID <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_MESSAGE);
        }

        Hotel hotel = hotelSv.findHotelById(hotelID);
        if (hotel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_HOTELS_FOUND_MESSAGE);
        }
        return ResponseEntity.ok(hotel);
    }

    @Operation(summary = "Endpoint para eliminar un hotel por ID", description = "Este método maneja la eliminación de un hotel por id. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación")
    @DeleteMapping("/hotels/delete/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid hotel ID"),
            @ApiResponse(responseCode = "404", description = "No registered hotels found"),
            @ApiResponse(responseCode = "409", description = "Hotel cannot be deleted because it has bookings"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    public ResponseEntity<?> deleteHotel(@PathVariable String id) {
        if (!id.matches("\\d+")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_MESSAGE);
        }

        Long hotelID = Long.parseLong(id);
        Hotel hotel = hotelSv.findHotelById(hotelID);
        if (hotel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_HOTELS_FOUND_MESSAGE);
        }

        // Verificar si el hotel tiene reservas asociadas
        if (!hotelSv.canHotelBeDeleted(hotelID)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Hotel cannot be deleted because it has bookings");
        }

        hotelSv.deleteHotel(hotelID);
        return ResponseEntity.ok().body("Hotel successfully deleted");
    }

    @Operation(summary = "Endpoint para editar un hotel por ID", description = "Este método maneja la modificación de un hotel por id. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid hotel ID"),
            @ApiResponse(responseCode = "404", description = "No registered hotel found"),
            @ApiResponse(responseCode = "409", description = "Hotel cannot be modified because it has bookings"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @PutMapping("/hotels/edit/{id}")
    public ResponseEntity<?> editHotel(@PathVariable String id,
                                       @RequestParam("name") String editName,
                                       @RequestParam("place") String editPlace) {
        if (!id.matches("\\d+")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_ID_MESSAGE);
        }

        if (editName == null ||!editName.matches(HOTEL_NAME_PATTERN)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_HOTEL_NAME);
        }

        if (editPlace == null ||!editPlace.matches(PLACE_PATTERN)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_PLACE_NAME);
        }

        Long hotelID = Long.parseLong(id);

        Hotel hotel = hotelSv.findHotelById(hotelID);
        if (hotel == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_HOTELS_FOUND_MESSAGE);
        }

        if (!hotelSv.canHotelBeModified(hotelID)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Hotel cannot be modified because it has bookings");
        }

        hotel.setName(editName);
        hotel.setPlace(editPlace);

        hotelSv.saveHotel(hotel);
        return ResponseEntity.ok().body("Hotel successfully updated");
    }

    @Operation(summary = "Endpoint para buscar hoteles filtrados por rango de fechas de disponibilidad, lugar y si esta reservado", description = "Este método maneja la busqueda de un hotel con diferentes parametros. Devuelve diferentes códigos de respuesta HTTP dependiendo del resultado de la operación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of hotels successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid date format or null parameters"),
            @ApiResponse(responseCode = "404", description = "No registered hotels found"),
            @ApiResponse(responseCode = "500", description = "Server error")})
    @GetMapping("/hotels/search")
    public ResponseEntity<?> getHotelsFiltered(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE) LocalDate disponibilityDateFrom,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE) LocalDate disponibilityDateTo,
            @RequestParam String place,
            @RequestParam Boolean isBooked) {
        if (place == null || !place.matches(PLACE_PATTERN)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(INVALID_PLACE_NAME);
        }

        if (isBooked == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The 'isBooked' parameter is required");
        }

        if (disponibilityDateFrom.isAfter(disponibilityDateTo)) {
            return ResponseEntity.badRequest().body(DATE_ORDER_ERROR_START);
        }

        if (disponibilityDateTo.isBefore(disponibilityDateFrom)){
            return ResponseEntity.badRequest().body(DATE_ORDER_ERROR_END);
        }

        List<Hotel> hotels = hotelSv.getHotelsFiltered(disponibilityDateFrom, disponibilityDateTo, place, isBooked);
        if (hotels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(NO_REGISTERED_HOTELS_MESSAGE);
        }
        return ResponseEntity.ok(hotels);
    }

    //Manejador de excepciones para capturar errores de formato de fecha y formato isBooked
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();

        if ("disponibilityDateFrom".equals(name) || "disponibilityDateTo".equals(name)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error when entering the date format, it must be yyyy-MM-dd.");
        } else if ("isBooked".equals(name)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The 'isBooked' parameter must be 'true' or 'false'");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en el servidor");
    }
}
