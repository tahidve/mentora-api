package vn.mentora.mentora_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.mentora.mentora_api.domain.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingIdAndMenteeMenteeId(Long bookingId, Long menteeId);

    Optional<Booking> findByPlanRegistrationRegistrationId(Long id);

    // đếm tổng số booking của 1 mentee (cho ResAdminMenteeDTO.totalBookings)
    long countByMenteeMenteeId(Long menteeId);

    // list bookings theo mentee
    List<Booking> findByMenteeMenteeId(Long menteeId);
}
