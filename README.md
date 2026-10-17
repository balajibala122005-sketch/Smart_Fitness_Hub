# Gym Hub — Smart Fitness Hub

A full-stack fitness and lifestyle management platform featuring a high-performance Spring Boot REST API backend and a dark-neon cyberpunk-themed frontend.

---

## 🚀 Overview

Gym Hub provides athletes and fitness enthusiasts with tools to authenticate, track workouts, record biometrics, generate personalized workout routines, and log daily nutrition and macronutrients.

### Technology Stack
- **Frontend**: HTML5, Vanilla CSS3 (Custom Dark-Neon Theme with Glassmorphism), Modern JavaScript (ES6+), Chart.js
- **Backend**: Java 21, Spring Boot 4.x, Spring Data JPA, Spring Security (Stateless JWT)
- **Database**: MySQL (via XAMPP) for development; in-memory H2 (MySQL Mode) for automated testing
- **Build & Test**: Apache Maven (`mvnw`), JUnit 5, Spring Boot Test, MockMvc

---

## 📋 Implemented Phases & Features

### Phase 1 — Backend Foundation
- Spring Boot architecture with package `com.example.gymhub`.
- MySQL database connectivity with automated schema updates.
- Centralized security filter chain with CORS configuration and public `/api/health` monitoring endpoint.

### Phase 2 — Authentication & User Management
- Secure user registration and login endpoints (`/api/auth/register`, `/api/auth/login`).
- BCrypt password hashing for credentials security.
- Stateless HMAC-SHA256 JWT token generation and validation filter.
- Protected user profile endpoint (`/api/user/profile`).

### Phase 3 — User Command Dashboard
- Dynamic authenticated user profile display.
- Biometric calculations (BMI, height, weight, activity levels).
- Comprehensive dashboard summary API (`/api/user/dashboard-summary`).
- Unified frontend API client (`GymApi`) in `script.js` with automatic JWT bearer token attachment.

### Phase 4 — Workout Logging & Progress Tracking
- Workout logging with exercise type, duration, sets, reps, weight, calories burned, and date (`/api/progress/workouts`).
- Workout statistics endpoint (`/api/progress/workouts/stats`).
- Body measurement history and dynamic progress charts with Chart.js (`/api/progress/measurements`).
- Rule-based workout routine generator and saved active workout plan selection (`/api/workouts/plan`).

### Phase 5 — Diet & Nutrition Management
- Meal and nutrition logging with meal type (Breakfast, Lunch, Dinner, Snack), calories, protein, carbohydrates, fats, and notes (`/api/nutrition/meals`).
- Filter meals by date with user-isolated access controls.
- Safe meal record deletion (`DELETE /api/nutrition/meals/{id}`) with strict ownership verification.
- Daily macronutrient and calorie summary endpoint (`/api/nutrition/summary?date=YYYY-MM-DD`).
- Real-time dashboard integration displaying today's calories consumed alongside workouts and active rank protocol.

---

## 🔒 Security & Data Isolation
- Strict user data isolation: all endpoints resolve the user exclusively via the authenticated JWT principal (`authentication.getName()`). Client-supplied user IDs are never trusted.
- Input validation on all DTOs (`@NotNull`, `@Size`, `@Min`, `@Max`, `@PastOrPresent`).
- Configurable environment variables for credentials and secrets (`.env.example`).

---

## 🛠️ Getting Started

### Prerequisites
- Java 21+ JDK
- MySQL (e.g. running via XAMPP on port 3306)
- Git

### Backend Setup
1. Navigate to the backend directory:
   ```bash
   cd backend/GymHub
   ```
2. (Optional) Configure environment variables in `.env` based on `.env.example`:
   ```bash
   SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/gym_hub?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   SPRING_DATASOURCE_USERNAME=root
   SPRING_DATASOURCE_PASSWORD=
   JWT_SECRET=YourSecure256BitSecretKeyHere
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   The backend API will start on `http://localhost:8080`.

### Running Tests
Execute the comprehensive test suite (all 30 tests):
```bash
cd backend/GymHub
./mvnw clean test
```

### Frontend Setup
Open `frontend/gym/index.html` in your browser or serve it via a local static HTTP server (e.g., Live Server or python `-m http.server 3000`).

---

## 📡 Key API Endpoints

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/health` | Service health status | No |
| `POST` | `/api/auth/register` | Register new user account | No |
| `POST` | `/api/auth/login` | Login and obtain JWT token | No |
| `GET` | `/api/user/profile` | Get current user profile | Yes |
| `GET` | `/api/user/dashboard-summary` | Aggregated user stats & biometrics | Yes |
| `POST` | `/api/progress/workouts` | Log a workout session | Yes |
| `GET` | `/api/progress/workouts` | Retrieve workout history | Yes |
| `GET` | `/api/progress/workouts/stats` | Retrieve total workouts & stats | Yes |
| `POST` | `/api/progress/measurements` | Record body measurements | Yes |
| `GET` | `/api/progress/measurements` | Get measurement history | Yes |
| `POST` | `/api/workouts/plan` | Select active workout plan | Yes |
| `GET` | `/api/workouts/plan` | Get active workout plan | Yes |
| `POST` | `/api/nutrition/meals` | Record a meal log | Yes |
| `GET` | `/api/nutrition/meals` | Get meals (optional `?date=YYYY-MM-DD`) | Yes |
| `DELETE`| `/api/nutrition/meals/{id}` | Delete a meal (owner only) | Yes |
| `GET` | `/api/nutrition/summary` | Daily nutrition & macro totals | Yes |
