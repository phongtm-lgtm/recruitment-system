package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.ServicePackageResponse;
import fu.se.recruitment_system.model.ServicePackage;
import fu.se.recruitment_system.repository.ServicePackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class ServicePackageService {
    private final ServicePackageRepository packageRepository;

    public ServicePackageService(ServicePackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    public List<ServicePackageResponse> getActivePackages() {
        return packageRepository.findByActiveTrueOrderByPriceAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public ServicePackage getActivePackage(Long packageId) {
        return packageRepository.findByIdAndActiveTrue(packageId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Active service package not found"));
    }

    private ServicePackageResponse toResponse(ServicePackage servicePackage) {
        return new ServicePackageResponse(
                servicePackage.getId(),
                servicePackage.getPackageName(),
                servicePackage.getPrice(),
                servicePackage.getDurationDays(),
                servicePackage.getQuota());
    }
}
