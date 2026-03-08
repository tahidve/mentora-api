package vn.mentora.mentora_api.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "skills")
@Getter
@Setter
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skillId;

    @NotBlank(message = "Skill name is requied!")
    private String skillName;

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private List<Category> categories;

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private List<Mentor> mentors;

}
