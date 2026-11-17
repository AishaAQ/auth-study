# 🔐 Authentication System

A proof-of-concept project focused on designing and implementing an authentication system. The application is built using Next.js and Spring Boot. 

## ⚙️ Tech Stack

- `Next.js`
- `React`
- `TypeScript`
- `Java`
- `Spring Boot`

## ✨ Features (In Progress)

### Implemented
- Email and password authentication with stateful session management
- Argon2id password hashing and secure session tokens
- Individual session revocation and logout across all devices
- Password changes with session invalidation
- Email verification with token expiration and rate limiting
- Role-based access control
- Integration tests for authentication, session management, and authorization

### Planned
- Complete frontend implementation and integration with the backend
- OAuth authentication flow
- Two-factor authentication (2FA)
- Password recovery
- Additional integration tests and security testing
 
## 📍 Motivation

In my previous projects, I did not dive deep into auth systems as they would either be handled by third party services like Firebase or they were not a primary focus. So, I wanted to build this project to understand the behind the scenes of an auth system while also learning about how these systems could be compromised and potential vulnerabilities.

<!---
## 🔐 Security Considerations
- Secure password hashing (Argon2id)
- Session expiration and renewal
- CSRF protection 
-->

## ▶️ Running the Project

This project consists of a frontend and backend that need to be run separately.

### Backend (Spring Boot)

1. Navigate to the `backend/auth` folder
```bash
cd backend/auth
```
2. Run the Spring Boot Application
```bash
./mvnw spring-boot:run
```
3. The backend server will run on `http://localhost:8080`

### Frontend (Next.js)

1. Navigate to the `frontend` folder
```bash
cd frontend
```
2. Install dependencies:
```bash
npm install
```
4. Run development server:
```bash
npm run dev
```
6. Open `http://localhost:3000` in your browser

## 📚 References
- https://thecopenhagenbook.com/
