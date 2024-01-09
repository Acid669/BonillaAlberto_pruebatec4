package com.hackaboss.agenciaviajes.repositories;

import com.hackaboss.agenciaviajes.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    //Consulta personalizada para obtener todos los vuelos en un rango de fechas y con origen y destino específicos.
    @Query("SELECT DISTINCT fli" +
            " from  Flight fli" +
            " where fli.date between :date1 and  :date2 and fli.origin = :origin and fli.destination = :destination")
    List<Flight> getFilteredFlights(LocalDate date1, LocalDate date2, String origin, String destination);

    //Método para encontrar un vuelo por su número de vuelo.
    Flight findByFlightNumber(String flightCode);

}
