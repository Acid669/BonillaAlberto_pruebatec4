package com.hackaboss.agenciaviajes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlightDTO {

    private String origin;
    private String destination;
    private String seatType;
    private Double flightPrice;
    private LocalDate date;

}
