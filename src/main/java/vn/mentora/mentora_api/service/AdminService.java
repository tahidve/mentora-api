package vn.mentora.mentora_api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.mentora.mentora_api.domain.Mentee;
import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.Skill;
import vn.mentora.mentora_api.domain.User;
import vn.mentora.mentora_api.domain.response.ResAdminMenteeDTO;
import vn.mentora.mentora_api.domain.response.ResAdminMentorDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;
import vn.mentora.mentora_api.repository.BookingRepository;
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

    public AdminService(
            MenteeRepository menteeRepository,
            BookingRepository bookingRepository,
            UserRepository userRepository,
            MentorRepository mentorRepository) {
        this.menteeRepository = menteeRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.mentorRepository = mentorRepository;
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
        return dto;
    }
}
