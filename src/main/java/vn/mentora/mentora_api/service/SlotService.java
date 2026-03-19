package vn.mentora.mentora_api.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.Slot;
import vn.mentora.mentora_api.domain.request.ReqCreateSlotDTO;
import vn.mentora.mentora_api.domain.response.ResSlotDTO;
import vn.mentora.mentora_api.repository.MentorRepository;
import vn.mentora.mentora_api.repository.SlotRepository;
import vn.mentora.mentora_api.util.constant.SlotStatusEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class SlotService {

    private final SlotRepository slotRepository;
    private final MentorRepository mentorRepository;

    public SlotService(SlotRepository slotRepository, MentorRepository mentorRepository) {
        this.slotRepository = slotRepository;
        this.mentorRepository = mentorRepository;
    }

    private void validateTimeOverlap(Long mentorId, LocalDate date, LocalTime newStart, LocalTime newEnd)
            throws IdInvalidException {
        List<Slot> currentSlots = this.slotRepository.findByMentorMentorIdAndDate(mentorId, date);
        for (Slot oldSlot : currentSlots) {
            // CANCELLED là terminal state → không chiếm chỗ nữa
            // Nếu sửa thành "reopen slot", phải xóa dòng này và validate khi reopen
            if (oldSlot.getStatus() == SlotStatusEnum.CANCELLED)
                continue;
            /*
             * Ý tưởng thuật toán:
             * Hai khoảng thời gian KHÔNG trùng nhau khi và chỉ khi:
             * Khoảng mới kết thúc trước khi khoảng cũ bắt đầu.
             * Hoặc khoảng mới bắt đầu sau khi khoảng cũ kết thúc.
             * Chỉ cần một trong hai điều kiện trong if sai thì thời gian slot sẽ chồng lên
             * nhau
             */
            if (newStart.isBefore(oldSlot.getEndTime()) && newEnd.isAfter(oldSlot.getStartTime())) {
                throw new IdInvalidException("Khung giờ bạn chọn bị trùng với một slot đã tồn tại ("
                        + oldSlot.getStartTime() + " - " + oldSlot.getEndTime() + ")");
            }
        }
    }

    private ResSlotDTO toDTO(Slot slot) {
        ResSlotDTO dto = new ResSlotDTO();
        dto.setSlotId(slot.getSlotId());
        dto.setMentorId(slot.getMentor().getMentorId());
        dto.setDate(slot.getDate());
        dto.setStartTime(slot.getStartTime());
        dto.setEndTime(slot.getEndTime());
        dto.setStatus(slot.getStatus());
        return dto;
    }

    public ResSlotDTO createSlot(String email, ReqCreateSlotDTO dto) throws IdInvalidException {
        Mentor mentor = this.mentorRepository.findByUserEmail(email)
                .orElseThrow(() -> new IdInvalidException("Mentor không tồn tại!"));

        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new IdInvalidException("Giờ bắt đầu phải trước giờ kết thúc!");
        }

        if (dto.getDate().isBefore(LocalDate.now().plusDays(1))) {
            throw new IdInvalidException("Slot phải được tạo từ ngày mai trở đi!");
        }

        this.validateTimeOverlap(mentor.getMentorId(), dto.getDate(),
                dto.getStartTime(), dto.getEndTime());

        Slot slot = new Slot();
        slot.setMentor(mentor);
        slot.setDate(dto.getDate());
        slot.setEndTime(dto.getEndTime());
        slot.setStartTime(dto.getStartTime());
        slot.setStatus(SlotStatusEnum.AVAILABLE);

        return toDTO(this.slotRepository.save(slot));
    }

    public List<ResSlotDTO> getAvailableSlot(Long mentorId) {
        return this.slotRepository.findByMentorMentorIdAndDateAfterAndStatus(mentorId, LocalDate.now(),
                SlotStatusEnum.AVAILABLE)
                .stream().map(this::toDTO).toList();
    }

    public void markAsBooked(Long slotId) throws IdInvalidException {
        Slot slot = this.slotRepository.findById(slotId)
                .orElseThrow(() -> new IdInvalidException("Slot không tồn tại"));
        slot.setStatus(SlotStatusEnum.BOOKED);
        this.slotRepository.save(slot);
    }

}
