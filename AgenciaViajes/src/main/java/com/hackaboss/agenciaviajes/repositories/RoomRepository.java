package com.hackaboss.agenciaviajes.repositories;

import com.hackaboss.agenciaviajes.model.Hotel;
import com.hackaboss.agenciaviajes.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    //Define una consulta personalizada para encontrar una habitación disponible.
    @Query("SELECT r FROM Room r WHERE r.disponibilityDateFrom <= :dateFrom AND r.disponibilityDateTo >= :dateTo AND :dateFrom <= :dateTo")
    Room findAvailableRoom(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo);

    List<Room> findByHotelAndRoomType(Hotel hotelExist, String roomType);
}
