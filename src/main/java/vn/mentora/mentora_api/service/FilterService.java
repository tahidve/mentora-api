package vn.mentora.mentora_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.Skill;
import vn.mentora.mentora_api.domain.response.ResAvailableFilterDTO;
import vn.mentora.mentora_api.repository.MentorRepository;
import vn.mentora.mentora_api.repository.SkillRepository;
import vn.mentora.mentora_api.repository.UserRepository;

@Service
public class FilterService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;

    public FilterService(SkillRepository skillRepository, UserRepository userRepository,
            MentorRepository mentorRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.mentorRepository = mentorRepository;
    }

    public ResAvailableFilterDTO getAvailableFilters() {
        ResAvailableFilterDTO dto = new ResAvailableFilterDTO();

        // Countries
        dto.setCountries(userRepository.findDistinctCountries());

        // Skills + mentorCount
        List<Object[]> rawSkills = skillRepository.findAllWithMentorCount();
        List<ResAvailableFilterDTO.SkillOption> skills = new ArrayList<>();
        for (Object[] row : rawSkills) {
            Skill skill = (Skill) row[0]; // Ép kiểu cột 0 về Skill
            Long count = (Long) row[1]; // Ép kiểu cột 1 về Long
            ResAvailableFilterDTO.SkillOption option = new ResAvailableFilterDTO.SkillOption();
            option.setSkillId(skill.getSkillId());
            option.setSkillName(skill.getSkillName());
            option.setMentorCount(count);
            skills.add(option);
        }
        dto.setSkills(skills);

        // Languages, distinct từ tất cả mentor
        dto.setLanguages(mentorRepository.findDistinctLanguages());

        return dto;
    }
}
