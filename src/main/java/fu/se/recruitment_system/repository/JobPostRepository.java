package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.enums.JobPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost> {
    List<JobPost> findByRecruiterIdAndStatusOrderByCreatedAtDesc(Long recruiterId, JobPostStatus status);

    List<JobPost> findTop20ByRecruiterIdOrderByCreatedAtDesc(Long recruiterId);

    List<JobPost> findTop20ByRecruiterIdAndStatusOrderByCreatedAtDesc(Long recruiterId, JobPostStatus status);

    List<JobPost> findTop20ByStatusOrderByCreatedAtDesc(JobPostStatus status);

    Optional<JobPost> findByIdAndRecruiterIdAndStatus(Long id, Long recruiterId, JobPostStatus status);

    Optional<JobPost> findByIdAndRecruiterId(Long id, Long recruiterId);

    Optional<JobPost> findByIdAndStatus(Long id, JobPostStatus status);

    boolean existsByIdAndRecruiterId(Long id, Long recruiterId);

    long countByCreatedAtBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
