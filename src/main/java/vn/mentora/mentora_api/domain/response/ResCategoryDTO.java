package vn.mentora.mentora_api.domain.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.mentora.mentora_api.domain.Category;

@Getter
@Setter
public class ResCategoryDTO {

    private long categoryId;

    private String categoryName;

    List<ResCategoryDTO> subCategories;

    public ResCategoryDTO(Category category) {
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            this.subCategories = new ArrayList<>();
            for (Category c : category.getChildren()) {
                this.subCategories.add(new ResCategoryDTO(c));
            }
        }
    }

}
