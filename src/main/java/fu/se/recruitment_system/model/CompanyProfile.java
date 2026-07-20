package fu.se.recruitment_system.model;

import fu.se.recruitment_system.model.enums.VerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_profiles", uniqueConstraints = {
        @UniqueConstraint(name = "uq_company_profiles_user", columnNames = "user_id"),
        @UniqueConstraint(name = "uq_company_profiles_tin", columnNames = "tin")
})
@Check(name = "chk_company_rejection_reason", constraints = "verification_status <> 'REJECTED' OR rejection_reason IS NOT NULL")
@Getter
@Setter
@NoArgsConstructor
public class CompanyProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(length = 50)
    private String tin;

    @Column(name = "business_field", length = 255)
    private String businessField;

    @Column(length = 255)
    private String website;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String images;

    @Column(name = "license_url", length = 500)
    private String licenseUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    @ColumnDefault("'PENDING'")
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
