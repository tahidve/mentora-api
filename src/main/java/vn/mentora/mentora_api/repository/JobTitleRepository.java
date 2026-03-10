package vn.mentora.mentora_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.mentora.mentora_api.domain.JobTitle;

@Repository
public interface JobTitleRepository extends JpaRepository<JobTitle, Long>, JpaSpecificationExecutor<JobTitle> {

}
