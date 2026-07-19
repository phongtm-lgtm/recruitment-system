package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.enums.JobPostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByRecruiterIdAndStatusOrderByCreatedAtDesc(Long recruiterId, JobPostStatus status);

    Optional<JobPost> findByIdAndRecruiterIdAndStatus(Long id, Long recruiterId, JobPostStatus status);
}
