package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.model.ServicePackage;
import fu.se.recruitment_system.service.AdminAuthorizationService;
import fu.se.recruitment_system.service.ServicePackageManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/service-packages")
public class ServicePackageAdminController {
    private final ServicePackageManagementService servicePackageManagementService;
    private final AdminAuthorizationService adminAuthorizationService;

    public ServicePackageAdminController(
            ServicePackageManagementService servicePackageManagementService,
            AdminAuthorizationService adminAuthorizationService) {
        this.servicePackageManagementService = servicePackageManagementService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    @GetMapping
    public List<ServicePackage> getServicePackages(@RequestHeader("X-Admin-Id") Long adminId) {
        adminAuthorizationService.verifyPackageManagementPermission(adminId);
        return servicePackageManagementService.getServicePackages();
    }

    @GetMapping("/{packageId}")
    public ServicePackage getServicePackageDetail(
            @RequestHeader("X-Admin-Id") Long adminId,
            @PathVariable Long packageId) {
        adminAuthorizationService.verifyPackageManagementPermission(adminId);
        return servicePackageManagementService.getServicePackageDetail(packageId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicePackage createServicePackage(
            @RequestHeader("X-Admin-Id") Long adminId,
            @RequestBody ServicePackage request) {
        adminAuthorizationService.verifyPackageManagementPermission(adminId);
        return servicePackageManagementService.createServicePackage(request);
    }

    @PutMapping("/{packageId}")
    public ServicePackage updateServicePackage(
            @RequestHeader("X-Admin-Id") Long adminId,
            @PathVariable Long packageId,
            @RequestBody ServicePackage request) {
        adminAuthorizationService.verifyPackageManagementPermission(adminId);
        return servicePackageManagementService.updateServicePackage(packageId, request);
    }

    @DeleteMapping("/{packageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableServicePackage(
            @RequestHeader("X-Admin-Id") Long adminId,
            @PathVariable Long packageId) {
        adminAuthorizationService.verifyPackageManagementPermission(adminId);
        servicePackageManagementService.disableServicePackage(packageId);
    }
}
