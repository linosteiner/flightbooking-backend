package ch.linosteiner.controller;

import ch.linosteiner.domain.Flight;
import ch.linosteiner.service.FlightService;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Controller("/flights")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
public class FlightController {

    private static final Logger LOG = LoggerFactory.getLogger(FlightController.class);
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @Get
    public Page<Flight> getFlights(
            @QueryValue Optional<String> departure,
            @QueryValue Optional<String> destination,
            @QueryValue Optional<LocalDate> date,
            @QueryValue Optional<String> time,
            @QueryValue Optional<String> airline,
            @QueryValue Optional<BigDecimal> maxPrice,
            @QueryValue Optional<Boolean> availableOnly,
            @QueryValue(defaultValue = "0") int page,
            @QueryValue(defaultValue = "6") int size) {

        LOG.info("Anfrage für Flugsuche erhalten (Von: {}, Nach: {}, Seite: {})",
                departure.orElse("Alle"), destination.orElse("Alle"), page);

        Pageable pageable = Pageable.from(page, size);
        return flightService.searchFlights(departure, destination, date, time, airline, maxPrice, availableOnly, pageable);
    }
}
