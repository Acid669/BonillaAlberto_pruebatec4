package com.hackaboss.agenciaviajes.dto;

import com.hackaboss.agenciaviajes.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingFlightDTO {

    private LocalDate date;
    private String origin;
    private String destination;
    private String flightCode;
    private String seatType;
    private User passenger;

}
