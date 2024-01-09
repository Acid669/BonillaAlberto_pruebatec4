package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.model.Flight;
import com.hackaboss.agenciaviajes.dto.FlightDTO;

import java.time.LocalDate;
import java.util.List;

public interface IFlightService {

    Flight addFlight(FlightDTO flightDTO);

    List<Flight> getAllFlights();

    Flight findFlightById(Long id);

    void deleteFlight(Long id);

    void saveFlight(Flight flight);

    boolean canFlightBeDeleted(Long flightId);

    boolean canFlightBeModified(Long flightId);

    List<Flight> getFilteredFlights(LocalDate disponibilityDateFrom, LocalDate disponibilityDateTo, String origin, String destination);
}
