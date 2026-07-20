package fu.se.recruitment_system.service;

import fu.se.recruitment_system.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class ApplicationHistoryService {
    private final ApplicationRepository applicationRepository;

    public ApplicationHistoryService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public long countApplications(Long jobPostId) {
        return applicationRepository.countByJobPostId(jobPostId);
    }

    public boolean hasAppliedInLast30Days(Long jobPostId, Long jobSeekerId) {
        if (jobSeekerId == null) {
            return false;
        }
        return applicationRepository.findTopByJobPostIdAndJobSeekerIdOrderByAppliedAtDesc(jobPostId, jobSeekerId)
                .map(application -> application.getAppliedAt().isAfter(LocalDateTime.now().minusDays(30)))
                .orElse(false);
    }
}
