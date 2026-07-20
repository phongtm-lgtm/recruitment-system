package fu.se.recruitment_system.model;

import fu.se.recruitment_system.model.enums.JobPostStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_posts")
@Check(name = "chk_job_post_rejection_reason", constraints = "status <> 'REJECTED' OR rejection_reason IS NOT NULL")
@Check(name = "chk_feature_expiration", constraints = "is_featured = false OR feature_expire_at IS NOT NULL")
@Getter
@Setter
@NoArgsConstructor
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String industry;

    @Column(length = 255)
    private String location;

    @Column(name = "experience_level", length = 100)
    private String experienceLevel;

    @Column(name = "work_type", length = 100)
    private String workType;

    @Column(name = "job_description", nullable = false, columnDefinition = "TEXT")
    private String jobDescription;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Column(name = "salary_range", length = 100)
    private String salaryRange;

    @Column(name = "application_deadline")
    private LocalDateTime applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20,
            columnDefinition = "ENUM('DRAFT','PENDING','ACTIVE','REJECTED','ESCALATED','CLOSED')")
    @ColumnDefault("'DRAFT'")
    private JobPostStatus status = JobPostStatus.DRAFT;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "is_featured", nullable = false)
    @ColumnDefault("false")
    private boolean featured;

    @Column(name = "feature_expire_at")
    private LocalDateTime featureExpireAt;

    @Column(name = "view_count", nullable = false)
    @ColumnDefault("0")
    private long viewCount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
