# Modernized E-Commerce API

![Java](https://img.shields.io/badge/Java-26_Preview-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0+-brightgreen?style=for-the-badge&logo=spring-boot)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.0.0--M2-6db33f?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)
![Architecture](https://img.shields.io/badge/Architecture-Modular_Monolith-purple?style=for-the-badge)

An enterprise-grade, highly scalable e-commerce backend built with **Spring Boot 4.0** and **Java 26**. This project serves as a showcase for modern backend engineering, emphasizing clean architecture, high-performance concurrency, Agentic AI, strict security, and robust DevOps practices.

👉 **[Read the full Architecture & Design Decisions Document (ARCHITECTURE.md)](ARCHITECTURE.md)**

---

## Architecture

### System Overview
```mermaid
graph TD
    subgraph DevOps Pipeline
        GH[GitHub Actions] --> |CI/CD Build & Test| Docker
    end

    subgraph Container Infrastructure
        direction TB
        Docker((Docker Engine))
        
        subgraph Spring Boot Modular Monolith
            API[Web Layer / Controllers]
            
            subgraph Bounded Contexts
                Order[Order Module]
                Cart[Cart Module]
                Catalog[Catalog Module]
                Promo[Promotion Module]
            end
            
            API --> Order
            API --> Cart
            API --> Catalog
            API --> Promo
            
            Order -.-> |Uses Port| Catalog
            Order -.-> |Uses Port| Promo
        end
        
        Docker --> API
    end
    
    subgraph Infrastructure Services
        DB[(PostgreSQL 17)]
        IAM[Keycloak IAM]
        PROM[Prometheus]
        GRAF[Grafana]
    end
    
    API --> |JDBC / JPA| DB
    API --> |OAuth2 / JWT| IAM
    PROM --> |Scrapes /actuator| API
    GRAF --> |Reads Metrics| PROM
```

This application strictly adheres to **Domain-Driven Design (DDD)** and **Hexagonal Architecture (Ports & Adapters)**. 

### Why a Modular Monolith?
Microservices often introduce unnecessary complexity (network latency, distributed transactions) for early-stage applications. This project implements a **Modular Monolith**—the application deploys as a single Spring Boot artifact, but the internal code is strictly segregated into independent Bounded Contexts (`Order`, `Cart`, `Catalog`, `Promotion`).

- **Separation of Concerns**: Modules do not share databases or directly invoke each other's `@Service` classes. They communicate strictly through interfaces (`UseCases`), ensuring loose coupling.
- **Microservice-Ready**: If business requirements dictate that the `Catalog` module must scale independently, it can be seamlessly extracted into a dedicated microservice by simply swapping its Adapter, without altering the core Domain logic.
- **Rich Domain Model**: Business rules (e.g., Cart subtotal calculations, Promotion validations) are encapsulated in pure Java objects (`Cart.java`) rather than Anemic Domain Models (`@Entity` classes), making testing lightning fast and independent of the Spring framework.

### Project Structure
The codebase strictly adheres to Hexagonal Architecture, isolating Domain Models from Infrastructure frameworks.

```text
src/main/java/com/sbecomm/modernized/
├── common/             # Cross-cutting concerns (Security, Exceptions, Configs, Logging)
├── cart/               # Bounded Context: Shopping Cart
├── catalog/            # Bounded Context: Product Catalog
├── order/              # Bounded Context: Order Processing
├── payment/            # Bounded Context: Payment Gateway
├── promotion/          # Bounded Context: Discount Codes
└── user/               # Bounded Context: User Management & Authentication
```

**Inside Every Bounded Context:**
```text
├── application/    # Services & UseCases (Ports / Inbound & Outbound Interfaces)
│   ├── dto/        # Request/Response Data Transfer Objects
│   ├── port/       # Interfaces defining what the module needs to do
│   └── service/    # Application Logic orchestration
├── domain/         # Pure Java Rich Domain Models & Core Business Rules
│   ├── exception/  # Domain-specific exceptions
│   ├── model/      # Aggregates and Entities (Zero Spring/JPA dependencies)
│   └── repository/ # Interfaces for data storage (Outbound Ports)
├── infrastructure/ # Framework-specific Adapters (Database, external APIs)
│   ├── adapter/    # Implementations of Domain Repositories (JPA, Stripe API)
│   └── entity/     # JPA @Entity classes mapping to PostgreSQL tables
└── presentation/   # Web Layer Adapters
    └── rest/       # Spring MVC @RestControllers
```

---

## Agentic AI & RAG Chatbot

Powered by **Spring AI 1.0.0-M2**, the API exposes an intelligent, privacy-first Customer Support Agent:
- **Local-First Processing**: Uses a local **Gemma** model via LM Studio and offline ONNX vector embeddings. No sensitive user data leaves the internal network.
- **Retrieval-Augmented Generation**: The agent is augmented with store policies, dynamically intercepting queries via a `QuestionAnswerAdvisor`.
- **Conversational Memory**: Automatically remembers session context using `InMemoryChatMemory`.
- **Strict BOLA Protection**: The LLM executes function calls (e.g., checking order status) via dynamically generated Java closures. The authenticated JWT user ID is bound *before* the prompt reaches the LLM, mathematically eliminating BOLA vulnerabilities and Virtual Thread `ThreadLocal` drops.

---

## Enterprise Security & IAM

- **Keycloak Integration**: Authentication is entirely offloaded to **Keycloak** (Identity and Access Management). The Spring Boot application acts as an OAuth2 Resource Server, cryptographically verifying JWTs on every request.
- **Role-Based Access Control (RBAC)**: Secure endpoints use method-level security (e.g., `@PreAuthorize`) mapped to Keycloak roles.

---

## DevOps & Containerization

The infrastructure is hardened for production and adheres to strict DevSecOps principles:

- **Immutable & Hardened Docker Containers**:
  - Runs as a non-root `spring` user to prevent privilege escalation.
  - Drops all Linux Kernel capabilities (`cap_drop: ALL`).
  - Enforces a Read-Only root filesystem to prevent malware downloads (`read_only: true` with `/tmp` mounted as `tmpfs`).
- **Resource Management**: Strict CPU and Memory limits prevent container starvation (`deploy.resources.limits`).
- **Advanced CI/CD Pipeline (GitHub Actions)**:
  - Parallelized jobs for `test` and `security-scan`.
  - Automated vulnerability scanning using **Trivy**.
  - Aggressive Docker Buildx layer caching.
  - Automated secure push to the GitHub Container Registry (GHCR).

---

## Observability & Monitoring

Built-in production monitoring using the **TICK/Prometheus** stack:
- **Spring Boot Actuator**: Exposes a `/actuator/prometheus` endpoint streaming real-time JVM, HTTP, and database metrics.
- **Prometheus**: Time-series database actively scraping the API.
- **Grafana**: Beautiful, real-time visualization dashboards.

---

## Quick Start (Local Development)

### Prerequisites
- Docker & Docker Compose
- Java 25 (If running outside of Docker)

### Running the Application

Because the entire infrastructure is containerized, you can spin up the API, PostgreSQL Database, Keycloak Server, Prometheus, and Grafana with a single command:

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

*(Note: Secrets are managed via a `.env` file for local development).*

---

## Testing

The core domain logic is framework-agnostic, allowing for sub-millisecond execution of unit tests.

```bash
# Run all Unit and Integration tests
./mvnw clean test
```

---

*Built with ❤️ focusing on clean code, testability, and modern enterprise standards.*
