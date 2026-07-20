# API Document for Frontend - UC-12 to UC-39

Base URL mac dinh:

```text
http://localhost:8080
```

Tat ca request/response dung JSON. Header id hien dang dung tam theo convention backend hien co, chua co JWT/security filter.

## Common Error Format

Spring `ResponseStatusException` se tra response loi theo format mac dinh cua Spring Boot. FE nen xu ly theo HTTP status:

| Status | Y nghia |
| --- | --- |
| 400 | Request sai validation |
| 403 | Khong co quyen |
| 404 | Khong tim thay resource |
| 409 | Conflict, vi du duplicate name hoac job da duoc moderate |

## Job Discovery APIs

### UC-12 Search & Filter Jobs

```http
GET /api/jobs/discovery
```

Query params:

| Param | Type | Required | Note |
| --- | --- | --- | --- |
| `keyword` | string | No | Toi da 256 ky tu |
| `categoryId` | number | No | Filter category |
| `industry` | string | No | Filter industry |
| `location` | string | No | Filter location |

Rule:

- Chi tra job `ACTIVE`.
- Toi da 20 records.
- Sort featured truoc, sau do moi nhat truoc.

Example:

```http
GET /api/jobs/discovery?keyword=java&categoryId=1&location=Ho%20Chi%20Minh
```

Response `200`:

```json
[
  {
    "id": 10,
    "title": "Senior Java Backend Developer",
    "industry": "Information Technology",
    "location": "Ho Chi Minh",
    "categoryName": "IT",
    "companyName": "ABC Software",
    "salaryRange": "2000-3000 USD",
    "status": "ACTIVE",
    "featured": true,
    "createdAt": "2026-07-20T08:00:00"
  }
]
```

### UC-13 View Job Details

```http
GET /api/jobs/discovery/{jobPostId}
```

Headers:

| Header | Type | Required | Note |
| --- | --- | --- | --- |
| `X-Job-Seeker-Id` | number | No | Neu co, backend kiem tra da apply trong 30 ngay gan nhat |

Rule:

- Chi xem duoc job `ACTIVE`.
- Moi lan goi endpoint se tang `viewCount`.

Example:

```http
GET /api/jobs/discovery/10
X-Job-Seeker-Id: 5
```

Response `200`:

```json
{
  "id": 10,
  "title": "Senior Java Backend Developer",
  "industry": "Information Technology",
  "location": "Ho Chi Minh",
  "experienceLevel": "Senior",
  "workType": "Full-time",
  "categoryName": "IT",
  "companyProfileId": 3,
  "companyName": "ABC Software",
  "jobDescription": "Build backend services...",
  "requirements": "Java, Spring Boot, MySQL",
  "benefits": "Insurance, bonus",
  "salaryRange": "2000-3000 USD",
  "applicationDeadline": "2026-08-20T23:59:00",
  "applicationClosed": false,
  "status": "ACTIVE",
  "featured": true,
  "viewCount": 101,
  "applicationCount": 12,
  "appliedInLast30Days": false,
  "createdAt": "2026-07-20T08:00:00",
  "updatedAt": "2026-07-20T08:30:00"
}
```

### UC-14 View Company Profile

```http
GET /api/jobs/discovery/companies/{companyProfileId}
```

Response `200`:

```json
{
  "id": 3,
  "userId": 8,
  "companyName": "ABC Software",
  "businessField": "Software Outsourcing",
  "website": "https://abc.example",
  "email": "hr@abc.example",
  "phone": "0900000000",
  "address": "Ho Chi Minh",
  "description": "Company profile description",
  "logoUrl": "https://cdn.example/logo.png",
  "images": "https://cdn.example/company-1.png",
  "verificationStatus": "VERIFIED",
  "activeJobs": [
    {
      "id": 10,
      "title": "Senior Java Backend Developer",
      "industry": "Information Technology",
      "location": "Ho Chi Minh",
      "categoryName": "IT",
      "companyName": "ABC Software",
      "salaryRange": "2000-3000 USD",
      "status": "ACTIVE",
      "featured": true,
      "createdAt": "2026-07-20T08:00:00"
    }
  ]
}
```

## Recruiter Job APIs

Tat ca endpoint recruiter can header:

```http
X-Recruiter-Id: {recruiterId}
```

Backend yeu cau user co role `RECRUITER`.

### UC-20 Create Job Post

```http
POST /api/recruiter/jobs
```

Request body:

