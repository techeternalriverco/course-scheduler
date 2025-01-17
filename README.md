# University Course Scheduling API

## 📌 Overview
This is a **Spring Boot RESTful API** for managing university course scheduling. The system allows users (students) to **register**, **book courses**, **drop courses**, and **list their enrolled courses** while preventing scheduling conflicts.

---
## 🚀 Features
- **User Management**: Register students.
- **Course Management**: Create and list courses.
- **Enrollment System**:
  - Book a course (with conflict prevention for overlapping schedules).
  - Drop a course.
  - View enrolled courses.
- **Database Integration**: Uses **PostgreSQL**.
- **Spring Boot Architecture**: Implements **MVC design pattern**.
- **Error Handling**: Provides meaningful error messages.

---
## 🛠 Tech Stack
- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA** (for database interactions)
- **PostgreSQL** (Primary database)
- **JUnit 5 & Mockito** (For unit and integration testing)
- **Maven** (Dependency management)

---
## 📂 Project Structure
```
project-root/
 ├── src/main/java/com/university
 │    ├── controller/  # REST Controllers
 │    ├── entity/  # JPA Entities
 │    ├── repository/  # Data Access Layer
 │    ├── service/  # Business Logic
 │    ├── CourseSchedulerApplication.java  # Main Entry Point
 │
 ├── src/test/java/com/university
 │    ├── controller/  # Integration Tests
 │    ├── service/  # Unit Tests
 │    ├── repository/  # Repository Tests
 │
 ├── src/main/resources
 │    ├── application.yml  # Main Configuration
 │    ├── application-test.yml  # Test Configuration
 │
 ├── pom.xml  # Project Dependencies
 ├── README.md  # Project Documentation
```

---
## 🏗 Database Schema
### **Users Table** (`users`)
| Column  | Type          | Constraints     |
|---------|--------------|----------------|
| id      | SERIAL       | PRIMARY KEY    |
| name    | VARCHAR(255) | NOT NULL       |

### **Courses Table** (`courses`)
| Column     | Type      | Constraints        |
|------------|----------|--------------------|
| id         | SERIAL   | PRIMARY KEY       |
| name       | VARCHAR(255) | NOT NULL   |
| day_of_week | INT | CHECK (0-6)  |
| start_time | TIME | NOT NULL  |
| end_time   | TIME | NOT NULL (end_time > start_time) |

### **Enrolled Courses Table** (`enrolled_courses`)
| Column    | Type  | Constraints                      |
|-----------|------|----------------------------------|
| id        | SERIAL | PRIMARY KEY                   |
| user_id   | INT   | FOREIGN KEY (users) ON DELETE CASCADE |
| course_id | INT   | FOREIGN KEY (courses) ON DELETE CASCADE |
| UNIQUE (user_id, course_id) | Ensures no duplicate enrollments |

---
## 🌍 API Endpoints
### **User Management**
- **Register a User**: `POST /users`
- **List All Courses for a User**: `GET /users/{userId}/courses`
- **Book a Course**: `POST /users/{userId}/courses`
- **Drop a Course**: `DELETE /users/{userId}/courses/{courseId}`

### **Course Management**
- **Create a Course**: `POST /courses`
- **List All Courses**: `GET /courses`

---
## 🛠 Installation & Setup
### **1️⃣ Clone the Repository**
```bash
git clone https://github.com/your-username/university-course-scheduler.git
cd university-course-scheduler
```

### **2️⃣ Configure the Database**
Modify `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_db_name
    username: your_db_user
    password: your_db_password
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

### **3️⃣ Build & Run the Project**
```bash
mvn clean install
mvn spring-boot:run
```

### **4️⃣ Run Tests**
```bash
mvn test
```

---
## 🧪 Testing Strategy
- **Unit Tests** (`JUnit 5`, `Mockito`) → Tests for `CourseService` and `UserService`.
- **Integration Tests** (`MockMvc`) → Verifies full API behavior.
---
## 🏗 TODO

- 🔲 Database migration (Flyway/Liquibase).
- 🔲 Add Dockerfile for containerized deployment.
---
## 📜 License
This project is **open-source** and available under the **MIT License**.

