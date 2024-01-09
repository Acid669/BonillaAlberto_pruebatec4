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
public class BookingHotelDTO {

    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String place;
    private String hotelCode;
    private int peopleQ;
    private String roomType;
    private User host;

}
