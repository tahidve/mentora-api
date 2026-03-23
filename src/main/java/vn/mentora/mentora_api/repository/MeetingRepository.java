package vn.mentora.mentora_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.Meeting;
import vn.mentora.mentora_api.util.constant.MeetingStatusEnum;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long>, JpaSpecificationExecutor<Meeting> {

    List<Meeting> findBySlotMentorMentorId(Long mentorId);

    List<Meeting> findByInvoiceMenteeMenteeId(Long menteeId);

    List<Meeting> findBySlotMentorMentorIdAndStatus(Long mentorid, MeetingStatusEnum status);

}
