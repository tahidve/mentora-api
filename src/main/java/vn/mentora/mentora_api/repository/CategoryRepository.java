package vn.mentora.mentora_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.mentora.mentora_api.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    List<Category> findByParentIsNull();

    boolean existsByCategoryName(String name);

}
