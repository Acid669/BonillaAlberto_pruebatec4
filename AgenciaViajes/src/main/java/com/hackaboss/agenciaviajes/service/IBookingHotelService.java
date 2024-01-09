package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.model.BookFlight;
import com.hackaboss.agenciaviajes.model.BookHotel;
import com.hackaboss.agenciaviajes.dto.BookingHotelDTO;

import java.util.List;

public interface IBookingHotelService {

    BookHotel createBookHotel(BookingHotelDTO bookingHotelDto);

    List<BookHotel> getAllBookHotels();

    void deleteBookHotel(Long id);

    BookHotel findBookHotelById(Long flightID);
}
