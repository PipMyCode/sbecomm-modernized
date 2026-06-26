# 🚀 Modernized E-Commerce Platform

![Java](https://img.shields.io/badge/Java-26-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1+-brightgreen?style=for-the-badge&logo=spring-boot)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.0.0--M2-6db33f?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)
![Architecture](https://img.shields.io/badge/Architecture-Modular_Monolith-purple?style=for-the-badge)

An enterprise-grade, highly scalable E-Commerce backend built from the ground up using **Spring Boot 4.1** and **Java 26**. 

This project serves as a technical showcase for modern backend engineering, emphasizing **Domain-Driven Design (DDD)**, high-performance concurrency, **Agentic AI** integration, Zero-Trust security, and robust DevSecOps practices.

---

## 🏗 System Architecture

```mermaid
graph TD
    User([Authenticated User]) -->|HTTPS / REST| API[Spring Boot 4.1 Modular Monolith]
    
    subgraph Core E-Commerce Modules
        API --> Order[📦 Order Context]
        API --> Cart[🛒 Cart Context]
        API --> Catalog[🏷 Catalog Context]
        API --> Promo[🎟 Promotion Context]
        
        Order -.->|Uses Port| Catalog
        Order -.->|Uses Port| Promo
        Order -.->|Uses Port| Cart
    end
    
    subgraph Agentic AI Support Bot
        API --> Chatbot[🤖 AI Chatbot Service]
        Chatbot <-->|Tool Call| Order
        Chatbot <-->|Retrieval| VectorStore[(SimpleVectorStore)]
        Chatbot <-->|Memory| ChatMemory[(InMemoryChatMemory)]
    end
    
    subgraph AI Models & Processing
        Chatbot <-->|REST| LLM[Local LLM - Gemma via LM Studio]
        VectorStore <-->|ONNX Embeddings| EmbedModel[Local Transformers]
    end
    
    subgraph Infrastructure
        DB[(PostgreSQL 17)]
        IAM[Keycloak IAM]
        RabbitMQ[RabbitMQ Message Broker]
        Redis[(Redis Cache)]
    end
    
    Core E-Commerce Modules -->|JDBC / JPA| DB
    Core E-Commerce Modules -->|OAuth2 / JWT| IAM
    Core E-Commerce Modules -->|AMQP| RabbitMQ
    Cart -->|State| Redis
```

---

## 🌟 Key Features & Architectural Decisions

### 1. Modular Monolith & Hexagonal Architecture
We deliberately chose a **Modular Monolith** over a distributed Microservices architecture to eliminate network latency and distributed transactions (Sagas) during early-stage scaling.
- **Deep Decoupling**: Bounded Contexts (`Order`, `Catalog`, `Cart`) communicate exclusively through interfaces (`UseCases`). Any module can be extracted into a standalone microservice simply by swapping its Adapter.
- **Rich Domain Models**: Core business rules are encapsulated in pure Java objects rather than Anemic Data Models attached to ORM frameworks. This ensures the domain is entirely agnostic of Spring Boot or JPA, allowing for sub-millisecond, frameworkless unit testing.

### 2. High-Performance Concurrency (Java 26)
To handle massive throughput (e.g., Black Friday sales), the system employs advanced concurrency patterns:
- **Structured Concurrency**: We utilize Java 26's `StructuredTaskScope` for massive scatter-gather read operations (e.g., fetching product details across multiple downstream services simultaneously).
- **Strict Transactional Boundaries**: We explicitly **avoid** using virtual threads or structured concurrency within write-heavy `@Transactional` boundaries (like Order Checkout) to ensure Spring's `ThreadLocal` transaction contexts remain intact and ACID guarantees are strictly enforced. Operations like inventory reservation and promotion consumption execute sequentially on the primary carrier thread within the database transaction.
- **Deadlock Prevention**: In the `Catalog` module, pessimistic locking is used during checkout inventory reservation. To mathematically prevent distributed deadlocks under heavy concurrent load, the system sorts all requested Product IDs alphabetically before acquiring row-level database locks (`SELECT ... FOR UPDATE`).
- **Thread Pinning Mitigation**: Jackson's `ObjectMapper` is explicitly configured within an immutable Bean layer to guarantee thread-safe serialization during asynchronous payload processing, preventing Virtual Thread pinning anomalies.

### 3. Agentic AI & RAG Chatbot
Powered by **Spring AI**, the API exposes an intelligent, privacy-first Customer Support Agent:
- **Local-First Processing**: Uses a local **Gemma** model via LM Studio and offline ONNX vector embeddings. No sensitive user data leaves the internal network.
- **Retrieval-Augmented Generation (RAG)**: The agent is augmented with store policies, dynamically intercepting queries via a `QuestionAnswerAdvisor`.
- **Conversational Memory**: Automatically remembers session context across HTTP requests using `InMemoryChatMemory`.
- **Strict BOLA Protection**: The LLM executes function calls (e.g., checking order status) via dynamically generated Java closures. The authenticated JWT user ID is bound *before* the prompt reaches the LLM, mathematically eliminating BOLA (Broken Object Level Authorization) vulnerabilities and Virtual Thread `ThreadLocal` context drops.

### 4. Zero-Trust Enterprise Security
- **Keycloak Integration**: Authentication is entirely offloaded to Keycloak (Identity and Access Management). The Spring Boot application acts as an OAuth2 Resource Server, cryptographically verifying JWTs on every request.
- **Role-Based Access Control (RBAC)**: Secure endpoints use method-level security (`@PreAuthorize`) mapped to Keycloak roles.

### 5. DevSecOps & Containerization
- **Hardened Docker Containers**: Runs as a non-root user, drops all Linux Kernel capabilities (`cap_drop: ALL`), and enforces a Read-Only root filesystem.
- **CI/CD Pipeline**: GitHub Actions workflows feature parallelized jobs for testing and aggressive Docker Buildx layer caching. Code quality is enforced via **Qodana**.
- **Observability**: Built-in production monitoring using the **TICK/Prometheus** stack with `/actuator/prometheus` streaming real-time JVM, HTTP, and database metrics.

---

## 💻 Tech Stack

| Category | Technologies |
|---|---|
| **Core** | Java 26, Spring Boot 4.1 |
| **Architecture** | Domain-Driven Design (DDD), Hexagonal Architecture |
| **AI** | Spring AI 1.0.0-M2, Local Gemma LLM (LM Studio), ONNX |
| **Database & Cache** | PostgreSQL 17, Redis |
| **Messaging** | RabbitMQ (Transactional Outbox Pattern) |
| **Security** | Keycloak, OAuth2, JWT Resource Server |
| **DevOps** | Docker, Docker Compose, GitHub Actions, Qodana |
| **Observability** | Prometheus, Grafana, Micrometer Tracing |

---

## 🚀 Quick Start (Local Development)

### Prerequisites
- Docker & Docker Compose
- Java 26 (If running outside of Docker)
- *Optional: LM Studio (running locally on port `1234` for AI features)*

### Running the Infrastructure
Because the entire infrastructure is containerized, you can spin up the API, PostgreSQL Database, Keycloak Server, RabbitMQ, Redis, Prometheus, and Grafana with a single command:

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/modernized-ecomm.git
cd modernized-ecomm

# 2. Build and start the infrastructure
docker-compose up --build -d
```

### Accessing the Services
- **E-Commerce API**: `http://localhost:8080`
- **Keycloak Admin Console**: `http://localhost:8081` (Login: `admin` / `secure_admin_pass_123`)
- **Grafana Dashboards**: `http://localhost:3000` (Login: `admin` / `secure_grafana_pass_123`)
- **Prometheus UI**: `http://localhost:9090`
- **RabbitMQ Management**: `http://localhost:15672`

*(Note: Secrets are managed via a `.env` file for local development).*

---

## 🧪 Testing

The core domain logic is framework-agnostic, allowing for sub-millisecond execution of unit tests.

```bash
# Run all Unit tests
./mvnw clean test

# Run Integration tests (Requires Docker for Testcontainers)
./mvnw verify
```

---

*Built with ❤️ focusing on clean code, testability, and modern enterprise standards.*
