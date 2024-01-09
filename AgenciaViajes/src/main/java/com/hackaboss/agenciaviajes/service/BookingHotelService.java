package com.hackaboss.agenciaviajes.service;

import com.hackaboss.agenciaviajes.dto.BookingHotelDTO;
import com.hackaboss.agenciaviajes.model.BookHotel;
import com.hackaboss.agenciaviajes.model.Hotel;
import com.hackaboss.agenciaviajes.model.Room;
import com.hackaboss.agenciaviajes.model.User;
import com.hackaboss.agenciaviajes.repositories.BookingHotelRepository;
import com.hackaboss.agenciaviajes.repositories.HotelRepository;
import com.hackaboss.agenciaviajes.repositories.RoomRepository;
import com.hackaboss.agenciaviajes.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingHotelService implements IBookingHotelService {

    @Autowired
    private BookingHotelRepository bookHotelRepo;

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private HotelRepository hotelRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IRoomService roomSv;

    @Autowired
    private IUserService userService;

    @Override
    public BookHotel createBookHotel(BookingHotelDTO bookingHotelDto) {
        BookHotel bookHotel = new BookHotel();
        Hotel hotelExist = hotelRepo.findByHotelCode(bookingHotelDto.getHotelCode());

        // Verificar que el hotel existe y no está reservado
        if (hotelExist == null || hotelExist.isBooked()) {
            return null; // Hotel no existe o ya está reservado
        }

        // Verificar que el lugar (place) coincida
        if (!hotelExist.getPlace().equals(bookingHotelDto.getPlace())) {
            return null; // El lugar (place) no coincide
        }

        // Verificar que el tipo de habitación sea el correcto
        List<Room> rooms = roomRepo.findByHotelAndRoomType(hotelExist, bookingHotelDto.getRoomType());
        if (rooms.isEmpty()) {
            return null; // Tipo de habitación no válido
        }

        // Verificar que las fechas de reserva estén dentro del rango de disponibilidad
        LocalDate disponibilityDateFrom = rooms.get(0).getDisponibilityDateFrom();
        LocalDate disponibilityDateTo = rooms.get(0).getDisponibilityDateTo();

        if (bookingHotelDto.getDateFrom().isBefore(disponibilityDateFrom) ||
                bookingHotelDto.getDateTo().isAfter(disponibilityDateTo) ||
                bookingHotelDto.getDateFrom().isAfter(bookingHotelDto.getDateTo())) {
            return null; // Fechas no válidas o fuera del rango de disponibilidad
        }

        // Creación o actualización del usuario solo si la reserva es posible
        User user = bookingHotelDto.getHost();
        User existingUser = userService.findByEmail(user.getEmail());
        if (existingUser == null) {
            existingUser = userService.addUser(user); // Crear nuevo usuario
        } else {
            // Actualizar los datos del usuario existente, si es necesario
        }

        // Configuración de la reserva
        hotelExist.setBooked(true);
        hotelRepo.save(hotelExist);

        bookHotel.setHotelCode(hotelExist.getHotelCode());
        bookHotel.setDateFrom(bookingHotelDto.getDateFrom());
        bookHotel.setDateTo(bookingHotelDto.getDateTo());
        int nights = (int) ChronoUnit.DAYS.between(bookingHotelDto.getDateFrom(), bookingHotelDto.getDateTo());
        bookHotel.setNights(nights);
        bookHotel.setRoomType(bookingHotelDto.getRoomType());
        bookHotel.setPeopleQ(bookingHotelDto.getPeopleQ());
        bookHotel.setPrice(rooms.get(0).getRoomPrice() * nights);

        // Asociar el hotel y el usuario con la reserva
        bookHotel.setHotel(hotelExist);
        bookHotel.setUser(existingUser);
        bookHotelRepo.save(bookHotel);

        return bookHotel;
    }




    @Override
    public List<BookHotel> getAllBookHotels() {
        return bookHotelRepo.findAll();
    }

    @Override
    public void deleteBookHotel(Long id) {
        bookHotelRepo.deleteById(id);
    }

    @Override
    public BookHotel findBookHotelById(Long hotelId) {
        return bookHotelRepo.findById(hotelId).orElse(null);
    }

}
