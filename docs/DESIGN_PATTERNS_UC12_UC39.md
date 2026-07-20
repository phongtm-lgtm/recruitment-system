# Design Pattern Backend cho UC-12 đến UC-39

Tài liệu này mô tả cách backend triển khai các use case về tìm kiếm việc làm, quản lý tin tuyển dụng của recruiter, kiểm duyệt tin tuyển dụng và quản lý danh mục hệ thống. Mục tiêu là khi đọc code có thể nhận ra ngay design pattern nào đang được áp dụng.

## Tổng Quan Class Flow

| UC | Luồng class chính | Pattern |
| --- | --- | --- |
| UC-12 Search & Filter Jobs | `JobDiscoveryController.search()` -> `JobDiscoveryFacade` -> `JobDiscoveryFacadeService.searchJobs()` -> `JobPostingQueryService` + `CompanyProfileQueryService` | Facade |
| UC-13 View Job Details | `JobDiscoveryController.getJobDetail()` -> `JobDiscoveryFacade` -> `JobDiscoveryFacadeService.getJobDetail()` -> `ViewCountService` + `JobPostingQueryService` + `ApplicationHistoryService` + `CompanyProfileQueryService` | Facade |
| UC-14 View Company Profile | `JobDiscoveryController.getCompanyProfile()` -> `JobDiscoveryFacade` -> `JobDiscoveryFacadeService.getCompanyProfile()` -> `CompanyProfileQueryService` + `JobPostingQueryService` | Facade |
| UC-20 Create Job Post | `RecruiterJobController.create()` -> `JobPostingServiceProxy` -> `JobPostingServiceImpl.create()` -> `DraftJobPostAuditLogCreator` hoặc `SubmittedJobPostAuditLogCreator` | Proxy + Factory Method |
| UC-21 View Job Postings | `RecruiterJobController.viewJobPostings()` -> `JobPostingServiceProxy` -> `JobPostingServiceImpl.viewJobPostings()` -> `JobPostRepository` | Proxy |
| UC-22 Maintain Job Posting | `RecruiterJobController.edit/close/reactivate()` -> `JobPostingServiceProxy` -> `JobPostingServiceImpl` -> audit creator | Proxy + Factory Method |
| UC-30 Moderate Job Posting | `ModeratorJobController` -> `ModerationService` -> `Approve/Reject/EscalateModerationStrategy` -> audit creator | Strategy + Factory Method |
| UC-37 Create Category | `CategoryAdminController.create()` -> `CreateCategoryService extends AbstractMasterDataService` | Template Method |
| UC-38 Edit Category | `CategoryAdminController.edit()` -> `EditCategoryService extends AbstractMasterDataService` | Template Method |
| UC-39 Deactivate Category | `CategoryAdminController.deactivate()` -> `DeactivateCategoryService extends AbstractMasterDataService` | Template Method |

## Package Thực Tế Trong Source

Các class dùng để thể hiện design pattern được đặt trong package `fu.se.recruitment_system.service.pattern.*`:

| Pattern | Package |
| --- | --- |
| Facade | `fu.se.recruitment_system.service.pattern.facade` |
| Proxy | `fu.se.recruitment_system.service.pattern.proxy` |
| Factory Method | `fu.se.recruitment_system.service.pattern.audit` |
| Strategy | `fu.se.recruitment_system.service.pattern.moderation.strategy` |
| Template Method | `fu.se.recruitment_system.service.pattern.masterdata` |

Các subsystem service không trực tiếp đại diện cho pattern nằm trong package service thường:

| Subsystem | Package |
| --- | --- |
| Job query | `fu.se.recruitment_system.service.JobPostingQueryService` |
| Company profile query | `fu.se.recruitment_system.service.CompanyProfileQueryService` |
| Application history | `fu.se.recruitment_system.service.ApplicationHistoryService` |
| View count | `fu.se.recruitment_system.service.ViewCountService` |
| Admin authorization | `fu.se.recruitment_system.service.AdminAuthorizationService` |

