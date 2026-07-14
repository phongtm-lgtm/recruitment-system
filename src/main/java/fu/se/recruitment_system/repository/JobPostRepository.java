package fu.se.recruitment_system.repository;

import fu.se.recruitment_system.model.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
}
