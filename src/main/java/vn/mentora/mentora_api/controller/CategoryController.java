package vn.mentora.mentora_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.mentora.mentora_api.domain.Category;
import vn.mentora.mentora_api.domain.response.ResCategoryDTO;
import vn.mentora.mentora_api.service.CategoryService;
import vn.mentora.mentora_api.util.annotation.ApiMessage;
import vn.mentora.mentora_api.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    @ApiMessage("Get all categories success")
    public ResponseEntity<List<ResCategoryDTO>> getAllRootCategories() {
        return ResponseEntity.ok().body(this.categoryService.getAllRootCategories());
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiMessage("Create category success")
    public ResponseEntity<Category> create(@Valid @RequestBody Category category) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.categoryService.createCategory(category.getCategoryName(), category.getCategoryId()));
    }

}
