package vn.mentora.mentora_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;

public interface MentorRepository extends JpaRepository<Mentor, Long>, JpaSpecificationExecutor<Mentor> {
    List<Mentor> findByUserStatus(UserStatusEnum status);

    Optional<Mentor> findByUserEmail(String email);

    @Query("""
            SELECT m FROM Mentor m
            WHERE LOWER(m.user.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(m.user.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(m.headline) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(m.bio) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Mentor> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT l FROM Mentor m JOIN m.languages l ORDER BY l")
    List<String> findDistinctLanguages();
}
