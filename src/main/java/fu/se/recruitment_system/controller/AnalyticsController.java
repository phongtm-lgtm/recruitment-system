package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.service.AdminAuthorizationService;
import fu.se.recruitment_system.service.RecruitmentAnalyticsService;
import fu.se.recruitment_system.service.RevenueAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
public class AnalyticsController {
    private final AdminAuthorizationService adminAuthorizationService;
    private final RevenueAnalyticsService revenueAnalyticsService;
    private final RecruitmentAnalyticsService recruitmentAnalyticsService;

    public AnalyticsController(
            AdminAuthorizationService adminAuthorizationService,
            RevenueAnalyticsService revenueAnalyticsService,
            RecruitmentAnalyticsService recruitmentAnalyticsService) {
        this.adminAuthorizationService = adminAuthorizationService;
        this.revenueAnalyticsService = revenueAnalyticsService;
        this.recruitmentAnalyticsService = recruitmentAnalyticsService;
    }

    @GetMapping("/revenue")
    public Map<String, Object> viewRevenueAnalytics(
            @RequestHeader("X-Admin-Id") Long adminId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        adminAuthorizationService.verifyAnalyticsPermission(adminId);
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        return Map.of(
                "statistics", revenueAnalyticsService.getRevenueStatistics(from, to),
                "trend", revenueAnalyticsService.getRevenueTrend(from, to),
                "packageRevenueBreakdown", revenueAnalyticsService.getPackageRevenueBreakdown(from, to),
                "recentSuccessfulTransactions", revenueAnalyticsService.getRecentSuccessfulTransactions());
    }

    @GetMapping("/recruitment")
    public Map<String, Object> viewRecruitmentAnalytics(
            @RequestHeader("X-Admin-Id") Long adminId,
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        adminAuthorizationService.verifyAnalyticsPermission(adminId);
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);
        return Map.of(
                "totalUsers", recruitmentAnalyticsService.getTotalUsers(),
                "totalJobs", recruitmentAnalyticsService.getTotalJobs(),
                "totalApplications", recruitmentAnalyticsService.getTotalApplications(),
                "totalHires", recruitmentAnalyticsService.getTotalHires(),
                "userGrowth", recruitmentAnalyticsService.getUserGrowth(from, to),
                "recruitmentFunnel", recruitmentAnalyticsService.getRecruitmentFunnel(from, to));
    }
}
