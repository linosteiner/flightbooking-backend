package ch.linosteiner.repository;

import ch.linosteiner.domain.Booking;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByUserIdOrderByBookingTimestampDesc(Long userId, Pageable pageable);
}
