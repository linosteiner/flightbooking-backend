package ch.linosteiner.controller;

import ch.linosteiner.domain.Flight;
import ch.linosteiner.service.FlightService;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller("/flights")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @Get
    public List<Flight> getFlights(
            @QueryValue Optional<String> departure,
            @QueryValue Optional<String> destination,
            @QueryValue Optional<LocalDate> date,
            @QueryValue Optional<String> time,
            @QueryValue Optional<String> airline,
            @QueryValue Optional<BigDecimal> maxPrice,
            @QueryValue Optional<Boolean> availableOnly) {

        return flightService.searchFlights(departure, destination, date, time, airline, maxPrice, availableOnly);
    }
}
