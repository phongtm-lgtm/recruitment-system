package fu.se.recruitment_system.controller;

import fu.se.recruitment_system.dto.CreateJobPostRequest;
import fu.se.recruitment_system.dto.RecruiterJobPostResponse;
import fu.se.recruitment_system.dto.UpdateJobPostRequest;
import fu.se.recruitment_system.model.enums.JobPostStatus;
import fu.se.recruitment_system.service.pattern.proxy.JobPostingServiceProxy;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/jobs")
@AllArgsConstructor
public class RecruiterJobController {
    private final JobPostingServiceProxy jobPostingServiceProxy;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecruiterJobPostResponse create(
            @RequestHeader("X-Recruiter-Id") Long recruiterId,
            @RequestBody CreateJobPostRequest request) {
        return jobPostingServiceProxy.create(recruiterId, request);
    }

    @GetMapping
    public List<RecruiterJobPostResponse> viewJobPostings(
            @RequestHeader("X-Recruiter-Id") Long recruiterId,
            @RequestParam(required = false) JobPostStatus status) {
        return jobPostingServiceProxy.viewJobPostings(recruiterId, status);
    }

    @PutMapping("/{jobPostId}")
    public RecruiterJobPostResponse edit(
            @RequestHeader("X-Recruiter-Id") Long recruiterId,
            @PathVariable Long jobPostId,
            @RequestBody UpdateJobPostRequest request) {
        return jobPostingServiceProxy.edit(recruiterId, jobPostId, request);
    }

    @PatchMapping("/{jobPostId}/close")
    public RecruiterJobPostResponse close(
            @RequestHeader("X-Recruiter-Id") Long recruiterId,
            @PathVariable Long jobPostId) {
        return jobPostingServiceProxy.close(recruiterId, jobPostId);
    }

    @PatchMapping("/{jobPostId}/reactivate")
    public RecruiterJobPostResponse reactivate(
            @RequestHeader("X-Recruiter-Id") Long recruiterId,
            @PathVariable Long jobPostId,
            @RequestBody UpdateJobPostRequest request) {
        return jobPostingServiceProxy.reactivate(recruiterId, jobPostId, request);
    }
}
