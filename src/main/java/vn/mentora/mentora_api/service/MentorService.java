package vn.mentora.mentora_api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import vn.mentora.mentora_api.domain.*;
import vn.mentora.mentora_api.domain.request.ReqUpdateMentorDTO;
import vn.mentora.mentora_api.domain.request.ReqWorkExperienceDTO;
import vn.mentora.mentora_api.domain.response.ResMentorCardDTO;
import vn.mentora.mentora_api.domain.response.ResMentorProfileDTO;
import vn.mentora.mentora_api.domain.response.ResWorkExperienceDTO;
import vn.mentora.mentora_api.domain.response.ResultPaginationDTO;

import java.util.stream.Collectors;

import vn.mentora.mentora_api.repository.*;
import vn.mentora.mentora_api.util.SecurityUtil;
import vn.mentora.mentora_api.util.constant.ResponseTimeEnum;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class MentorService {

    private final MentorRepository mentorRepository;
    private final CompanyRepository companyRepository;
    private final JobTitleRepository jobTitleRepository;
    private final WorkForRepository workForRepository;
    private final SkillRepository skillRepository;
    private final FileService fileService;

    public MentorService(MentorRepository mentorRepository,
            CompanyRepository companyRepository,
            JobTitleRepository jobTitleRepository,
            WorkForRepository workForRepository,
            SkillRepository skillRepository,
            FileService fileService) {
        this.mentorRepository = mentorRepository;
        this.companyRepository = companyRepository;
        this.jobTitleRepository = jobTitleRepository;
        this.workForRepository = workForRepository;
        this.skillRepository = skillRepository;
        this.fileService = fileService;
    }

    @Transactional
    public WorkFor addWorkExperience(ReqWorkExperienceDTO request) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        // Lấy Mentor hiện tại
        Mentor currentMentor = this.mentorRepository.findByUserEmail(email)
                .orElseThrow(() -> new IdInvalidException("Profile Mentor không tồn tại!"));

        Company company = null;
        Optional<Company> companyOptional = this.companyRepository.findByCompanyName(request.getCompanyName());
        if (companyOptional.isEmpty()) {
            Company newCompany = new Company();
            newCompany.setCompanyName(request.getCompanyName());
            company = this.companyRepository.save(newCompany);
        } else {
            company = companyOptional.get();
        }

        JobTitle jobTitle = null;
        Optional<JobTitle> jobTitleOptional = this.jobTitleRepository.findByJobTitleName(request.getJobTitleName());
        if (jobTitleOptional.isEmpty()) {
            JobTitle newJobTitle = new JobTitle();
            newJobTitle.setJobTitleName(request.getJobTitleName());
            jobTitle = this.jobTitleRepository.save(newJobTitle);
        } else {
            jobTitle = jobTitleOptional.get();
        }

        WorkFor workFor = new WorkFor();
        WorkForId workForId = new WorkForId(currentMentor.getMentorId(), company.getCompanyId());
        workFor.setId(workForId);
        workFor.setMentor(currentMentor);
        workFor.setCompany(company);
        workFor.setJobTitle(jobTitle);
        workFor.setStartDate(request.getStartDate());
        workFor.setEndDate(request.getEndDate());
        workFor.setCurrent(request.isCurrent());
        return this.workForRepository.save(workFor);
    }

    public List<ResWorkExperienceDTO> getWorkExperiences(Long mentorId) {
        List<WorkFor> workForList = this.workForRepository.findByMentorMentorId(mentorId);
        return workForList.stream()
                .map(workFor -> new ResWorkExperienceDTO(workFor))
                .collect(Collectors.toList());
    }

    @Transactional
    public ResMentorProfileDTO updateProfile(String email, ReqUpdateMentorDTO request) throws IdInvalidException {
        Optional<Mentor> mentorOptional = this.mentorRepository.findByUserEmail(email);
        if (mentorOptional.isEmpty()) {
            throw new IdInvalidException("Mentor không tồn tại!");
        }

        Mentor mentor = mentorOptional.get();
        mentor.setBio(request.getBio());
        mentor.setHeadline(request.getHeadline());
        mentor.getUser().setCountry(request.getCountry());
        mentor.getUser().setTimezone(request.getTimezone());

        if (request.getLanguages() != null) {
            mentor.setLanguages(request.getLanguages());
        }

        if (request.getSkillIds() != null) {
            List<Skill> skills = this.skillRepository.findBySkillIdIn(request.getSkillIds());
            mentor.setSkills(skills);
        }

        mentorRepository.save(mentor);
        return this.convertToProfileDTO(mentor);
    }

    public ResMentorProfileDTO getPublicProfile(Long mentorId) throws IdInvalidException {
        Mentor mentor = this.mentorRepository.findById(mentorId)
                .orElseThrow(() -> new IdInvalidException("Mentor không tồn tại!"));
        return this.convertToProfileDTO(mentor);
    }

    @Transactional
    public String uploadAvatar(String email, MultipartFile file) throws IdInvalidException, IOException {
        Mentor mentor = this.getMentorByEmail(email);

        // Gọi FileService để lưu file, lấy về uniqueFilename
        String filename = this.fileService.store(file);

        // Lưu URL vào Mentor (chỉ tên file, FE ghép với base URL)
        mentor.setAvatarUrl(filename);
        mentorRepository.save(mentor);

        return filename;
    }

    private ResMentorProfileDTO convertToProfileDTO(Mentor mentor) {
        ResMentorProfileDTO dto = new ResMentorProfileDTO();
        dto.setId(mentor.getMentorId());
        dto.setName(mentor.getUser().getFirstName() + " " + mentor.getUser().getLastName());
        dto.setAvatarUrl(mentor.getAvatarUrl());
        dto.setBio(mentor.getBio());
        dto.setHeadline(mentor.getHeadline());
        dto.setResponseTime(mentor.getResponseTime());
        dto.setTotalMentee(mentor.getTotalMentee());
        dto.setTotalStars(mentor.getTotalStars());
        dto.setTotalReviews(mentor.getTotalReviews());
        dto.setLanguages(mentor.getLanguages());

        List<String> skillNames = new ArrayList<>();
        for (Skill skill : mentor.getSkills()) {
            skillNames.add(skill.getSkillName());
        }
        dto.setSkills(skillNames);

        List<ResWorkExperienceDTO> workExperiences = new ArrayList<>();
        for (WorkFor workFor : mentor.getWorkExperiences()) {
            workExperiences.add(new ResWorkExperienceDTO(workFor));
        }
        dto.setWorkExperiences(workExperiences);

        // Badge logic
        dto.setBadges(this.computeBadges(mentor));
        return dto;
    }

    private List<String> computeBadges(Mentor mentor) {
        List<String> badges = new ArrayList<>();

        double rating = mentor.getTotalReviews() > 0
                ? (double) mentor.getTotalStars() / mentor.getTotalReviews()
                : 0;
        if (rating >= 4.8 && mentor.getTotalReviews() >= 10) {
            badges.add("Top Mentor");
        }

        if (mentor.getResponseTime() != null) {
            ResponseTimeEnum rt = mentor.getResponseTime();
            if (rt == ResponseTimeEnum.WITHIN_1_HOUR || rt == ResponseTimeEnum.WITHIN_2_HOURS) {
                badges.add("Quick Responder");
            }
        }

        return badges;
    }

    public Mentor getMentorByEmail(String email) throws IdInvalidException {
        Optional<Mentor> mentorOptional = this.mentorRepository.findByUserEmail(email);
        if (mentorOptional.isEmpty()) {
            throw new IdInvalidException("Không tồn tại mentor có email" + email);
        }
        return mentorOptional.get();
    }

    @Transactional
    public ResMentorProfileDTO updateSkills(String email, List<Long> skillIds) throws IdInvalidException {
        Mentor mentor = this.getMentorByEmail(email);
        List<Skill> skills = this.skillRepository.findBySkillIdIn(skillIds);
        mentor.setSkills(skills);
        this.mentorRepository.save(mentor);
        return this.convertToProfileDTO(mentor);
    }

    @Transactional
    public ResMentorProfileDTO updateLanguages(String email, List<String> languages) throws IdInvalidException {
        Mentor mentor = this.getMentorByEmail(email);
        mentor.setLanguages(languages);
        this.mentorRepository.save(mentor);
        return this.convertToProfileDTO(mentor);
    }

    public ResultPaginationDTO fetchMentorsPaginated(Specification<Mentor> spec, Pageable pageable) {
        Page<Mentor> page = this.mentorRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber());
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);

        List<ResMentorCardDTO> cards = new ArrayList<>();
        for (Mentor mentor : page.getContent()) {
            ResMentorCardDTO card = this.convertToCardDTO(mentor);
            cards.add(card);
        }

        result.setResult(cards);
        return result;
    }

    public ResMentorCardDTO convertToCardDTO(Mentor mentor) {
        ResMentorCardDTO mentorCard = new ResMentorCardDTO();
        mentorCard.setId(mentor.getMentorId());
        mentorCard.setAvatarUrl(mentor.getAvatarUrl());
        mentorCard.setHeadline(mentor.getHeadline());
        mentorCard.setName(mentor.getUser().getFirstName() + " " + mentor.getUser().getLastName());
        mentorCard.setBadges(this.computeBadges(mentor));
        mentorCard.setSkills(mentor.getSkills().stream()
                .limit(3)
                .map(skill -> skill.getSkillName())
                .collect(Collectors.toList()));
        mentorCard.setTotalReviews(mentor.getTotalReviews());
        mentorCard.setTotalStars(mentor.getTotalStars());

        return mentorCard;
    }

}
