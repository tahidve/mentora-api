package vn.mentora.mentora_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.mentora.mentora_api.domain.Mentor;
import vn.mentora.mentora_api.util.constant.UserStatusEnum;

public interface MentorRepository extends JpaRepository<Mentor, Long>, JpaSpecificationExecutor<Mentor> {
    List<Mentor> findByUserStatus(UserStatusEnum status);
}
