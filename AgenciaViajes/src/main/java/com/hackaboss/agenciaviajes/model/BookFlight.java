package com.hackaboss.agenciaviajes.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BookFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String origin;
    private String destination;
    private String flightCode;
    private Integer peopleQ;
    private String seatType;
    private Double price;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_user")
    private User passenger;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_flight")
    private Flight flight;

}
