package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.repositories.RoomRepository;
import com.hackaboss.agenciaviajes.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService implements IRoomService {

    @Autowired
    private RoomRepository roomRepo;

    // Método para añadir una nueva habitación al repositorio
    @Override
    public Room addRoom(Room room) {
        return roomRepo.save(room);
    }

    // Método para obtener todas las habitaciones registradas
    @Override
    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }
}