## 1. Facade Pattern

### Áp dụng cho UC-12, UC-13, UC-14

Facade abstraction nằm tại:

```text
fu.se.recruitment_system.service.pattern.facade.JobDiscoveryFacade
```

Concrete facade nằm tại:

```text
fu.se.recruitment_system.service.pattern.facade.JobDiscoveryFacadeService
```

Controller public chỉ gọi facade:

```text
JobDiscoveryController -> JobDiscoveryFacade -> JobDiscoveryFacadeService
```

Facade che giấu việc điều phối nhiều subsystem service bên trong:

```text
JobDiscoveryFacadeService
  -> JobPostingQueryService
  -> ApplicationHistoryService
  -> ViewCountService
  -> CompanyProfileQueryService
```

Theo class diagram của Facade pattern:

- `JobDiscoveryController` đóng vai trò `Client`.
- `JobDiscoveryFacade` đóng vai trò `Facade` interface.
- `JobDiscoveryFacadeService` đóng vai trò concrete facade.
- `JobPostingQueryService`, `ApplicationHistoryService`, `ViewCountService`, `CompanyProfileQueryService` là các subsystem classes.

### UC-12 Search & Filter Jobs

`JobDiscoveryController.search()` nhận `keyword`, `categoryId`, `industry`, `location`. Controller không tự build query. Controller phụ thuộc vào `JobDiscoveryFacade` và chuyển request sang `JobDiscoveryFacadeService.searchJobs()`.

Facade gọi `JobPostingQueryService.searchActiveJobs()` để đảm bảo:

- Keyword không vượt quá 256 ký tự.
- Chỉ lấy job có status `ACTIVE`.
- Kết quả giới hạn 20 bản ghi.
- Sắp xếp ưu tiên featured job và job mới.

Sau đó facade gọi `CompanyProfileQueryService` để lấy tên công ty và map entity sang `JobSearchResponse`.

### UC-13 View Job Details

`JobDiscoveryFacadeService.getJobDetail()` điều phối các bước:

- Lấy job đang `ACTIVE` bằng `JobPostingQueryService.getActiveJobDetail()`.
- Tăng view count bằng `ViewCountService.incrementViewCount()`.
- Lấy số lượng application và lịch sử apply bằng `ApplicationHistoryService`.
- Lấy thông tin công ty bằng `CompanyProfileQueryService`.
- Map dữ liệu sang `JobDetailResponse`.

Controller không cần biết view count, application history và company profile được xử lý như thế nào. Đây là lý do dùng Facade.

### UC-14 View Company Profile

`JobDiscoveryFacadeService.getCompanyProfile()` lấy:

- Thông tin company profile từ subsystem `CompanyProfileQueryService`.
- Danh sách active jobs của công ty từ `JobPostingQueryService.getActiveJobsByCompany()`.

Kết quả trả về `CompanyProfileResponse`, gồm thông tin công ty và danh sách `activeJobs`.

## 2. Proxy Pattern

### Áp dụng cho UC-20, UC-21, UC-22

Interface:

```text
fu.se.recruitment_system.service.pattern.proxy.JobPostingService
```

Proxy:

```text
fu.se.recruitment_system.service.pattern.proxy.JobPostingServiceProxy
```

Real subject:

```text
fu.se.recruitment_system.service.pattern.proxy.JobPostingServiceImpl
```

Luồng xử lý:

```text
RecruiterJobController
  -> JobPostingServiceProxy
  -> JobPostingServiceImpl
  -> JobPostRepository
```

Theo class diagram của Proxy pattern:

- `JobPostingService` là `Subject`.
- `JobPostingServiceImpl` là `RealSubject`.
- `JobPostingServiceProxy` là `Proxy`.
- `RecruiterJobController` là `Client`.

`JobPostingServiceProxy` chịu trách nhiệm bảo vệ service thật:

- Kiểm tra `X-Recruiter-Id` có tồn tại.
- Kiểm tra user có role `RECRUITER`.
- Kiểm tra ownership trước khi edit/close/reactivate.

