package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.dto.HotelDTO;
import com.hackaboss.agenciaviajes.model.Hotel;

import java.time.LocalDate;
import java.util.List;

public interface IHotelService {

    Hotel addHotel(HotelDTO hotelDTO);

    List<Hotel> getAllHotels();

    Hotel findHotelById(Long id);

    void deleteHotel(Long id);

    void saveHotel(Hotel hotel);

    boolean canHotelBeDeleted(Long hotelId);

    boolean canHotelBeModified(Long hotelId);

    List<Hotel> getHotelsFiltered(LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo, String place, boolean isBooked);


}
