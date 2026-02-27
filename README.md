# 🌸 Blooms - Enterprise Blog Management System (Backend REST API)

Welcome to the backend repository of **Blooms**! This is a robust, highly scalable, and fully containerized RESTful API built using **Java 21** and **Spring Boot 3.3.0**. 

It serves as the powerful engine behind a comprehensive Blog Management System and Social Networking platform. This project demonstrates enterprise-level architecture, real-time communication, AI integration, caching mechanisms, and secure authorization.

---

## 🚀 Key Features

### 🔐 1. Advanced Security & Authentication
* **JWT (JSON Web Tokens):** Secure stateless authentication mechanism.
* **Role-Based Access Control (RBAC):** Strict separation of `USER` and `ADMIN` privileges across endpoints.
* **Password Security:** Passwords are cryptographically secured using **BCryptPasswordEncoder**.
* **Forgot Password Flow:** Automated OTP generation and verification handled via the **Brevo Email API** (using `RestTemplate`).

### 📝 2. Core Blogging & Content Engine
* **Hierarchical Data:** Manage Categories, Subcategories, and Blogs with proper MongoDB relations.
* **Optimized Queries (Pagination):** Implemented Spring Data `Pageable` to return paginated lists, preventing memory leaks on large datasets.
* **Content Moderation:** Admin portal to approve, review, or reject blogs seamlessly.

### 🤝 3. Social Interactions & Networking
* **Network Building:** A complete Follow/Unfollow connection system.
* **Dynamic Analytics:** Optimized logic to fetch real-time follower/following counts directly via MongoDB aggregate counts.
* **User Engagement:** APIs to handle Liking and Commenting on published blogs.

### 💬 4. Real-Time 1-to-1 Chat (WebSockets)
* **Instant Messaging:** Built using Spring WebSockets and STOMP messaging protocol.
* **Private Chat Rooms:** Secure `1-to-1` communication channels (`/topic/messages/{userId}`).
* **Chat History & Inbox:** Persistent chat history stored in MongoDB, with a dynamic algorithm to fetch the "Inbox" (latest conversations with unique users).

### 🤖 5. AI Integration (Google Gemini)
* **Automated Blog Generation:** Users provide a title and category, and the AI automatically generates a well-structured blog using the Gemini API.
* **Content Summarization:** AI reads existing blogs and provides a short summary along with constructive feedback for improvement.

### ⚡ 6. High-Performance Caching (Redis)
* **In-Memory Caching:** Heavy GET requests (fetching user profiles, blogs, and categories) are cached using **Redis** to reduce database load and ensure blazing-fast response times.
* **Smart Cache Eviction:** Automated cache clearing (`@CacheEvict`) upon data updates ensures users always see real-time, accurate data.

---

## 🛠️ Tech Stack & Tools

* **Core Language:** Java 21
* **Framework:** Spring Boot 3.3.0
* **Database:** MongoDB (Atlas)
* **Caching:** Redis Cloud / Local Redis Container
* **Real-time Engine:** Spring WebSockets (STOMP)
* **AI Provider:** Google Gemini API (via Spring AI)
* **Email Service:** Brevo API 
* **API Documentation:** Swagger UI / OpenAPI 3.0
* **DevOps:** Docker, Docker Compose, GitHub Actions (CI/CD Pipeline)

---

## ☁️ Cloud Deployment & CI/CD
This backend is built for the cloud and is fully automated:
1. **GitHub Actions:** Every push to the `master` branch triggers a CI/CD pipeline that automatically tests and compiles the code using Maven.
2. **Dockerized:** The application runs inside an isolated Docker container.
3. **Auto-Deploy:** Successfully built images are automatically deployed to **Render Cloud**.

---

## ⚙️ How to Run Locally (Developer Guide)

Follow these steps to run the backend on your local machine:

**1. Start Redis using Docker:**
```bash
docker run --name blooms-redis -p 6379:6379 -d redis
2. Setup Environment Variables:
Configure the following variables in src/main/resources/application.properties or your system environment:

Properties
MONGO_URI=mongodb+srv://<username>:<password>@cluster.mongodb.net/blooms_db
EMAIL_USERNAME=your_email@gmail.com
BREVO_API_KEY=your_brevo_api_key
GEMINI_API_KEY=your_google_gemini_api_key
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_SSL=false
3. Run the Spring Boot Application:

Bash
mvn spring-boot:run
4. Access Swagger UI:
Once the app starts on port 8081, visit:
👉 http://localhost:8081/swagger-ui/index.html

(You can use the Swagger interface to Register, Login, get your Bearer Token, and test all secured APIs!)

Architected and Developed by Vishal Kumar.
