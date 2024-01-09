package com.hackaboss.agenciaviajes.dto;

import com.hackaboss.agenciaviajes.model.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class HotelDTO {

    private String name;
    private String place;
    private List<Room> rooms;

}

