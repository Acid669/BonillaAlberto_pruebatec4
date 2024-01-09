package com.hackaboss.agenciaviajes.repositories;

import com.hackaboss.agenciaviajes.model.BookHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingHotelRepository extends JpaRepository<BookHotel, Long> {

    // Método para contar las reservas existentes de un hotel específico por su ID
    int countByHotelId(Long hotelId);
}