```json
{
  "categoryId": 1,
  "title": "Senior Java Backend Developer",
  "industry": "Information Technology",
  "location": "Ho Chi Minh",
  "experienceLevel": "Senior",
  "workType": "Full-time",
  "jobDescription": "Build backend services with Spring Boot.",
  "requirements": "Java, Spring Boot, MySQL",
  "benefits": "Insurance, bonus",
  "salaryRange": "2000-3000 USD",
  "applicationDeadline": "2026-08-20T23:59:00",
  "submitForModeration": true
}
```

Rule:

- `title`: 10-100 ky tu.
- `categoryId`: bat buoc, category phai active.
- `jobDescription`: bat buoc.
- Neu `submitForModeration = true`, `requirements` bat buoc va `applicationDeadline` phai trong tuong lai.
- `submitForModeration = true` tao job status `PENDING`.
- `submitForModeration = false` tao job status `DRAFT`.

Response `201`:

```json
{
  "id": 10,
  "title": "Senior Java Backend Developer",
  "industry": "Information Technology",
  "location": "Ho Chi Minh",
  "categoryName": "IT",
  "salaryRange": "2000-3000 USD",
  "applicationDeadline": "2026-08-20T23:59:00",
  "status": "PENDING",
  "rejectionReason": null,
  "featured": false,
  "viewCount": 0,
  "createdAt": "2026-07-20T08:00:00",
  "updatedAt": "2026-07-20T08:00:00"
}
```

### UC-21 View Job Postings

```http
GET /api/recruiter/jobs
```

Query params:

| Param | Type | Required | Note |
| --- | --- | --- | --- |
| `status` | enum | No | `DRAFT`, `PENDING`, `ACTIVE`, `REJECTED`, `ESCALATED`, `CLOSED` |

Example:

```http
GET /api/recruiter/jobs?status=PENDING
X-Recruiter-Id: 8
```

Response `200`:

```json
[
  {
    "id": 10,
    "title": "Senior Java Backend Developer",
    "industry": "Information Technology",
    "location": "Ho Chi Minh",
    "categoryName": "IT",
    "salaryRange": "2000-3000 USD",
    "applicationDeadline": "2026-08-20T23:59:00",
    "status": "PENDING",
    "rejectionReason": null,
    "featured": false,
    "viewCount": 0,
    "createdAt": "2026-07-20T08:00:00",
    "updatedAt": "2026-07-20T08:00:00"
  }
]
```

### UC-22 Edit Job Posting

```http
PUT /api/recruiter/jobs/{jobPostId}
```

Headers:

```http
X-Recruiter-Id: 8
```

Request body: same as create, but type is update.

Rule:

- Recruiter chi sua job cua minh.
- Job `CLOSED` khong duoc edit.
- Neu `submitForModeration = true`, status duoc dua ve `PENDING`.

Response `200`: `RecruiterJobPostResponse`.

### UC-22 Close Job Posting

```http
PATCH /api/recruiter/jobs/{jobPostId}/close
```

Headers:

```http
X-Recruiter-Id: 8
```

Response `200`:

```json
{
  "id": 10,
  "title": "Senior Java Backend Developer",
  "industry": "Information Technology",
  "location": "Ho Chi Minh",
  "categoryName": "IT",
  "salaryRange": "2000-3000 USD",
  "applicationDeadline": "2026-08-20T23:59:00",
  "status": "CLOSED",
  "rejectionReason": null,
  "featured": false,
  "viewCount": 0,
  "createdAt": "2026-07-20T08:00:00",
  "updatedAt": "2026-07-20T09:00:00"
}
```

### UC-22 Reactivate Job Posting

```http
PATCH /api/recruiter/jobs/{jobPostId}/reactivate
```

Headers:

```http
X-Recruiter-Id: 8
```

Request body: same fields as update.

Rule:

- Chi job `CLOSED` moi reactivate duoc.
- Reactivate dua status ve `PENDING`.
- `applicationDeadline` phai trong tuong lai.

Response `200`: `RecruiterJobPostResponse`.

## Moderator APIs

Tat ca endpoint moderator can header:

```http
X-Moderator-Id: {moderatorId}
```

Backend chap nhan role `MODERATOR` hoac `ADMIN`.

### UC-30 View Pending Job Postings

```http
GET /api/moderator/jobs/pending
```

Response `200`:

