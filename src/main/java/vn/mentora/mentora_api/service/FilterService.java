package vn.mentora.mentora_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.Skill;
import vn.mentora.mentora_api.domain.response.ResAvailableFilterDTO;
import vn.mentora.mentora_api.repository.SkillRepository;
import vn.mentora.mentora_api.repository.UserRepository;

@Service
public class FilterService {
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public FilterService(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    public ResAvailableFilterDTO getAvailableFilters() {
        ResAvailableFilterDTO dto = new ResAvailableFilterDTO();

        // 1. Countries
        dto.setCountries(userRepository.findDistinctCountries());

        // 2. Skills + mentorCount
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

        return dto;
    }
}
