package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.OrderResponse;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.ServiceOrder;
import fu.se.recruitment_system.model.ServicePackage;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.ServiceOrderStatus;
import fu.se.recruitment_system.repository.ServiceOrderRepository;
import fu.se.recruitment_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderService {
    private final ServiceOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ServicePackageService packageService;
    private final JobPostService jobPostService;

    public OrderService(
            ServiceOrderRepository orderRepository,
            UserRepository userRepository,
            ServicePackageService packageService,
            JobPostService jobPostService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.packageService = packageService;
        this.jobPostService = jobPostService;
    }

    @Transactional
    public OrderResponse createPackageOrder(Long recruiterId, Long packageId) {
        return toResponse(saveOrder(recruiterId, packageId, null));
    }

    @Transactional
    public OrderResponse createFeaturedOrder(Long recruiterId, Long packageId, Long jobPostId) {
        JobPost jobPost = jobPostService.getEligibleJob(recruiterId, jobPostId);
        return toResponse(saveOrder(recruiterId, packageId, jobPost));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getPurchaseHistory(Long recruiterId) {
        return orderRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceOrder getOwnedOrder(Long recruiterId, Long orderId) {
        return orderRepository.findByIdAndRecruiterId(orderId, recruiterId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Service order not found"));
    }

    private ServiceOrder saveOrder(Long recruiterId, Long packageId, JobPost jobPost) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Recruiter not found"));
        ServicePackage servicePackage = packageService.getActivePackage(packageId);

        ServiceOrder order = new ServiceOrder();
        order.setRecruiter(recruiter);
        order.setServicePackage(servicePackage);
        order.setJobPost(jobPost);
        order.setAmount(servicePackage.getPrice());
        order.setStatus(ServiceOrderStatus.PENDING);
        return orderRepository.save(order);
    }

    OrderResponse toResponse(ServiceOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getServicePackage().getPackageName(),
                order.getJobPost() == null ? null : order.getJobPost().getId(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt());
    }
}
