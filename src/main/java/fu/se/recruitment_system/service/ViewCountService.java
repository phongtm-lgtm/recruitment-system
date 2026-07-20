package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.repository.JobPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViewCountService {
    private final JobPostRepository jobPostRepository;

    public ViewCountService(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
    }

    @Transactional
    public JobPost incrementViewCount(JobPost jobPost) {
        jobPost.setViewCount(jobPost.getViewCount() + 1);
        return jobPostRepository.save(jobPost);
    }
}
