package vn.mentora.mentora_api.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.Slot;
import vn.mentora.mentora_api.util.constant.SlotStatusEnum;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    // Lấy tất cả slot của mentor theo status
    List<Slot> findByMentorMentorIdAndStatus(Long mentorId, SlotStatusEnum status);

    // Lấy tất cả slot của mentor có date > date truyền và theo status
    List<Slot> findByMentorMentorIdAndDateAfterAndStatus(Long mentorId, LocalDate date, SlotStatusEnum status);

    // Check nhanh mentor đã có slot cùng ngày + cùng giờ bắt đầu
    boolean existsByMentorMentorIdAndDateAndStartTime(Long mentorId, LocalDate date, LocalTime startTime);
}
