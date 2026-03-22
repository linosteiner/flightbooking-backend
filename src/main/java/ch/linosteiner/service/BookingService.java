package ch.linosteiner.service;

import ch.linosteiner.domain.Booking;
import ch.linosteiner.domain.Flight;
import ch.linosteiner.domain.UserEntity;
import ch.linosteiner.repository.BookingRepository;
import ch.linosteiner.repository.FlightRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class BookingService {
    private static final Logger LOG = LoggerFactory.getLogger(BookingService.class);
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
        LOG.debug("Starte Transaktion für Buchung (User: {}, Flug: {})", username, flightId);

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flug nicht gefunden"));
        UserEntity user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        if (flight.getAvailableTickets() <= 0) {
            LOG.warn("Buchung abgelehnt: Flug {} ist bereits ausgebucht.", flightId);
            throw new IllegalStateException("Ausgebucht");
        }

        flight.setAvailableTickets(flight.getAvailableTickets() - 1);
        flightRepository.update(flight);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlight(flight);
        Booking savedBooking = bookingRepository.save(booking);

        LOG.debug("Transaktion erfolgreich: Tickets für Flug {} um 1 reduziert.", flightId);
        return savedBooking;
    }

    public Page<Booking> getHistory(String username, Pageable pageable) {
        LOG.debug("Hole Historie für User {}", username);
        UserEntity user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));
        return bookingRepository.findByUserIdOrderByBookingTimestampDesc(user.getId(), pageable);
    }

    @Transactional
    public void deleteAllBookingsForUser(String username, Pageable pageable) {
        LOG.debug("Starte Transaktion zum Löschen aller Buchungen für User {}", username);
        UserEntity user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND));

        Page<Booking> bookings = bookingRepository.findByUserIdOrderByBookingTimestampDesc(user.getId(), pageable);
        int deleteCount = 0;

        for (Booking booking : bookings) {
            Flight flight = booking.getFlight();
            flight.setAvailableTickets(flight.getAvailableTickets() + 1);
            flightRepository.update(flight);
            bookingRepository.delete(booking);
            deleteCount++;
        }
        LOG.debug("Transaktion erfolgreich: {} Buchungen gelöscht und Tickets freigegeben.", deleteCount);
    }
}
