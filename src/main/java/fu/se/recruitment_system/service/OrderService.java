package fu.se.recruitment_system.service;

import fu.se.recruitment_system.dto.OrderResponse;
import fu.se.recruitment_system.model.JobPost;
import fu.se.recruitment_system.model.Order;
import fu.se.recruitment_system.model.ServicePackage;
import fu.se.recruitment_system.model.User;
import fu.se.recruitment_system.model.enums.BenefitType;
import fu.se.recruitment_system.model.enums.OrderStatus;
import fu.se.recruitment_system.repository.OrderRepository;
import fu.se.recruitment_system.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ServicePackageService packageService;
    private final JobPostService jobPostService;

    public OrderService(
            OrderRepository orderRepository,
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
        return toResponse(saveOrder(recruiterId, packageId, null, BenefitType.POSTING_QUOTA));
    }

    @Transactional
    public OrderResponse createFeaturedOrder(Long recruiterId, Long packageId, Long jobPostId) {
        JobPost jobPost = jobPostService.getEligibleJob(recruiterId, jobPostId);
        return toResponse(saveOrder(recruiterId, packageId, jobPost, BenefitType.FEATURED_JOB));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getPurchaseHistory(Long recruiterId) {
        return orderRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Order getOwnedOrder(Long recruiterId, Long orderId) {
        return orderRepository.findByIdAndRecruiterId(orderId, recruiterId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Service order not found"));
    }

    private Order saveOrder(
            Long recruiterId,
            Long packageId,
            JobPost jobPost,
            BenefitType benefitType) {
        User recruiter = userRepository.findById(recruiterId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Recruiter not found"));
        ServicePackage servicePackage = packageService.getActivePackage(packageId);

        Order order = new Order();
        order.setRecruiter(recruiter);
        order.setServicePackage(servicePackage);
        order.setJobPost(jobPost);
        order.setBenefitType(benefitType);
        order.setAmount(servicePackage.getPrice());
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getServicePackage().getPackageName(),
                order.getJobPost() == null ? null : order.getJobPost().getId(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt());
    }
}
