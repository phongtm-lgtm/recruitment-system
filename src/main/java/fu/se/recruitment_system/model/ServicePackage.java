package fu.se.recruitment_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_packages", uniqueConstraints =
        @UniqueConstraint(name = "uq_service_packages_name", columnNames = "package_name"))
@Check(name = "chk_service_packages_price", constraints = "price >= 0")
@Check(name = "chk_service_packages_duration", constraints = "duration_days > 0")
@Check(name = "chk_service_packages_quota", constraints = "quota >= 0")
@Getter
@Setter
@NoArgsConstructor
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_name", nullable = false, length = 100)
    private String packageName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(nullable = false)
    private int quota;

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("true")
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
