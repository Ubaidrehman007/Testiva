# 🎓 Testiva - Timed Mock Test Portal

Testiva is a full-stack online mock test and examination portal developed for colleges and educational institutions using Spring Boot, Thymeleaf, Bootstrap, and PostgreSQL.

The platform provides secure login-based access for Admins and Students with separate dashboards, timed examinations, and role-based functionalities.

---

# 🚀 Core Features

## 👨‍💼 Admin Module
- Secure Admin Login
- Admin Dashboard
- Manage Students
- Create Timed Mock Tests
- Upload Test Information
- Approve Student Registrations
- Monitor Student Activities
- Manage Test Results
- Control Test Availability
- Email Notifications

---

## 👨‍🎓 Student Module
- Student Registration & Login
- Email-Based Approval System
- Student Dashboard
- View Available Mock Tests
- Attempt Timed Online Exams
- Auto Test Submission on Timer End
- View Test Results
- Profile Management

---

# ⏳ Timed Mock Test System

- Real-time countdown timer
- Automatic test submission after time completion
- Multiple test management
- Dynamic question handling
- Separate test duration controls

---

# 🛠️ Tech Stack

## Backend
- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate

## Frontend
- HTML5
- CSS3
- Bootstrap 5
- Thymeleaf
- JavaScript

## Database
- PostgreSQL

## Deployment & DevOps
- Docker
- Render
- GitHub Actions (CI/CD)

---

# 📂 Project Structure

```bash
src/
 ├── main/
 │    ├── java/
 │    │     └── com.project.Testiva
 │    │           ├── Controller
 │    │           ├── Model
 │    │           ├── Repository
 │    │           ├── Service
 │    │           └── Config
 │    │
 │    └── resources/
 │          ├── static/
 │          ├── templates/
 │          └── application.properties
```

---

# 🔐 Authentication & Authorization

Testiva uses role-based authentication with separate access permissions for:

- ADMIN
- STUDENT

Each user role has a dedicated dashboard and protected routes.

---

# 📧 Email Functionality

The platform supports automated email notifications for:
- Student approval updates
- Registration-related notifications
- System communication

---

# 🐳 Docker Support

The application is fully containerized using Docker for simplified deployment and scalability.

## Build Docker Image

```bash
docker build -t testiva .
```

## Run Docker Container

```bash
docker run -p 8484:8484 testiva
```

---

# ▶️ Run Project Locally

## Clone Repository

```bash
git clone https://github.com/your-username/Testiva.git
```

## Navigate to Project

```bash
cd Testiva
```

## Run Spring Boot Application

```bash
./mvnw spring-boot:run
```

---

# 📸 Project Screenshots

<img width="2874" height="1626" alt="image" src="https://github.com/user-attachments/assets/a240d9b5-ab18-4661-95a5-80a3872086fc" />

---

## 👨‍💼 Admin Dashboard
<img width="2868" height="1622" alt="image" src="https://github.com/user-attachments/assets/8cc946e0-5fd3-4b9c-8c42-5ee5a35cd72f" />



---

## 👨‍🎓 Student Dashboard
<img width="2868" height="1626" alt="image" src="https://github.com/user-attachments/assets/730a283d-cdb4-40cd-876f-c73e07d593b9" />


---

## ⏳ Timed Mock Test
<img width="2866" height="1622" alt="image" src="https://github.com/user-attachments/assets/43fc1507-bedb-4dee-a50e-456b2251277c" />

<img width="2842" height="1622" alt="image" src="https://github.com/user-attachments/assets/26f81a07-743f-4ba5-9a61-d7252a2d440a" />

<img width="2674" height="1304" alt="image" src="https://github.com/user-attachments/assets/0148f52e-9c99-493a-ba70-1d3f84096168" />
<img width="2866" height="1616" alt="image" src="https://github.com/user-attachments/assets/6ad051bd-b894-4734-a9fe-7a62534ceb44" />

---

## 📊 Result Page
<img width="2862" height="1620" alt="image" src="https://github.com/user-attachments/assets/d16d6ec4-e654-41c7-bc3f-e1c0734ff198" />


---

# 🌐 Live Project

https://testiva.onrender.com

---

# 📌 Upcoming Enhancements

- JWT Authentication
- AI-Based Proctoring
- Leaderboard System
- Result Analytics
- PDF Report Generation
- Face Recognition Authentication
- Performance Tracking Dashboard
- Dark Mode UI

---

# 👨‍💻 Developer

### Ubaid Rehman

Full Stack Java Developer passionate about building scalable, secure, and modern web applications using Spring Boot and advanced backend technologies.

---

# ⭐ Support

If you found this project useful, consider giving it a ⭐ on GitHub.
