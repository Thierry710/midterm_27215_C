# midterm_27215_C
web tech mid-exam 
# Online Voting System

A full-stack web application built with **Spring Boot 3.2.0**, **Spring Security 6**, **PostgreSQL**, and **Thymeleaf**. It supports three roles (Admin, Candidate, Voter) and implements a full 5-level Rwanda location hierarchy.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2.0, Java 21 |
| Security | Spring Security 6 (Form Login + HTTP Basic) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Frontend | Thymeleaf + Bootstrap 5.3.2 |
| API | REST (JSON) for Postman |
| Build | Maven |

---

## Getting Started

### Prerequisites
- Java 21+
- Maven
- PostgreSQL

### Database Setup
```sql
CREATE DATABASE online_voting_db;
```

### Configuration (`application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/online_voting_db
spring.datasource.username=postgres
spring.datasource.password=123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=true
```

### Run the App
```powershell
# Always clean before running to avoid stale compiled classes
mvn clean spring-boot:run
```

### Default Admin Account
| Field | Value |
|-------|-------|
| URL | http://localhost:8080/login |
| Username | admin |
| Password | admin123 |

---

## Project Structure

```
src/main/java/auca/ac/rw/onlineVotingSystem/
├── model/          # JPA Entities (10 tables)
├── repository/     # Spring Data JPA repositories
├── service/        # Business logic
├── controller/     # Thymeleaf web UI controllers
├── api/            # REST API controllers (for Postman)
├── config/         # Spring Security configuration
└── enums/          # UserRole enum (ADMIN, CANDIDATE, VOTER)
```

---

## Requirement 1 — ERD (5+ Tables) ✅

The system has **10 entities** forming a complete ERD:

```
Province ──< District ──< Sector ──< Cell ──< Village ──< User >── VoterProfile
                                                                        │
Election >──── election_tags ────< Tag              Candidate >── Election
```

| Entity | Purpose |
|--------|---------|
| Province | Top-level Rwanda location (e.g. Kigali City) |
| District | Belongs to Province (e.g. Gasabo) |
| Sector | Belongs to District (e.g. Remera) |
| Cell | Belongs to Sector (e.g. Rukiri I) |
| Village | Belongs to Cell — **only field saved on User** |
| User | System user with role (ADMIN/CANDIDATE/VOTER) |
| VoterProfile | Extra voter details linked One-to-One with User |
| Election | A voting event, linked Many-to-Many with Tags |
| Candidate | Registered candidate linked to an Election |
| Tag | Label for elections (Many-to-Many) |

---

## Requirement 2 — Location Saving ✅

**Key rule:** A User saves **only `village_id`**. The system auto-resolves the full chain:

```
User (village_id) → Village → Cell → Sector → District → Province
```

```java
// User.java — only stores village_id
@ManyToOne
@JoinColumn(name = "village_id")
private Village village;

