package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.model.Room;

import java.util.List;

public interface IRoomService {

    Room addRoom(Room room);

    List<Room> getAllRooms();
}
