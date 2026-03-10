package vn.mentora.mentora_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.mentora.mentora_api.domain.Company;
import vn.mentora.mentora_api.domain.JobTitle;
import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.domain.WorkFor;
import vn.mentora.mentora_api.domain.WorkForId;
import vn.mentora.mentora_api.domain.request.ReqWorkExperienceDTO;
import vn.mentora.mentora_api.domain.response.ResWorkExperienceDTO;
import java.util.stream.Collectors;
import vn.mentora.mentora_api.repository.CompanyRepository;
import vn.mentora.mentora_api.repository.JobTitleRepository;
import vn.mentora.mentora_api.repository.MentorRepository;
import vn.mentora.mentora_api.repository.WorkForRepository;
import vn.mentora.mentora_api.util.SecurityUtil;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class MentorService {

    private final MentorRepository mentorRepository;
    private final CompanyRepository companyRepository;
    private final JobTitleRepository jobTitleRepository;
    private final WorkForRepository workForRepository;

    public MentorService(MentorRepository mentorRepository, CompanyRepository companyRepository,
            JobTitleRepository jobTitleRepository, WorkForRepository workForRepository) {
        this.mentorRepository = mentorRepository;
        this.companyRepository = companyRepository;
        this.jobTitleRepository = jobTitleRepository;
        this.workForRepository = workForRepository;
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

}