```json
[
  {
    "id": 10,
    "title": "Senior Java Backend Developer",
    "industry": "Information Technology",
    "location": "Ho Chi Minh",
    "categoryName": "IT",
    "salaryRange": "2000-3000 USD",
    "applicationDeadline": "2026-08-20T23:59:00",
    "status": "PENDING",
    "rejectionReason": null,
    "featured": false,
    "viewCount": 0,
    "createdAt": "2026-07-20T08:00:00",
    "updatedAt": "2026-07-20T08:00:00"
  }
]
```

### UC-30 Approve Job Posting

```http
PATCH /api/moderator/jobs/{jobPostId}/approve
```

Response `200`:

```json
{
  "jobPostId": 10,
  "status": "ACTIVE",
  "rejectionReason": null,
  "strategy": "ApproveModerationStrategy",
  "auditAction": "JOB_POST_STATUS_CHANGED"
}
```

### UC-30 Reject Job Posting

```http
PATCH /api/moderator/jobs/{jobPostId}/reject
```

Request body:

```json
{
  "reason": "Job content violates platform policy."
}
```

Rule:

- `reason` bat buoc khong rong.

Response `200`:

```json
{
  "jobPostId": 10,
  "status": "REJECTED",
  "rejectionReason": "Job content violates platform policy.",
  "strategy": "RejectModerationStrategy",
  "auditAction": "JOB_POST_STATUS_CHANGED"
}
```

### UC-30 Escalate Job Posting

```http
PATCH /api/moderator/jobs/{jobPostId}/escalate
```

Request body optional:

```json
{
  "reason": "Needs admin review."
}
```

Response `200`:

```json
{
  "jobPostId": 10,
  "status": "ESCALATED",
  "rejectionReason": null,
  "strategy": "EscalateModerationStrategy",
  "auditAction": "JOB_POST_STATUS_CHANGED"
}
```

Conflict:

- Neu job khong con `PENDING`, backend tra `409`.

## Admin Category APIs

Tat ca endpoint admin can header:

```http
X-Admin-Id: {adminId}
```

Backend yeu cau role `ADMIN`.

### UC-37 Create Category

```http
POST /api/admin/categories
```

Request body:

```json
{
  "name": "Information Technology",
  "type": "INDUSTRY"
}
```

Rule:

- `name`: 3-50 ky tu.
- `type`: optional, default `INDUSTRY`.
- `name + type` phai unique.
- Category moi co `active = true`.

Response `201`:

```json
{
  "id": 1,
  "name": "Information Technology",
  "type": "INDUSTRY",
  "active": true,
  "createdAt": "2026-07-20T08:00:00",
  "updatedAt": "2026-07-20T08:00:00"
}
```

### UC-38 Edit Category

```http
PUT /api/admin/categories/{categoryId}
```

Request body:

```json
{
  "name": "Software Engineering",
  "type": "INDUSTRY"
}
```

Response `200`:

```json
{
  "id": 1,
  "name": "Software Engineering",
  "type": "INDUSTRY",
  "active": true,
  "createdAt": "2026-07-20T08:00:00",
  "updatedAt": "2026-07-20T09:00:00"
}
```

### UC-39 Deactivate Category

```http
DELETE /api/admin/categories/{categoryId}
```

Rule:

- Khong xoa category khoi DB.
- Chi set `active = false`.
- Job post lich su van giu category cu.

Response `200`:

```json
{
  "id": 1,
  "name": "Software Engineering",
  "type": "INDUSTRY",
  "active": false,
  "createdAt": "2026-07-20T08:00:00",
  "updatedAt": "2026-07-20T10:00:00"
}
```

## Enum Values FE Can Use

### JobPostStatus

```text
DRAFT
PENDING
ACTIVE
REJECTED
ESCALATED
CLOSED
```

### CategoryType

```text
INDUSTRY
JOB_TYPE
OTHER
```

### UserRole

```text
JOB_SEEKER
RECRUITER
MODERATOR
ADMIN
```

## FE Notes

- Public job list/detail/company profile khong can auth header, rieng job detail co the truyen `X-Job-Seeker-Id` neu user da login.
- Recruiter screens phai gui `X-Recruiter-Id`.
- Moderator screens phai gui `X-Moderator-Id`.
- Admin category screens phai gui `X-Admin-Id`.
- FE nen disable edit button khi job status la `CLOSED`, chi hien action reactivate.
- FE nen hien rejection reason khi status la `REJECTED`.
- FE nen khong cho submit job neu deadline khong nam trong tuong lai.
- FE nen validate category name 3-50 ky tu truoc khi goi API.
