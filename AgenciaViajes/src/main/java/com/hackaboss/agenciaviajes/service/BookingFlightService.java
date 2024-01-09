package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.dto.BookingFlightDTO;
import com.hackaboss.agenciaviajes.model.BookFlight;
import com.hackaboss.agenciaviajes.model.Flight;
import com.hackaboss.agenciaviajes.model.User;
import com.hackaboss.agenciaviajes.repositories.BookingFlightRepository;
import com.hackaboss.agenciaviajes.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingFlightService implements IBookingFlightService {
    @Autowired
    private BookingFlightRepository bookingFlightRepo;

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private IUserService userSv;

    //Método para crear una reserva de vuelo
    @Override
    public BookFlight createBookingFlight(BookingFlightDTO flightDTO) {
        BookFlight bookFlight = new BookFlight();

        // Comprobar si el vuelo existe según el código de vuelo
        Flight flightExist = flightRepo.findByFlightNumber(flightDTO.getFlightCode());
        if (flightExist == null) {
            return null; // El vuelo no existe
        }

        // Comprobar si el usuario existe o crear un nuevo usuario
        User userFromDTO = flightDTO.getPassenger();
        User userExist = userSv.findByEmail(userFromDTO.getEmail());
        if (userExist == null) {
            userExist = userSv.addUser(userFromDTO);
        }

        // Verificar si ya existe una reserva para el mismo usuario, vuelo y fecha
        if (bookingFlightRepo.existsByPassengerAndFlightAndDate(userExist, flightExist, flightDTO.getDate())) {
            return null; // Ya existe una reserva para el mismo usuario, vuelo y fecha
        }

        // Verificar que los campos origin, destination, flightCode, seatType y date coincidan con el vuelo
        if (!flightDTO.getOrigin().equals(flightExist.getOrigin()) ||
                !flightDTO.getDestination().equals(flightExist.getDestination()) ||
                !flightDTO.getFlightCode().equals(flightExist.getFlightNumber()) ||
                !flightDTO.getSeatType().equals(flightExist.getSeatType()) ||
                !flightDTO.getDate().isEqual(flightExist.getDate())) {
            return null; // Los campos no coinciden con el vuelo
        }

        // Verificar la fecha de la reserva con la fecha del vuelo
        if (!flightDTO.getDate().isEqual(flightExist.getDate())) {
            return null; // La fecha de reserva no coincide con la fecha del vuelo
        }

        // Construir los detalles de la reserva
        bookFlight.setFlightCode(flightExist.getFlightNumber());
        bookFlight.setOrigin(flightExist.getOrigin());
        bookFlight.setDestination(flightExist.getDestination());
        bookFlight.setPeopleQ(1);
        bookFlight.setSeatType(flightDTO.getSeatType());
        bookFlight.setDate(flightDTO.getDate());
        bookFlight.setPrice(flightExist.getFlightPrice());
        bookFlight.setPassenger(userExist);
        bookFlight.setFlight(flightExist);

        return bookingFlightRepo.save(bookFlight);
    }



    //Implementación del método para obtener todas las reservas de vuelo
    @Override
    public List<BookFlight> getAllBookFlights() {
        return bookingFlightRepo.findAll();
    }

    @Override
    public void deleteBookFlight(Long id) {
        bookingFlightRepo.deleteById(id);
    }

    @Override
    public BookFlight findFlightById(Long flightID) {
        return bookingFlightRepo.findById(flightID).orElse(null);
    }


}
