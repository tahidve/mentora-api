package vn.mentora.mentora_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.mentora.mentora_api.domain.WorkFor;
import vn.mentora.mentora_api.domain.WorkForId;

public interface WorkForRepository extends JpaRepository<WorkFor, WorkForId>, JpaSpecificationExecutor<WorkFor> {
    List<WorkFor> findByMentorMentorId(Long mentorId);
}