`JobPostingServiceImpl` chỉ tập trung vào nghiệp vụ tạo, xem, sửa, đóng và mở lại job post.

### UC-20 Create Job Post

Khi tạo job post, controller vẫn gọi qua proxy:

```text
RecruiterJobController.create()
  -> JobPostingServiceProxy.create()
  -> JobPostingServiceImpl.create()
```

Proxy xác thực recruiter. Service thật validate dữ liệu, tạo `JobPost`, set status `DRAFT` hoặc `PENDING`, sau đó gọi audit creator tương ứng.

### UC-21 View Job Postings

Proxy xác thực recruiter. Service thật lấy tối đa 20 job posting của recruiter:

```text
findTop20ByRecruiterIdOrderByCreatedAtDesc()
```

Nếu FE truyền `status`, service dùng:

```text
findTop20ByRecruiterIdAndStatusOrderByCreatedAtDesc()
```

### UC-22 Maintain Job Posting

Proxy bắt buộc job post phải thuộc recruiter:

```text
existsByIdAndRecruiterId(jobPostId, recruiterId)
```

Service thật xử lý rule:

- Job `CLOSED` không được edit, chỉ được reactivate.
- Reactivate đưa job về `PENDING`.
- Submit lại job sau edit đưa status về `PENDING`.
- Category inactive không được chọn khi tạo/sửa/reactivate.

## 3. Factory Method Pattern

### Áp dụng cho UC-20, UC-22, UC-30

Base creator:

```text
fu.se.recruitment_system.service.pattern.audit.AbstractJobPostAuditLogCreator
```

Concrete creators:

```text
fu.se.recruitment_system.service.pattern.audit.DraftJobPostAuditLogCreator
fu.se.recruitment_system.service.pattern.audit.SubmittedJobPostAuditLogCreator
fu.se.recruitment_system.service.pattern.audit.StatusChangeJobPostAuditLogCreator
```

Method quan trọng:

```text
createAndSave(JobPost jobPost, User actor, String message)
  -> createAuditLog(...)
  -> auditLogRepository.save(...)
```

Theo class diagram của Factory Method pattern:

- `AbstractJobPostAuditLogCreator` là `Creator`.
- `createAuditLog(...)` là factory method.
- `DraftJobPostAuditLogCreator`, `SubmittedJobPostAuditLogCreator`, `StatusChangeJobPostAuditLogCreator` là `ConcreteCreator`.
- `fu.se.recruitment_system.model.AuditLog` là `Product` và cũng là `ConcreteProduct` trong triển khai hiện tại.

Trong class diagram Factory Method chuẩn thường có `Product` là interface/abstract class và `ConcreteProduct` là class cụ thể. Ở backend này chưa tách thêm interface/abstract cho audit log vì hệ thống chỉ cần lưu một entity log duy nhất. Do đó `AuditLog` chính là object cụ thể được factory method tạo ra và lưu xuống DB.

Vị trí file:

```text
src/main/java/fu/se/recruitment_system/model/AuditLog.java
```

`createAndSave()` là luồng cố định. Concrete creator override `createAuditLog()` để tạo audit log phù hợp từng action.

### UC-20 Create Job Post

Nếu recruiter save draft:

```text
DraftJobPostAuditLogCreator -> JOB_POST_DRAFT_CREATED
```

Nếu recruiter submit moderation:

```text
SubmittedJobPostAuditLogCreator -> JOB_POST_SUBMITTED
```

### UC-22 Maintain Job Posting

Edit/close dùng:

```text
StatusChangeJobPostAuditLogCreator -> JOB_POST_STATUS_CHANGED
```

Reactivate dùng:

```text
SubmittedJobPostAuditLogCreator -> JOB_POST_SUBMITTED
```

Vì reactivate phải đưa job về `PENDING` để moderator duyệt lại.

### UC-30 Moderate Job Posting

Sau khi approve/reject/escalate, `ModerationService` tạo audit log bằng:

