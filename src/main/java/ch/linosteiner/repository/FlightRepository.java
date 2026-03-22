package ch.linosteiner.repository;

import ch.linosteiner.domain.Flight;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.repository.jpa.JpaSpecificationExecutor;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long>, JpaSpecificationExecutor<Flight> {
}
