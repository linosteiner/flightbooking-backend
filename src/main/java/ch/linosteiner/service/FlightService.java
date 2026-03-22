package ch.linosteiner.service;

import ch.linosteiner.domain.Flight;
import ch.linosteiner.repository.FlightRepository;
import io.micronaut.data.repository.jpa.criteria.QuerySpecification;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Singleton
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> searchFlights(
            Optional<String> departure, Optional<String> destination,
            Optional<LocalDate> date, Optional<String> time,
            Optional<String> airline, Optional<BigDecimal> maxPrice,
            Optional<Boolean> availableOnly
    ) {
        QuerySpecification<Flight> spec = QuerySpecification.where((QuerySpecification<Flight>) null);

        if (departure.isPresent() && !departure.get().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("departureCity")), "%" + departure.get().toLowerCase() + "%"));
        }

        if (destination.isPresent() && !destination.get().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("destinationCity")), "%" + destination.get().toLowerCase() + "%"));
        }

        if (date.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("departureDate"), date.get()));
        }

        if (time.isPresent() && !time.get().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("departureTime"), LocalTime.parse(time.get())));
        }

        if (airline.isPresent() && !airline.get().isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("airline")), "%" + airline.get().toLowerCase() + "%"));
        }

        if (maxPrice.isPresent()) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice.get()));
        }

        if (availableOnly.isPresent() && availableOnly.get()) {
            spec = spec.and((root, query, cb) -> cb.greaterThan(root.get("availableTickets"), 0));
        }

        return flightRepository.findAll(spec);
    }
}
