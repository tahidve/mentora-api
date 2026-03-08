package vn.mentora.mentora_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.mentora.mentora_api.domain.Skill;
import vn.mentora.mentora_api.repository.SkillRepository;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill createSkill(String name) throws IdInvalidException {
        if (this.skillRepository.existsBySkillName(name)) {
            throw new IdInvalidException("Skill " + name + " đã tồn tại!");
        }

        Skill skill = new Skill();
        skill.setSkillName(name);

        return skillRepository.save(skill);
    }

    public List<Skill> getAllSkills() {
        return this.skillRepository.findAll();
    }

}
