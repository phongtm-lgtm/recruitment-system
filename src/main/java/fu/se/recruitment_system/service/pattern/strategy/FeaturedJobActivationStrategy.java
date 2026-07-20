package fu.se.recruitment_system.service.pattern.strategy;

import fu.se.recruitment_system.model.Order;
import fu.se.recruitment_system.enums.BenefitType;
import fu.se.recruitment_system.service.JobPostService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FeaturedJobActivationStrategy implements BenefitActivationStrategy {
    private final JobPostService jobPostService;

    public FeaturedJobActivationStrategy(JobPostService jobPostService) {
        this.jobPostService = jobPostService;
    }

    @Override
    public BenefitType supportedType() {
        return BenefitType.FEATURED_JOB;
    }

    @Override
    public void activate(Order order, LocalDateTime expiredAt) {
        if (order.getJobPost() == null) {
            throw new IllegalArgumentException("Featured job order must target a job post");
        }
        jobPostService.activateFeaturedJob(order.getJobPost(), expiredAt);
    }
}
