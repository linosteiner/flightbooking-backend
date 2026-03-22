package ch.linosteiner.service;

import ch.linosteiner.domain.Booking;
import ch.linosteiner.domain.Flight;
import ch.linosteiner.domain.UserEntity;
import ch.linosteiner.repository.BookingRepository;
import ch.linosteiner.repository.FlightRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class BookingService {
    private static final String USER_NOT_FOUND = "Benutzer nicht gefunden";
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserService userService;

    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userService = userService;
    }

    @Transactional
    public Booking createBooking(Long flightId, String username) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flug nicht gefunden"));
        UserEntity user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        if (flight.getAvailableTickets() <= 0) {
            throw new IllegalStateException("Ausgebucht");
        }

        flight.setAvailableTickets(flight.getAvailableTickets() - 1);
        flightRepository.update(flight);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        return bookingRepository.save(booking);
    }

    public List<Booking> getHistory(String username) {
        UserEntity user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        return bookingRepository.findByUserIdOrderByBookingTimestampDesc(user.getId());
    }

    @Transactional
    public void deleteAllBookingsForUser(String username) {
        UserEntity user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        List<Booking> bookings = bookingRepository.findByUserIdOrderByBookingTimestampDesc(user.getId());

        for (Booking booking : bookings) {
            Flight flight = booking.getFlight();
            flight.setAvailableTickets(flight.getAvailableTickets() + 1);
            flightRepository.update(flight);

            bookingRepository.delete(booking);
        }
    }
}
