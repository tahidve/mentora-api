package vn.mentora.mentora_api.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotBlank(message = "Category name is required!")
    private String categoryName;

    // N category con thuộc 1 category cha
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "super_category_id", nullable = true)
    @JsonIgnore
    private Category parent;

    // List các con
    @OneToMany(mappedBy = "parent")
    private List<Category> children;

    @ManyToMany
    @JoinTable(name = "own_skill", joinColumns = @JoinColumn(name = "category_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

}
