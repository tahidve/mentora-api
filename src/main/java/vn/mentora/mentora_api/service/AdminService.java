package vn.mentora.mentora_api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.domain.Specification;
import vn.mentora.mentora_api.domain.Booking;
import vn.mentora.mentora_api.domain.Invoice;
import vn.mentora.mentora_api.domain.Mentee;
import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.Skill;
import vn.mentora.mentora_api.domain.User;
import vn.mentora.mentora_api.domain.response.ResAdminInvoiceDTO;
import vn.mentora.mentora_api.domain.response.ResAdminMenteeDTO;
import vn.mentora.mentora_api.domain.response.ResAdminMentorDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
import vn.mentora.mentora_api.repository.BookingRepository;
import vn.mentora.mentora_api.repository.InvoiceRepository;
import vn.mentora.mentora_api.repository.MenteeRepository;
import vn.mentora.mentora_api.repository.MentorRepository;
import vn.mentora.mentora_api.repository.UserRepository;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class AdminService {

    private final MenteeRepository menteeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final InvoiceRepository invoiceRepository;

    public AdminService(
            MenteeRepository menteeRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository,
            MentorRepository mentorRepository,
            InvoiceRepository invoiceRepository) {
        this.menteeRepository = menteeRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.mentorRepository = mentorRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public ResultPaginationDTO getAllMentees(Pageable pageable) {
        Page<Mentee> page = this.menteeRepository.findAll(pageable);

        List<ResAdminMenteeDTO> dtoList = new ArrayList<>();
        for (Mentee mentee : page.getContent()) {
            dtoList.add(convertToMenteeDTO(mentee));
        }

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(dtoList);

        return result;
    }

    public ResAdminMenteeDTO getMenteeDetail(Long menteeId) throws IdInvalidException {
        Mentee mentee = this.menteeRepository.findById(menteeId)
                .orElseThrow(() -> new IdInvalidException("Mentee không tồn tại"));
        return this.convertToMenteeDTO(mentee);
    }

    @Transactional
    public ResAdminMenteeDTO toggleBanMentee(Long menteeId) throws IdInvalidException {
        Mentee mentee = this.menteeRepository.findById(menteeId)
                .orElseThrow(() -> new IdInvalidException("Mentee không tồn tại"));

        User user = mentee.getUser();

        if (user.getStatus() == UserStatusEnum.BANNED) {
            // Unban → trả về ACTIVE
            user.setStatus(UserStatusEnum.ACTIVE);
        } else if (user.getStatus() == UserStatusEnum.ACTIVE) {
            // Ban → xóa refreshToken
            user.setStatus(UserStatusEnum.BANNED);
            user.setRefreshToken(null);
        } else {
            // PENDING, INACTIVE... không cho toggle
            throw new IdInvalidException(
                    "Chỉ có thể ban/unban tài khoản ACTIVE hoặc BANNED (status hiện tại: " + user.getStatus() + ")");
        }

        this.userRepository.save(user);
        return this.convertToMenteeDTO(mentee);
    }

    @Transactional
    public ResAdminMentorDTO toggleBanMentor(Long mentorId) throws IdInvalidException {
        Mentor mentor = this.mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IdInvalidException("Mentor không tồn tại"));

        User user = mentor.getUser();

        if (user.getStatus() == UserStatusEnum.BANNED) {
            user.setStatus(UserStatusEnum.ACTIVE);
        } else if (user.getStatus() == UserStatusEnum.ACTIVE) {
            user.setStatus(UserStatusEnum.BANNED);
            user.setRefreshToken(null);
        } else {
            throw new IdInvalidException(
                    "Chỉ có thể ban/unban tài khoản ACTIVE hoặc BANNED (status hiện tại: " + user.getStatus() + ")");
        }

        this.userRepository.save(user);
        return this.convertToMentorDTO(mentor);
    }

    public ResultPaginationDTO getAllMentors(Pageable pageable) {
        Page<Mentor> page = this.mentorRepository.findAll(pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);
        List<Mentor> mentors = page.getContent();
        List<ResAdminMentorDTO> mentorDTOs = new ArrayList<>();
        for (Mentor mentor : mentors) {
            mentorDTOs.add(this.convertToMentorDTO(mentor));
        }
        result.setResult(mentorDTOs);

        return result;
    }

    public ResAdminMentorDTO getMentorDetail(Long mentorId) throws IdInvalidException {
        Mentor mentor = this.mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IdInvalidException("Mentor không tồn tại"));
        return this.convertToMentorDTO(mentor);
    }

    public ResultPaginationDTO getPendingMentors(Pageable pageable) {
        Page<Mentor> page = this.mentorRepository.findByUserStatus(UserStatusEnum.PENDING, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);
        List<Mentor> mentors = page.getContent();
        List<ResAdminMentorDTO> mentorDTOs = new ArrayList<>();
        for (Mentor mentor : mentors) {
            mentorDTOs.add(this.convertToMentorDTO(mentor));
        }
        result.setResult(mentorDTOs);

        return result;
    }

    @Transactional
    public ResAdminMentorDTO approveMentor(Long mentorId) throws IdInvalidException {
        Mentor mentor = this.mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IdInvalidException("Mentor không tồn tại"));

        if (mentor.getRejectedAt() != null) {
            throw new IdInvalidException("Hồ sơ này đang chờ Mentor cập nhật vì đã bị từ chối trước đó.");
        }

        if (mentor.getUser().getStatus() != UserStatusEnum.PENDING) {
            throw new IdInvalidException("Mentor không ở trạng thái chờ duyệt");
        }

        User user = mentor.getUser();
        user.setStatus(UserStatusEnum.ACTIVE);

        mentor.setRejectionReason(null);
        mentor.setRejectedAt(null);

        this.userRepository.save(user);
        this.mentorRepository.save(mentor);

        System.out.println("[MOCK EMAIL] Email " + user.getEmail() + " được duyệt đã được gửi");

        return this.convertToMentorDTO(mentor);
    }

    @Transactional
    public ResAdminMentorDTO rejectMentor(Long mentorId,
            vn.mentora.mentora_api.domain.request.ReqAdminReviewMentorDTO dto) throws IdInvalidException {
        Mentor mentor = this.mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IdInvalidException("Mentor không tồn tại"));

        if (mentor.getRejectedAt() != null) {
            throw new IdInvalidException("Hồ sơ này đang chờ Mentor cập nhật vì đã bị từ chối trước đó.");
        }

        if (mentor.getUser().getStatus() != UserStatusEnum.PENDING) {
            throw new IdInvalidException("Mentor không ở trạng thái chờ duyệt");
        }

        mentor.setRejectionReason(dto.getReason());
        mentor.setRejectedAt(java.time.Instant.now());

        this.mentorRepository.save(mentor);

        System.out.println("[MOCK EMAIL] Yêu cầu từ chối " + mentor.getUser().getEmail() + " đã được gửi, lý do: "
                + dto.getReason());
        return this.convertToMentorDTO(mentor);
    }

    public ResultPaginationDTO getAllInvoices(Specification<Invoice> spec, Pageable pageable) {
        Page<Invoice> page = this.invoiceRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        result.setMeta(meta);

        List<ResAdminInvoiceDTO> dtoList = new ArrayList<>();
        for (Invoice invoice : page.getContent()) {
            dtoList.add(convertToInvoiceDTO(invoice));
        }
        result.setResult(dtoList);

        return result;
    }

    private ResAdminInvoiceDTO convertToInvoiceDTO(Invoice invoice) {
        ResAdminInvoiceDTO dto = new ResAdminInvoiceDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setPaymentStatus(invoice.getPaymentStatus());
        dto.setAmountSubtotal(invoice.getAmountSubtotal());
        dto.setAmountTotal(invoice.getAmountTotal());
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setPaidTime(invoice.getPaidTime());
        dto.setCreatedAt(invoice.getCreatedAt());

        // Mentee info
        if (invoice.getMentee() != null && invoice.getMentee().getUser() != null) {
            User menteeUser = invoice.getMentee().getUser();
            dto.setMenteeName(menteeUser.getFirstName() + " " + menteeUser.getLastName());
            dto.setMenteeEmail(menteeUser.getEmail());
        }

        // Lấy mentorName: Invoice → PlanRegistration → Booking → Plan → Mentor
        // Vì PlanRegistration không có FK thẳng tới Mentor (theo EERD)
        if (invoice.getPlanRegistration() != null) {
            java.util.Optional<Booking> bookingOpt = this.bookingRepository
                    .findByPlanRegistrationRegistrationId(invoice.getPlanRegistration().getRegistrationId());
            if (bookingOpt.isPresent() && bookingOpt.get().getPlan() != null) {
                Mentor mentor = bookingOpt.get().getPlan().getMentor();
                if (mentor != null && mentor.getUser() != null) {
                    User mentorUser = mentor.getUser();
                    dto.setMentorName(mentorUser.getFirstName() + " " + mentorUser.getLastName());
                }
            }
        }

        return dto;
    }

    private ResAdminMenteeDTO convertToMenteeDTO(Mentee mentee) {
        ResAdminMenteeDTO dto = new ResAdminMenteeDTO();
        dto.setMenteeId(mentee.getMenteeId());
        dto.setFirstName(mentee.getUser().getFirstName());
        dto.setLastName(mentee.getUser().getLastName());
        dto.setEmail(mentee.getUser().getEmail());
        dto.setPhone(mentee.getUser().getPhone());
        dto.setCountry(mentee.getUser().getCountry());
        dto.setGender(mentee.getUser().getGender());
        dto.setStatus(mentee.getUser().getStatus());
        dto.setGoal(mentee.getGoal());
        dto.setCreatedAt(mentee.getUser().getCreatedAt());

        // N+1 querry, tạm chấp nhận
        long totalBookings = this.bookingRepository.countByMenteeMenteeId(mentee.getMenteeId());
        dto.setTotalBookings(totalBookings);

        return dto;
    }

    private ResAdminMentorDTO convertToMentorDTO(Mentor mentor) {
        ResAdminMentorDTO dto = new ResAdminMentorDTO();
        // private Long mentorId;
        // private String firstName;
        // private String lastName;
        // private String email;
        // private UserStatusEnum status;
        // private String headline;
        // private String bio;
        // private double rating;
        // private int totalReviews;
        // private int totalMeetings;
        // private List<String> skills;
        // private Instant createdAt;
        dto.setMentorId(mentor.getMentorId());
        dto.setBio(mentor.getBio());
        dto.setCreatedAt(mentor.getUser().getCreatedAt());
        dto.setEmail(mentor.getUser().getEmail());
        dto.setFirstName(mentor.getUser().getFirstName());
        dto.setLastName(mentor.getUser().getLastName());
        dto.setTotalMeetings(mentor.getTotalMeetings());
        dto.setTotalReviews(mentor.getTotalReviews());
        dto.setHeadline(mentor.getHeadline());
        dto.setStatus(mentor.getUser().getStatus());
        List<String> skills = new ArrayList<>();
        for (Skill skill : mentor.getSkills()) {
            skills.add(skill.getSkillName());
        }
        dto.setSkills(skills);
        double rating = 0;
        if (mentor.getTotalReviews() > 0) {
            rating = (double) mentor.getTotalStars() / mentor.getTotalReviews();
        }
        dto.setRating(rating);

        dto.setRejectionReason(mentor.getRejectionReason());
        dto.setRejectedAt(mentor.getRejectedAt());

        return dto;
    }
}
