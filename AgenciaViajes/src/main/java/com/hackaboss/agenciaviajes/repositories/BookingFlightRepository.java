package com.hackaboss.agenciaviajes.repositories;

import com.hackaboss.agenciaviajes.model.BookFlight;
import com.hackaboss.agenciaviajes.model.Flight;
import com.hackaboss.agenciaviajes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BookingFlightRepository extends JpaRepository<BookFlight, Long> {

    // Método para contar las reservas existentes de un vuelo específico por su ID
    int countByFlightId(Long flightId);

    // Método para verificar si existe una reserva con un usuario, vuelo y fecha específicos
    boolean existsByPassengerAndFlightAndDate(User userExist, Flight flightExist, LocalDate date);
}
