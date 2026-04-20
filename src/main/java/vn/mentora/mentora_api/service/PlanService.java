package vn.mentora.mentora_api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.MentorshipsBenefit;
import vn.mentora.mentora_api.domain.MentorshipsBenefitId;
import vn.mentora.mentora_api.domain.PlanMentorship;
import vn.mentora.mentora_api.domain.PlanSession;
import vn.mentora.mentora_api.domain.request.ReqCreateMentorshipDTO;
import vn.mentora.mentora_api.domain.request.ReqCreateSessionDTO;
import vn.mentora.mentora_api.domain.response.ResPlanMentorshipDTO;
import vn.mentora.mentora_api.domain.response.ResPlanSessionDTO;
import vn.mentora.mentora_api.repository.MentorRepository;
import vn.mentora.mentora_api.repository.PlanMentorshipRepository;
import vn.mentora.mentora_api.repository.PlanSessionRepository;
import vn.mentora.mentora_api.util.constant.PlanStatusEnum;
import vn.mentora.mentora_api.util.constant.PlanTypeEnum;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class PlanService {

    private final PlanSessionRepository planSessionRepository;
    private final PlanMentorshipRepository planMentorshipRepository;
    private final MentorRepository mentorRepository;

    public PlanService(PlanSessionRepository planSessionRepository,
            PlanMentorshipRepository planMentorshipRepository,
            MentorRepository mentorRepository) {
        this.planSessionRepository = planSessionRepository;
        this.planMentorshipRepository = planMentorshipRepository;
        this.mentorRepository = mentorRepository;
    }

    @Transactional
    public ResPlanSessionDTO createSession(String email, ReqCreateSessionDTO dto) throws IdInvalidException {
        Mentor mentor = this.mentorRepository.findByUserEmail(email)
                .orElseThrow(() -> new IdInvalidException("Mentor không tồn tại!"));

        // Chỉ Mentor ACTIVE mới được tạo Plan
        if (mentor.getUser().getStatus() != UserStatusEnum.ACTIVE) {
            throw new IdInvalidException("Tài khoản chưa được Admin duyệt. Bạn không thể tạo Plan khi chưa ACTIVE.");
        }

        // Kiểm tra trùng tên trong cùng 1 mentor
        if (planSessionRepository.existsBySessionNameAndMentorMentorId(dto.getSessionName(), mentor.getMentorId())) {
            throw new IdInvalidException("Bạn đã có session plan tên '" + dto.getSessionName() + "' rồi!");
        }

        PlanSession planSession = new PlanSession();
        planSession.setSessionName(dto.getSessionName());
        planSession.setDescription(dto.getDescription());
        planSession.setCharge(dto.getCharge());
        planSession.setSessionsDuration(dto.getSessionsDuration());
        planSession.setPlanType(PlanTypeEnum.SESSION);
        planSession.setStatus(PlanStatusEnum.ACTIVE);
        planSession.setMentor(mentor);
        return convertToSessionDTO(this.planSessionRepository.save(planSession));
    }

    @Transactional
    public ResPlanMentorshipDTO createMentorship(String email, ReqCreateMentorshipDTO dto) throws IdInvalidException {
        Mentor mentor = this.mentorRepository.findByUserEmail(email)
                .orElseThrow(() -> new IdInvalidException("Mentor không tồn tại!"));

        // Chỉ Mentor ACTIVE mới được tạo Plan
        if (mentor.getUser().getStatus() != UserStatusEnum.ACTIVE) {
            throw new IdInvalidException("Tài khoản chưa được Admin duyệt. Bạn không thể tạo Plan khi chưa ACTIVE.");
        }

        // Kiểm tra trùng tên trong cùng 1 mentor
        if (planMentorshipRepository.existsByPlanNameAndMentorMentorId(dto.getPlanName(), mentor.getMentorId())) {
            throw new IdInvalidException("Bạn đã có mentorship plan tên '" + dto.getPlanName() + "' rồi!");
        }

        PlanMentorship planMentorship = new PlanMentorship();
        planMentorship.setPlanName(dto.getPlanName());
        planMentorship.setDescription(dto.getDescription());
        planMentorship.setCharge(dto.getCharge());
        planMentorship.setCallsPerMonth(dto.getCallsPerMonth());
        planMentorship.setMinutesPerCall(dto.getMinutesPerCall());
        planMentorship.setPlanType(PlanTypeEnum.MENTORSHIP);
        planMentorship.setStatus(PlanStatusEnum.ACTIVE);
        planMentorship.setMentor(mentor);

        List<MentorshipsBenefit> mentorshipsBenefits = new ArrayList<>();
        int order = 1;
        for (String s : dto.getBenefits()) {
            MentorshipsBenefit benefit = new MentorshipsBenefit();
            benefit.setBenefitContent(s);
            benefit.setId(new MentorshipsBenefitId(null, order++));
            benefit.setPlanMentorship(planMentorship);
            mentorshipsBenefits.add(benefit);
        }

        planMentorship.setBenefits(mentorshipsBenefits);
        return convertToMentorshipDTO(planMentorshipRepository.save(planMentorship));
    }

    public Map<String, Object> getPlansByMentor(Long mentorId) {
        List<ResPlanSessionDTO> sessions = planSessionRepository.findByMentorMentorId(mentorId)
                .stream().map(this::convertToSessionDTO).collect(Collectors.toList());

        List<ResPlanMentorshipDTO> mentorships = planMentorshipRepository.findByMentorMentorId(mentorId)
                .stream().map(this::convertToMentorshipDTO).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("sessions", sessions);
        result.put("mentorships", mentorships);
        return result;
    }

    private ResPlanSessionDTO convertToSessionDTO(PlanSession s) {
        ResPlanSessionDTO dto = new ResPlanSessionDTO();
        dto.setPlanId(s.getId());
        dto.setSessionName(s.getSessionName());
        dto.setDescription(s.getDescription());
        dto.setCharge(s.getCharge());
        dto.setSessionsDuration(s.getSessionsDuration());
        dto.setStatus(s.getStatus());
        return dto;
    }

    private ResPlanMentorshipDTO convertToMentorshipDTO(PlanMentorship m) {
        ResPlanMentorshipDTO dto = new ResPlanMentorshipDTO();
        dto.setPlanId(m.getId());
        dto.setPlanName(m.getPlanName());
        dto.setDescription(m.getDescription());
        dto.setCharge(m.getCharge());
        dto.setCallsPerMonth(m.getCallsPerMonth());
        dto.setMinutesPerCall(m.getMinutesPerCall());
        dto.setStatus(m.getStatus());
        dto.setBenefits(m.getBenefits().stream()
                .map(MentorshipsBenefit::getBenefitContent)
                .collect(Collectors.toList()));
        return dto;
    }

}
