package vn.mentora.mentora_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.mentora.mentora_api.domain.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingIdAndMenteeMenteeId(Long bookingId, Long menteeId);
}
