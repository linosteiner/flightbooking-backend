package ch.linosteiner.controller;

import ch.linosteiner.domain.Booking;
import ch.linosteiner.domain.UserEntity;
import ch.linosteiner.service.BookingService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;

@Controller("/bookings")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED) // Protects booking endpoints
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Post
    public HttpResponse<Booking> createBooking(@Body BookingRequest request) {
        try {
            Booking booking = bookingService.createBooking(request.flightId(), request.username());
            return HttpResponse.created(booking);
        } catch (IllegalStateException _) {
            return HttpResponse.badRequest();
        } catch (IllegalArgumentException _) {
            return HttpResponse.notFound();
        }
    }

    @Get("/{username}")
    public List<Booking> getHistory(String username) {
        return bookingService.getHistory(username);
    }

    @Delete("/{username}")
    public HttpResponse<UserEntity> deleteAllBookings(String username) {
        try {
            bookingService.deleteAllBookingsForUser(username);
            return HttpResponse.ok();
        } catch (IllegalArgumentException _) {
            return HttpResponse.notFound();
        }
    }

    @Serdeable
    public record BookingRequest(Long flightId, String username) {
    }
}
