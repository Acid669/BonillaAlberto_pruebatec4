package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.dto.HotelDTO;
import com.hackaboss.agenciaviajes.model.Hotel;
import com.hackaboss.agenciaviajes.repositories.BookingHotelRepository;
import com.hackaboss.agenciaviajes.repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class HotelService implements IHotelService {

    @Autowired
    private HotelRepository hotelRepo;

    @Autowired
    private BookingHotelRepository bookingHotelRepo;

    //Método para añadir un nuevo hotel
    @Override
    public Hotel addHotel(HotelDTO hotelDTO) {
        Hotel hotel = new Hotel();

        hotel.setName(hotelDTO.getName());
        hotel.setPlace(hotelDTO.getPlace());
        hotel.setBooked(false);

        //Obtener las dos primeras letras del nombre y lugar
        String twoLettersName = hotel.getName().isEmpty() ? "OO" : hotel.getName().substring(0, Math.min(2, hotel.getName().length()));
        String twoLettersPlace = hotel.getPlace().isEmpty() ? "DD" : hotel.getPlace().substring(0, Math.min(2, hotel.getPlace().length()));

        //Generar los números del código de forma aleatoria
        Random random = new Random();
        String formattedNumber = String.format("%04d", random.nextInt(10000)); // Número entre 0 y 9999

        //Construir el código del hotel
        String hotelCode = twoLettersName + twoLettersPlace + "-" + formattedNumber;
        hotel.setHotelCode(hotelCode.toUpperCase());

        return hotelRepo.save(hotel);
    }


    //Método para obtener todos los hoteles
    @Override
    public List<Hotel> getAllHotels() {
        return hotelRepo.findAll();
    }


    //Método para buscar un hotel por su ID
    @Override
    public Hotel findHotelById(Long id) {
        return hotelRepo.findById(id).orElse(null);
    }


    //Método para eliminar un hotel por su ID
    @Override
    public void deleteHotel(Long id) {
        hotelRepo.deleteById(id);
    }

    //Método para guardar la información de un hotel
    @Override
    public void saveHotel(Hotel hotel) {
        hotelRepo.save(hotel);
    }

    //Método para verificar si un hotel puede ser eliminado
    @Override
    public boolean canHotelBeDeleted(Long hotelId) {
        return bookingHotelRepo.countByHotelId(hotelId) == 0;
    }

    //Método para verificar si un hotel puede ser modificado
    @Override
    public boolean canHotelBeModified(Long hotelId) {
        return bookingHotelRepo.countByHotelId(hotelId) == 0;
    }

    //Método para obtener hoteles filtrados por fechas de disponibilidad, lugar y estado de reserva
    @Override
    public List<Hotel> getHotelsFiltered(LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo, String place, boolean isBooked) {
        return hotelRepo.getHotelsFiltered(disponibilityDateFrom, disponibilityDateTo, place, isBooked);
    }
}