// Auto-resolve province without storing it on User
public String getProvinceName() {
    return village.getCell().getSector().getDistrict().getProvince().getName();
}
```

### Postman Workflow to Insert Your Own Data

**Step 1 — Create Province**
```
POST /api/locations/provinces
{ "code": "KIG", "name": "Kigali City" }
```

**Step 2 — Create District** (use provinceId from Step 1)
```
POST /api/locations/districts
{ "name": "Gasabo", "provinceId": 1 }
```

**Step 3 — Create Sector**
```
POST /api/locations/sectors
{ "name": "Remera", "districtId": 2 }
```

**Step 4 — Create Cell**
```
POST /api/locations/cells
{ "name": "Rukiri I", "sectorId": 3 }
```

**Step 5 — Create Village**
```
POST /api/locations/villages
{ "name": "Nyarutarama", "cellId": 4 }
```

**Step 6 — Create User with only villageId**
```
POST /api/users
{ "username": "mugabo", "password": "pass123", "role": "VOTER", "villageId": 5 }
```

Response includes the full resolved chain automatically:
```json
{
  "locationChain": {
    "village": "Nyarutarama",
    "cell": "Rukiri I",
    "sector": "Remera",
    "district": "Gasabo",
    "province": "Kigali City"
  }
}
```

---

## Requirement 3 — Sorting & Pagination ✅

Implemented in `UserService.java` using Spring Data JPA `PageRequest` and `Sort`:

```java
public Page<User> getVotersPaginated(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
    return userRepo.findByRole(UserRole.VOTER, pageable);
}
```

- **Pagination** splits large datasets into pages, reducing memory usage and improving performance. Only the requested page is loaded from the database.
- **Sorting** orders results by `username` ascending without loading all records.
- Used in the Admin dashboard at `/admin/voters?page=0&size=10`

---

## Requirement 4 — Many-to-Many Relationship ✅

`Election` ↔ `Tag` via a join table `election_tags`:

```java
// Election.java
@ManyToMany
@JoinTable(
    name = "election_tags",
    joinColumns = @JoinColumn(name = "election_id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private List<Tag> tags;

// Tag.java
@ManyToMany(mappedBy = "tags")
private List<Election> elections;
```

The join table `election_tags` holds two foreign keys (`election_id`, `tag_id`), allowing many elections to have many tags and vice versa.

---

## Requirement 5 — One-to-Many Relationship ✅

Every level of the location hierarchy is One-to-Many:

```java
// Province.java — one province has many districts
@OneToMany(mappedBy = "province", cascade = CascadeType.ALL)
private List<District> districts;

// District.java — belongs to one province
@ManyToOne
@JoinColumn(name = "province_id")
private Province province;
```

Same pattern repeats: District→Sector, Sector→Cell, Cell→Village, Village→User.

---

## Requirement 6 — One-to-One Relationship ✅

`User` ↔ `VoterProfile` — each user has exactly one profile:

```java
// User.java
@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
private VoterProfile voterProfile;

// VoterProfile.java
@OneToOne
@JoinColumn(name = "user_id", unique = true)
private User user;
```

The `unique = true` constraint on `user_id` enforces the one-to-one constraint at the database level.

---

## Requirement 7 — existsBy() Methods ✅

Used across all repositories to prevent duplicates before saving:

```java
// UserRepository
boolean existsByUsername(String username);

// ProvinceRepository
boolean existsByCode(String code);

// VoterProfileRepository
boolean existsByNationalId(String nationalId);

// CandidateRepository
boolean existsByNameAndElectionId(String name, Long electionId);
```

Spring Data JPA auto-generates the SQL `SELECT COUNT(*) > 0 WHERE ...` query from the method name. No custom SQL needed.

---

## Requirement 8 — Retrieve Users by Province (code OR name) ✅

```java
// UserRepository.java
@Query("SELECT u FROM User u WHERE " +
       "u.village.cell.sector.district.province.code = :search OR " +
       "u.village.cell.sector.district.province.name = :search")
List<User> findByProvinceCodeOrName(@Param("search") String search);
```

The JPQL query traverses the full location chain from `User` → `village` → `cell` → `sector` → `district` → `province` without needing to store the province on the User directly.

### Postman — Retrieve Users by Any Location Level

```
GET /api/users/by-province/KIG          ← by province code
GET /api/users/by-province/Kigali City  ← by province name
GET /api/users/by-district/Gasabo
GET /api/users/by-sector/Remera
GET /api/users/by-cell/Rukiri I
GET /api/users/by-village/Nyarutarama
```

---

## REST API Reference (Postman)

**Authentication:** GET requests are public. POST/PUT/DELETE require Basic Auth (`admin` / `admin123`).

### Users
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/users` | All users |
| GET | `/api/users/{id}` | User by ID |
| GET | `/api/users/role/VOTER` | Users by role |
| GET | `/api/users/by-province/KIG` | Users by province |
| GET | `/api/users/by-district/Gasabo` | Users by district |
| GET | `/api/users/by-sector/Remera` | Users by sector |
| GET | `/api/users/by-cell/Rukiri I` | Users by cell |
| GET | `/api/users/by-village/Village A` | Users by village |
| POST | `/api/users` | Create user (villageId only) |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Locations
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/locations/villages/all-with-chain` | All villages with full chain |
| GET | `/api/locations/villages/{id}/chain` | Single village full chain |
| POST | `/api/locations/provinces` | Create province |
| POST | `/api/locations/districts` | Create district |
| POST | `/api/locations/sectors` | Create sector |
| POST | `/api/locations/cells` | Create cell |
| POST | `/api/locations/villages` | Create village |

### Elections & Candidates
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/elections` | All elections |
| GET | `/api/elections/active` | Active election |
| POST | `/api/elections` | Create election |
| GET | `/api/candidates` | All candidates |
| GET | `/api/candidates/approved` | Approved candidates only |
| POST | `/api/candidates` | Register candidate |
| PUT | `/api/candidates/{id}/approve` | Approve a candidate |

---

## Web UI (Browser)

| URL | Role | Description |
|-----|------|-------------|
| `/login` | All | Login page |
| `/register` | All | Register new user |
| `/admin/dashboard` | Admin | Manage elections, approve candidates |
| `/admin/voters` | Admin | Paginated voter list |
| `/admin/locations` | Admin | Location hierarchy view |
| `/candidate/dashboard` | Candidate | Register for election |
| `/voter/dashboard` | Voter | Cast vote |
| `/admin/results` | Admin | Election results |

---

## Roles & Permissions

| Role | Access |
|------|--------|
| ADMIN | Full access — create elections, approve candidates, view all voters |
| CANDIDATE | Register for election, view results |
| VOTER | Cast one vote, view results |