```text
StatusChangeJobPostAuditLogCreator
```

Audit log lưu actor moderator/admin, entity type `JobPost`, entity id và message decision.

## 4. Strategy Pattern

### Áp dụng cho UC-30 Moderate Job Posting

Interface:

```text
fu.se.recruitment_system.service.pattern.moderation.strategy.ModerationStrategy
```

Concrete strategies:

```text
fu.se.recruitment_system.service.pattern.moderation.strategy.ApproveModerationStrategy
fu.se.recruitment_system.service.pattern.moderation.strategy.RejectModerationStrategy
fu.se.recruitment_system.service.pattern.moderation.strategy.EscalateModerationStrategy
```

`ModerationService` inject danh sách strategy và tạo map:

```text
decision -> strategy
```

Theo class diagram của Strategy pattern:

- `fu.se.recruitment_system.service.pattern.moderation.ModerationService` là `Context`.
- `ModerationStrategy` là `Strategy`.
- `ApproveModerationStrategy`, `RejectModerationStrategy`, `EscalateModerationStrategy` là `ConcreteStrategy`.

Khi controller gọi endpoint:

- `/approve` -> decision `approve` -> `ApproveModerationStrategy`
- `/reject` -> decision `reject` -> `RejectModerationStrategy`
- `/escalate` -> decision `escalate` -> `EscalateModerationStrategy`

Mỗi strategy tự quyết định cách cập nhật job:

- Approve: `PENDING -> ACTIVE`, clear rejection reason.
- Reject: `PENDING -> REJECTED`, bắt buộc có reason.
- Escalate: `PENDING -> ESCALATED`, clear rejection reason.

`ModerationService` còn kiểm tra concurrent conflict: chỉ job đang `PENDING` mới được moderate. Nếu job đã được xử lý, backend trả `409 CONFLICT`.

## 5. Template Method Pattern

### Áp dụng cho UC-37, UC-38, UC-39

Abstract class:

```text
fu.se.recruitment_system.service.pattern.masterdata.AbstractMasterDataService
```

Concrete services:

```text
fu.se.recruitment_system.service.pattern.masterdata.CreateCategoryService
fu.se.recruitment_system.service.pattern.masterdata.EditCategoryService
fu.se.recruitment_system.service.pattern.masterdata.DeactivateCategoryService
```

Template method:

```text
execute(adminId, categoryId, request)
```

Luôn chạy theo thứ tự:

```text
verifyMasterDataPermission()
validateRequest()
resolveCategory()
applyOperation()
categoryRepository.save()
toResponse()
```

Theo class diagram của Template Method pattern:

- `AbstractMasterDataService` là abstract class định nghĩa template method `execute(...)`.
- `CreateCategoryService`, `EditCategoryService`, `DeactivateCategoryService` là concrete classes.
- `resolveCategory(...)` và `applyOperation(...)` là các primitive operations được subclass cài đặt.

Phần giống nhau được giữ trong abstract class:

- Kiểm tra admin permission.
- Validate name 3-50 ký tự.
- Validate duplicate name theo type.
- Save và map response.

Phần thay đổi được giao cho subclass:

- `CreateCategoryService`: tạo category mới, active = true.
- `EditCategoryService`: load category hiện có và update name/type.
- `DeactivateCategoryService`: load category và set active = false.

## 6. Rule Nghiệp Vụ Đã Gắn Vào Code

- Public job seeker chỉ thấy job `ACTIVE`.
- Search keyword tối đa 256 ký tự.
- Search/recruiter dashboard/moderation queue giới hạn 20 bản ghi.
- Job title phải từ 10 đến 100 ký tự.
- Submit/reactivate job cần `applicationDeadline` trong tương lai.
- Category inactive không được chọn khi create/edit/reactivate job.
- Recruiter chỉ được manage job của chính mình.
- Closed job chỉ được reactivate.
- Reject moderation bắt buộc có reason.
- Job đã không còn `PENDING` thì moderator không được moderate lại.
