package com.hackaboss.agenciaviajes.repositories;

import com.hackaboss.agenciaviajes.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    //Método para buscar un hotel por su código de hotel.
    Hotel findByHotelCode(String hotelCode);

    //Consulta personalizada que usa JPQL para obtener hoteles según varios criterios.
    //La consulta selecciona hoteles únicos (DISTINCT) donde:
    // 1. El lugar del hotel coincide con el parámetro 'place'.
    // 2. El estado de reserva del hotel coincide con 'isBooked'.
    // 3. Las habitaciones están disponibles en el rango de fechas proporcionado.
    @Query("SELECT DISTINCT h " +
            "FROM Hotel h " +
            "JOIN h.rooms r " +
            "WHERE h.place = :place " +
            "  AND h.isBooked = :isBooked " +
            "  AND r.disponibilityDateFrom  BETWEEN :disponibilityDateFrom  AND :disponibilityDateTo ")
    List<Hotel> getHotelsFiltered(LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo, String place, boolean isBooked);

}
