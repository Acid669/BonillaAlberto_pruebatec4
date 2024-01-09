package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.dto.BookingFlightDTO;
import com.hackaboss.agenciaviajes.model.BookFlight;

import java.util.List;

public interface IBookingFlightService {

    BookFlight createBookingFlight(BookingFlightDTO bookingFlightDTO);

    List<BookFlight> getAllBookFlights();

    void deleteBookFlight(Long id);

    BookFlight findFlightById(Long flightID);
}
