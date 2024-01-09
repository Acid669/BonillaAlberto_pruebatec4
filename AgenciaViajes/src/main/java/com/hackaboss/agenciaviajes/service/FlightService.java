package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.dto.FlightDTO;
import com.hackaboss.agenciaviajes.model.Flight;
import com.hackaboss.agenciaviajes.repositories.BookingFlightRepository;
import com.hackaboss.agenciaviajes.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class FlightService implements IFlightService {

    @Autowired
    private FlightRepository flightRepo;

    @Autowired
    private BookingFlightRepository bookingFlightRepo;


    //Método para crear un vuelo
    @Override
    public Flight addFlight(FlightDTO flightDTO) {

        Flight flight = new Flight();

        flight.setOrigin(flightDTO.getOrigin());
        flight.setDestination(flightDTO.getDestination());

        //Obtener las dos primeras letras del origen y destino
        String twoLettersOrigin = flight.getOrigin().isEmpty() ? "OO" : flight.getOrigin().substring(0, Math.min(2, flight.getOrigin().length()));
        String twoLettersDestination = flight.getDestination().isEmpty() ? "DD" : flight.getDestination().substring(0, Math.min(2, flight.getDestination().length()));

        //Generar los números del código de forma aleatoria
        Random random = new Random();
        String formattedNumber = String.format("%04d", random.nextInt(10000)); // Número entre 0 y 9999

        //Construir el código del vuelo
        String flightCode = twoLettersOrigin + twoLettersDestination + "-" + formattedNumber;
        flight.setFlightNumber(flightCode.toUpperCase());

        flight.setSeatType(flightDTO.getSeatType());
        flight.setFlightPrice(flightDTO.getFlightPrice());

        flight.setDate(flightDTO.getDate());

        return flightRepo.save(flight);
    }

    //Método para obtener todos los vuelos
    @Override
    public List<Flight> getAllFlights() {
        return flightRepo.findAll();
    }

    //Método para obtener un vuelo por su código
    @Override
    public Flight findFlightById(Long id) {
        return flightRepo.findById(id).orElse(null);
    }

    //Método para eliminar un vuelo
    @Override
    public void deleteFlight(Long id) {
        flightRepo.deleteById(id);
    }

    //Método para actualizar un vuelo
    @Override
    public void saveFlight(Flight flight) {
        flightRepo.save(flight);
    }

    //Método para verificar si un vuelo puede ser eliminado
    @Override
    public boolean canFlightBeDeleted(Long flightId) {
        return bookingFlightRepo.countByFlightId(flightId) == 0;
    }

    //Método para verificar si un vuelo puede ser modificado
    @Override
    public boolean canFlightBeModified(Long flightId) {
        return bookingFlightRepo.countByFlightId(flightId) == 0;
    }

    //Metodo para obtener una lista de vuelos filtrados por fecha, origen y destino
    @Override
    public List<Flight> getFilteredFlights(LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo, String origin, String destination) {
        return flightRepo.getFilteredFlights(disponibilityDateFrom, disponibilityDateTo, origin, destination);
    }

}
