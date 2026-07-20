package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.CompanyProfile;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.enums.JobPostStatus;
import fu.se.recruitment_system.repository.JobPostRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class JobPostingQueryService {
    private static final int MAX_PAGE_SIZE = 20;

    private final JobPostRepository jobPostRepository;

    public JobPostingQueryService(JobPostRepository jobPostRepository) {
        this.jobPostRepository = jobPostRepository;
    }

    public List<JobPost> searchActiveJobs(String keyword, Long categoryId, String industry, String location) {
        validateSearchCriteria(keyword);
        return jobPostRepository.findAll(
                activeJobSpecification(keyword, categoryId, industry, location),
                PageRequest.of(0, MAX_PAGE_SIZE, Sort.by(
                        Sort.Order.desc("featured"),
                        Sort.Order.desc("createdAt")))).getContent();
    }

    public JobPost getActiveJobDetail(Long jobPostId) {
        return jobPostRepository.findByIdAndStatus(jobPostId, JobPostStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Active job post not found"));
    }

    public List<JobPost> getActiveJobsByCompany(CompanyProfile companyProfile) {
        return jobPostRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.and(
                        criteriaBuilder.equal(root.get("status"), JobPostStatus.ACTIVE),
                        criteriaBuilder.equal(root.get("recruiter").get("id"), companyProfile.getUser().getId())),
                PageRequest.of(0, MAX_PAGE_SIZE, Sort.by(Sort.Order.desc("createdAt")))).getContent();
    }

    private Specification<JobPost> activeJobSpecification(
            String keyword,
            Long categoryId,
            String industry,
            String location) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), JobPostStatus.ACTIVE));

            String normalizedKeyword = normalize(keyword);
            if (normalizedKeyword != null) {
                String like = "%" + normalizedKeyword.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), like),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("industry")), like),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("jobDescription")), like),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("requirements")), like)));
            }
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            String normalizedIndustry = normalize(industry);
            if (normalizedIndustry != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("industry")),
                        "%" + normalizedIndustry.toLowerCase() + "%"));
            }
            String normalizedLocation = normalize(location);
            if (normalizedLocation != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")),
                        "%" + normalizedLocation.toLowerCase() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void validateSearchCriteria(String keyword) {
        if (keyword != null && keyword.length() > 256) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Search keyword must not exceed 256 characters");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
