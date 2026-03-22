package ch.linosteiner.controller;

import ch.linosteiner.domain.Booking;
import ch.linosteiner.domain.UserEntity;
import ch.linosteiner.service.BookingService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/bookings")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
public class BookingController {

    private static final Logger LOG = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Post
    public HttpResponse<Booking> createBooking(@Body BookingRequest request) {
        LOG.info("Buchungsanfrage erhalten: User '{}' für Flug-ID {}", request.username(), request.flightId());
        try {
            Booking booking = bookingService.createBooking(request.flightId(), request.username());
            LOG.info("Buchung erfolgreich erstellt mit ID: {}", booking.getId());
            return HttpResponse.created(booking);
        } catch (IllegalStateException e) {
            LOG.warn("Buchung fehlgeschlagen (Bad Request): {}", e.getMessage());
            return HttpResponse.badRequest();
        } catch (IllegalArgumentException e) {
            LOG.warn("Buchung fehlgeschlagen (Not Found): {}", e.getMessage());
            return HttpResponse.notFound();
        }
    }

    @Get("/{username}")
    public Page<Booking> getHistory(
            String username,
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "10") int size) {

        LOG.info("Lade Buchungshistorie für User '{}' (Seite: {})", username, page);
        Pageable pageable = Pageable.from(page, size);
        return bookingService.getHistory(username, pageable);
    }

    @Delete("/{username}")
    public HttpResponse<UserEntity> deleteAllBookings(String username) {
        LOG.info("Löschanfrage für alle Buchungen von User '{}' erhalten (Demo-Reset)", username);
        try {
            bookingService.deleteAllBookingsForUser(username, Pageable.unpaged());
            LOG.info("Alle Buchungen für User '{}' erfolgreich gelöscht.", username);
            return HttpResponse.ok();
        } catch (IllegalArgumentException _) {
            LOG.warn("Löschen fehlgeschlagen: User '{}' nicht gefunden.", username);
            return HttpResponse.notFound();
        }
    }

    @Serdeable
    public record BookingRequest(Long flightId, String username) {
    }
}
