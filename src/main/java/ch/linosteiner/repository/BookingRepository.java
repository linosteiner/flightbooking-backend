package ch.linosteiner.repository;

import ch.linosteiner.domain.Booking;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
//    @Join(value = "flight", type = Join.Type.FETCH)
    List<Booking> findByUserIdOrderByBookingTimestampDesc(Long userId);
}
