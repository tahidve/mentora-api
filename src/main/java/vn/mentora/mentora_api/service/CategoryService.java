package vn.mentora.mentora_api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.mentora.mentora_api.domain.Category;
import vn.mentora.mentora_api.domain.response.ResCategoryDTO;
import vn.mentora.mentora_api.repository.CategoryRepository;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(String name, Long parentId) throws IdInvalidException {
        if (categoryRepository.existsByCategoryName(name)) {
            throw new IdInvalidException("Category " + name + " đã tồn tại!");
        }

        Category category = new Category();
        category.setCategoryName(name);

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new IdInvalidException("Parent Category ID " + parentId + " không tồn tại!"));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<ResCategoryDTO> getAllRootCategories() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();

        List<ResCategoryDTO> resCategoryDTOs = new ArrayList<>();
        for (Category c : rootCategories) {
            resCategoryDTOs.add(new ResCategoryDTO(c));
        }

        return resCategoryDTOs;
    }

}
