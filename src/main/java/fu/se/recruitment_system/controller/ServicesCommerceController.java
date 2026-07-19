package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.dto.CreateFeaturedOrderRequest;
import fu.se.recruitment_system.dto.CreatePackageOrderRequest;
import fu.se.recruitment_system.dto.JobPostResponse;
import fu.se.recruitment_system.dto.OrderResponse;
import fu.se.recruitment_system.dto.ServicePackageResponse;
import fu.se.recruitment_system.dto.SubscriptionResponse;
import fu.se.recruitment_system.service.JobPostService;
import fu.se.recruitment_system.service.OrderService;
import fu.se.recruitment_system.service.ServicePackageService;
import fu.se.recruitment_system.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/services")
public class ServicesCommerceController {
    private final ServicePackageService packageService;
    private final OrderService orderService;
    private final JobPostService jobPostService;
    private final SubscriptionService subscriptionService;

    public ServicesCommerceController(
            ServicePackageService packageService,
            OrderService orderService,
            JobPostService jobPostService,
            SubscriptionService subscriptionService) {
        this.packageService = packageService;
        this.orderService = orderService;
        this.jobPostService = jobPostService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/packages")
    public List<ServicePackageResponse> getPackages() {
        return packageService.getActivePackages();
    }

    @PostMapping("/orders/package")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createPackageOrder(
            @RequestHeader("X-Recruiter-Id") Long recruiterId,
            @RequestBody CreatePackageOrderRequest request) {
        if (request.packageId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "packageId is required");
        }
        return orderService.createPackageOrder(recruiterId, request.packageId());
    }

    @GetMapping("/featured-jobs/eligible")
    public List<JobPostResponse> getEligibleJobs(
            @RequestHeader("X-Recruiter-Id") Long recruiterId) {
        return jobPostService.getEligibleJobs(recruiterId);
    }

    @PostMapping("/orders/featured-job")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createFeaturedOrder(
            @RequestHeader("X-Recruiter-Id") Long recruiterId,
            @RequestBody CreateFeaturedOrderRequest request) {
        if (request.packageId() == null || request.jobPostId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "packageId and jobPostId are required");
        }
        return orderService.createFeaturedOrder(recruiterId, request.packageId(), request.jobPostId());
    }

    @GetMapping("/subscriptions")
    public List<SubscriptionResponse> getSubscriptions(
            @RequestHeader("X-Recruiter-Id") Long recruiterId) {
        return subscriptionService.getActiveSubscriptions(recruiterId);
    }

    @GetMapping("/orders")
    public List<OrderResponse> getPurchaseHistory(
            @RequestHeader("X-Recruiter-Id") Long recruiterId) {
        return orderService.getPurchaseHistory(recruiterId);
    }
}
