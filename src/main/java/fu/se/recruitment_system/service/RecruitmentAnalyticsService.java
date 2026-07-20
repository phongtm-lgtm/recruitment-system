package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.enums.ApplicationStatus;
import fu.se.recruitment_system.model.enums.UserRole;
import fu.se.recruitment_system.repository.ApplicationRepository;
import fu.se.recruitment_system.repository.JobPostRepository;
import fu.se.recruitment_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RecruitmentAnalyticsService {
    private final UserRepository userRepository;
    private final JobPostRepository jobPostRepository;
    private final ApplicationRepository applicationRepository;

    public RecruitmentAnalyticsService(
            UserRepository userRepository,
            JobPostRepository jobPostRepository,
            ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.jobPostRepository = jobPostRepository;
        this.applicationRepository = applicationRepository;
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalJobs() {
        return jobPostRepository.count();
    }

    public long getTotalApplications() {
        return applicationRepository.count();
    }

    public long getTotalHires() {
        return applicationRepository.countByStatus(ApplicationStatus.HIRED);
    }

    public Map<String, Object> getUserGrowth(LocalDateTime fromDate, LocalDateTime toDate) {
        return Map.of(
                "newUsers", userRepository.countByCreatedAtBetween(fromDate, toDate),
                "jobSeekers", userRepository.countByRole(UserRole.JOB_SEEKER),
                "recruiters", userRepository.countByRole(UserRole.RECRUITER));
    }

    public Map<String, Object> getRecruitmentFunnel(LocalDateTime fromDate, LocalDateTime toDate) {
        return Map.of(
                "jobs", jobPostRepository.countByCreatedAtBetween(fromDate, toDate),
                "applications", applicationRepository.countByAppliedAtBetween(fromDate, toDate),
                "hires", getTotalHires());
    }
}
