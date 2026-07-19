package fu.se.recruitment_system.service;

import fu.se.recruitment_system.model.ServicePackage;
import fu.se.recruitment_system.repository.OrderRepository;
import fu.se.recruitment_system.repository.ServicePackageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServicePackageManagementService {
    private final ServicePackageRepository servicePackageRepository;
    private final OrderRepository orderRepository;

    public ServicePackageManagementService(
            ServicePackageRepository servicePackageRepository,
            OrderRepository orderRepository) {
        this.servicePackageRepository = servicePackageRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<ServicePackage> getServicePackages() {
        return servicePackageRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ServicePackage getServicePackageDetail(Long packageId) {
        return findPackage(packageId);
    }

    @Transactional
    public ServicePackage createServicePackage(ServicePackage command) {
        validatePackageData(command);
        if (servicePackageRepository.existsByPackageName(command.getPackageName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Service package name already exists");
        }
        command.setActive(true);
        return servicePackageRepository.save(command);
    }

    @Transactional
    public ServicePackage updateServicePackage(Long packageId, ServicePackage command) {
        validatePackageData(command);
        if (servicePackageRepository.existsByPackageNameAndIdNot(command.getPackageName(), packageId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Service package name already exists");
        }

        ServicePackage servicePackage = findPackage(packageId);
        servicePackage.setPackageName(command.getPackageName());
        servicePackage.setPrice(command.getPrice());
        servicePackage.setDurationDays(command.getDurationDays());
        servicePackage.setQuota(command.getQuota());
        servicePackage.setActive(command.isActive());
        return servicePackageRepository.save(servicePackage);
    }

    @Transactional
    public void disableServicePackage(Long packageId) {
        checkPackageCanBeDisabled(packageId);
        ServicePackage servicePackage = findPackage(packageId);
        servicePackage.setActive(false);
        servicePackageRepository.save(servicePackage);
    }

    public void validatePackageData(ServicePackage command) {
        if (command.getPackageName() == null || command.getPackageName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "packageName is required");
        }
        if (command.getPrice() == null || command.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price must be greater than or equal to 0");
        }
        if (command.getDurationDays() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "durationDays must be greater than 0");
        }
        if (command.getQuota() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quota must be greater than or equal to 0");
        }
    }

    public void checkPackageCanBeDisabled(Long packageId) {
        findPackage(packageId);
        orderRepository.countByServicePackageId(packageId);
    }

    private ServicePackage findPackage(Long packageId) {
        return servicePackageRepository.findById(packageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service package not found"));
    }
}
