# Microservices Architecture with Kafka, Nginx, and Java 21

[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-7.5.0-red?logo=apache-kafka)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-29.6.1-blue?logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)



Mrkdown# Microservices Architecture with Kafka, Nginx, and Java 21

## Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Technology Stack](#technology-stack)
4. [Setup Instructions](#setup-instructions)
5. [API Endpoints](#api-endpoints)
6. [Kafka Event Flow](#kafka-event-flow)
7. [Docker Compose Setup](#docker-compose-setup)
8. [Running the Project](#running-the-project)
9. [Testing](#testing)
10. [Troubleshooting](#troubleshooting)
12. [Key Features](#key-features)

---

## Project Overview

This is a **production-ready microservices architecture** built with:
- **Java 21** (Latest LTS version with modern features)
- **Spring Boot 3.2** (Latest stable version)
- **Two Independent Microservices** (Order Service & Inventory Service)
- **Apache Kafka** (Event-driven asynchronous communication)
- **Nginx** (Reverse proxy & API Gateway)
- **PostgreSQL** (Persistent data storage)
- **Docker & Docker Compose** (Containerization and orchestration)

### Problem Solved
Modern applications require:
- ✅ **Scalability** - Services scale independently
- ✅ **Resilience** - Failure isolation between services
- ✅ **Asynchronous Communication** - Via Kafka events
- ✅ **Load Balancing** - Through Nginx gateway
- ✅ **Containerization** - Easy deployment with Docker

This project demonstrates all these concepts in a real-world scenario.

---

## System Architecture

### High-Level Diagram
┌─────────────────────────────────────────────────────────────┐
│                         CLIENT                              │
│                   (Browser/API Client)                      │
└────────────────────────┬────────────────────────────────────┘
│
│ HTTP Requests
▼
┌─────────────┐
│   NGINX     │  Port 80
│  Gateway    │  (Reverse Proxy & Load Balancer)
└──────┬──────┘
│
┌──────────────────┼──────────────────┐
│                  │                  │
│                  │                  │
┌────▼────┐        ┌────▼──────┐    ┌─────▼──────┐
│  Order  │        │ Inventory │    │ Prometheus │
│ Service │        │  Service  │    │  Metrics   │
│ :8081   │        │  :8082    │    │  :9090     │
└────┬────┘        └────┬──────┘    └────────────┘
│                  │
│   ┌──────────────┘
│   │
│   │  Service-to-Service
│   │  Communication (REST)
│   │
└───┴─────────────────────┐
│
┌────────▼────────┐
│     KAFKA       │
│  Message Broker │
│     :9092       │
│                 │
│ order-created   │
│ topic           │
└────┬────────────┘
│
│ Events
▼
┌──────────────────────┐
│   Shared Database    │
│    PostgreSQL        │
│     :5432            │
│                      │
│  order_db            │
│  inventory_db        │
└──────────────────────┘

### Workflow: Creating an Order

Client sends: POST /orders { productId: 1, quantity: 5 }
│
▼
Nginx routes to Order Service (Port 8081)
│
▼
Order Service validates request
│
▼
Calls Inventory Service via Feign (REST with Circuit Breaker)

Checks stock availability
Reserves stock if available
│
▼


Creates Order in PostgreSQL (PENDING status)
│
▼
Publishes Event to Kafka: "OrderCreated"
{ orderNumber, productId, quantity, totalPrice, timestamp }
│
▼
Returns 201 Created with Order details to Client
│
▼
Inventory Service consumes Kafka event

Listens on "order-created-topic"
Updates internal statistics
Can trigger shipment, notifications, etc.




---

## Technology Stack

### Core Technologies
| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 21 (LTS) | Language with latest features (records, sealed interfaces) |
| Spring Boot | 3.2.0 | Application framework |
| Spring Cloud | 2023.0.0 | Service discovery, load balancing |
| Spring Data JPA | 3.2.0 | ORM with Hibernate |
| Spring Kafka | 3.1.0 | Kafka integration |
| PostgreSQL | 16 | Relational database |
| Kafka | 7.5.0 | Distributed event streaming |
| Zookeeper | 7.5.0 | Kafka coordination |
| Nginx | latest | Reverse proxy & gateway |
| Docker | 29.6.1 | Containerization |
| Docker Compose | 5.3.0 | Orchestration |

### Key Libraries
- **Lombok** - Reduces boilerplate code
- **Resilience4j** - Circuit breaker pattern
- **OpenFeign** - Declarative HTTP client
- **Micrometer** - Metrics collection
- **Prometheus** - Monitoring & alerting
- **Validation API** - Input validation


## Setup Instructions

### Prerequisites
1. **Docker Desktop** (v29.6.1+) - [Download](https://www.docker.com/products/docker-desktop)
2. **Java 21 JDK** - [Download](https://www.oracle.com/java/technologies/downloads/)
3. **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
4. **Git** - [Download](https://git-scm.com/)

### Step 1: Install Docker Desktop

1. Download and install Docker Desktop
2. Start Docker Desktop from Start menu
3. Wait for Docker icon to appear in system tray
4. Verify installation:
```powershell
docker --version
docker compose --version
```

### Step 2: Create Project Structure

```powershell
cd D:\jAVA
mkdir microservices-project
cd microservices-project
```

Create these files in the root directory:
- `docker-compose.yml`
- `nginx.conf`
- `init-db.sql`
- `prometheus.yml`

### Step 3: Start Docker Infrastructure

```powershell
cd D:\jAVA\microservices-project
docker compose up -d
```

Verify all services are running:
```powershell
docker compose ps
```

Expected output:
NAME              STATUS
postgres_db       Up (healthy)
zookeeper         Up
kafka             Up
nginx_gateway     Up
prometheus        Up

### Step 4: Run Spring Boot Applications

Open two separate PowerShell/CMD terminals:

**Terminal 1 - Order Service:**
```powershell
cd D:\jAVA\microservices-project\order-service
mvn clean install
mvn spring-boot:run
```

**Terminal 2 - Inventory Service:**
```powershell
cd D:\jAVA\microservices-project\inventory-service
mvn clean install
mvn spring-boot:run
```

Both should start in ~30 seconds with message:
Started OrderServiceApplication in X.XXX seconds
Started InventoryServiceApplication in X.XXX seconds

---

## API Endpoints

### Base URL: `http://localhost` (via Nginx)

### Order Service Endpoints

#### 1. Create Order
POST /orders
Content-Type: application/json
Request Body:
{
"productId": 1,
"quantity": 5
}
Response:
{
"id": 1,
"orderNumber": "ORD-1705318245000-a1b2c3d4",
"productId": 1,
"quantity": 5,
"totalPrice": 500,
"status": "PENDING",
"createdAt": "2026-07-15T13:00:00",
"updatedAt": "2026-07-15T13:00:00"
}

#### 2. Get Order by ID
GET /orders/{id}
Response: Order object (same as above)

#### 3. Get Orders by Product
GET /orders/product/{productId}
Response: Array of Order objects

#### 4. Update Order Status
PATCH /orders/{id}/status?status=CONFIRMED
Status options: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
Response: Updated Order object

#### 5. Delete Order
DELETE /orders/{id}
Response: 204 No Content

#### 6. Health Check
GET /orders/health
Response: "Order Service is running"

### Inventory Service Endpoints

#### 1. Create Inventory
POST /inventory
Content-Type: application/json
Request Body:
{
"productId": 1,
"productName": "Laptop",
"totalQuantity": 100
}
Response:
{
"id": 1,
"productId": 1,
"productName": "Laptop",
"totalQuantity": 100,
"availableQuantity": 100,
"reservedQuantity": 0,
"createdAt": "2026-07-15T13:00:00",
"updatedAt": "2026-07-15T13:00:00"
}

#### 2. Get Inventory
GET /inventory/{productId}
Response: Inventory object (same as above)

#### 3. Check Stock
GET /inventory/{productId}/check?requiredQuantity=5
Response:
{
"productId": 1,
"availableQuantity": 100,
"sufficient": true
}

#### 4. Reserve Stock
POST /inventory/{productId}/reserve?quantity=5
Response: Updated Inventory object

#### 5. Release Stock
POST /inventory/{productId}/release?quantity=5
Response: Updated Inventory object

#### 6. Update Inventory
PUT /inventory/{productId}
Content-Type: application/json
Request Body:
{
"totalQuantity": 150
}
Response: Updated Inventory object

#### 7. Get All Inventories
GET /inventory
Response: Array of Inventory objects

#### 8. Health Check
GET /inventory/health
Response: "Inventory Service is running"

---

## Kafka Event Flow

### Event Topic: `order-created-topic`

### Event Schema
```json
{
  "orderNumber": "ORD-1705318245000-a1b2c3d4",
  "productId": 1,
  "quantity": 5,
  "totalPrice": 500,
  "timestamp": 1705318245000
}
```

### Producer: Order Service
**File:** `OrderEventProducer.java`

When an order is created:
1. Order is persisted to PostgreSQL
2. Event is converted to JSON string
3. Published to Kafka topic
4. Message key: `orderNumber`
5. Message value: JSON event

### Consumer: Inventory Service
**File:** `OrderEventConsumer.java`

Listens on `order-created-topic`:
1. Receives JSON string from Kafka
2. Deserializes to `OrderCreatedEvent` object
3. Processes business logic
4. Can trigger:
   - Send shipment notification
   - Update internal statistics
   - Trigger fulfillment process
   - Send customer notification

### Why Kafka?
- **Decoupling**: Services don't directly call each other
- **Asynchronous**: Non-blocking communication
- **Scalability**: Can handle thousands of events
- **Durability**: Events persist until consumed
- **Flexibility**: Add new consumers without changing producer

---

## Docker Compose Setup

### Services

#### PostgreSQL (Port 5432)
- **Image:** `postgres:16-alpine`
- **Databases:** `order_db`, `inventory_db`
- **Username:** postgres
- **Password:** postgres
- **Purpose:** Persistent data storage
- **Volumes:** Database data persists across restarts

#### Zookeeper (Port 2181)
- **Image:** `confluentinc/cp-zookeeper:7.5.0`
- **Purpose:** Kafka coordination and configuration
- **Required by:** Kafka broker

#### Kafka (Port 9092)
- **Image:** `confluentinc/cp-kafka:7.5.0`
- **Purpose:** Event streaming platform
- **Auto-creates topics:** Yes
- **Retention:** 168 hours (7 days)
- **Topics:** Auto-created on first use

#### Nginx (Port 80)
- **Image:** `nginx:latest`
- **Purpose:** Reverse proxy and API gateway
- **Routes:**
  - `/orders/*` → Order Service (8081)
  - `/inventory/*` → Inventory Service (8082)
- **Features:** Load balancing, health checks, timeout management

#### Prometheus (Port 9090)
- **Image:** `prom/prometheus:latest`
- **Purpose:** Metrics collection and monitoring
- **Scrape Interval:** 15 seconds
- **Data Retention:** 15 days (default)
- **Access:** http://localhost:9090

---

## Running the Project

### Complete Startup Flow

```powershell
# 1. Navigate to project
cd D:\jAVA\microservices-project

# 2. Start all Docker services
docker compose up -d

# 3. Wait 30 seconds for services to stabilize
# Check status:
docker compose ps

# 4. Terminal 1 - Start Order Service
cd order-service
mvn spring-boot:run

# 5. Terminal 2 - Start Inventory Service
cd inventory-service
mvn spring-boot:run

# 6. Wait for both to start (~30 seconds each)
# Look for: "Started OrderServiceApplication" and "Started InventoryServiceApplication"
```

### Verify Everything Works

```powershell
# Test Nginx routing
curl http://localhost/orders/health
curl http://localhost/inventory/health

# Check services directly
curl http://localhost:8081/api/orders/health
curl http://localhost:8082/api/inventory/health

# Check Kafka (via Docker)
docker compose exec kafka kafka-topics --list --bootstrap-server localhost:9092

# Check Prometheus
# Open browser: http://localhost:9090
```

---

## Testing

### End-to-End Test

```powershell
# 1. Create Inventory
curl -X POST http://localhost/inventory `
  -H "Content-Type: application/json" `
  -d '{
    "productId": 1,
    "productName": "Laptop Pro",
    "totalQuantity": 100
  }'

# Response should include:
# "id": 1, "totalQuantity": 100, "availableQuantity": 100

# 2. Check Stock
curl "http://localhost/inventory/1/check?requiredQuantity=10"

# Response: "sufficient": true

# 3. Create Order
curl -X POST http://localhost/orders `
  -H "Content-Type: application/json" `
  -d '{
    "productId": 1,
    "quantity": 5
  }'

# Response should include:
# "status": "PENDING", "orderNumber": "ORD-..."

# 4. Verify Inventory Updated
curl http://localhost/inventory/1

# Response: "availableQuantity": 95, "reservedQuantity": 5

# 5. Check Logs
# Order Service should log: "Publishing order created event"
# Inventory Service should log: "Received order event"

# 6. Get Order Details
curl http://localhost/orders/1

# 7. Update Order Status
curl -X PATCH "http://localhost/orders/1/status?status=CONFIRMED"

# 8. Verify Status Changed
curl http://localhost/orders/1
# Response: "status": "CONFIRMED"
```

### Testing with Postman

Import requests:
```json
{
  "info": {
    "name": "Microservices API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Create Inventory",
      "request": {
        "method": "POST",
        "url": "http://localhost/inventory",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"productId\": 1,\n  \"productName\": \"Laptop\",\n  \"totalQuantity\": 100\n}"
        }
      }
    },
    {
      "name": "Create Order",
      "request": {
        "method": "POST",
        "url": "http://localhost/orders",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"productId\": 1,\n  \"quantity\": 5\n}"
        }
      }
    }
  ]
}
```

---

## Troubleshooting

### Issue: Docker containers won't start

**Solution:**
```powershell
# Restart Docker Desktop
# 1. Right-click Docker icon in system tray
# 2. Click "Quit Docker Desktop"
# 3. Wait 10 seconds
# 4. Open Docker Desktop again
# 5. Wait 2-3 minutes for startup
# 6. Run: docker compose up -d
```

### Issue: "Cannot connect to database"

**Solution:**
```powershell
# Check PostgreSQL is healthy
docker compose logs postgres_db

# Restart PostgreSQL
docker compose restart postgres_db

# Wait 10 seconds
docker compose exec postgres_db pg_isready -U postgres
```

### Issue: "Kafka not responding"

**Solution:**
```powershell
# Check Kafka logs
docker compose logs kafka

# Restart Kafka
docker compose down
docker compose up -d kafka zookeeper

# Wait 30 seconds for startup
```

### Issue: Kafka deserialization error

**Solution:** Already fixed in the updated code:
- Use `StringDeserializer` instead of `JsonDeserializer`
- Services communicate via JSON strings
- Solves class not found errors

### Issue: Port already in use

**Solution:**
```powershell
# Find process using port
netstat -ano | findstr :80      # Nginx
netstat -ano | findstr :5432    # PostgreSQL
netstat -ano | findstr :9092    # Kafka

# Kill process
taskkill /PID <PID> /F

# Or stop Docker services first
docker compose down
```

### Issue: Spring Boot app won't start

**Solution:**
```powershell
# Clear Maven cache
mvn clean

# Rebuild
mvn install

# Run with debug
mvn spring-boot:run -X

# Check logs for specific errors
```

---

## Key Features

### 1. Microservices Architecture
- **Independent services** that can be deployed separately
- **Database per service** for data isolation
- **Loose coupling** via events and REST APIs
- **Easy scaling** - scale individual services

### 2. Event-Driven Communication
- **Kafka** for asynchronous messaging
- **Publish-Subscribe** pattern
- **Decoupled** producer and consumers
- **Reliable** - events persist until consumed

### 3. API Gateway (Nginx)
- **Single entry point** for all clients
- **Load balancing** across services
- **Request routing** to appropriate services
- **Reverse proxy** functionality

### 4. Resilience & Fault Tolerance
- **Circuit breaker** (Resilience4j)
- **Timeouts** on service calls
- **Retry logic** for failed requests
- **Graceful degradation** on service failure

### 5. Monitoring & Observability
- **Prometheus** for metrics collection
- **Spring Actuator** endpoints
- **Health checks** for all services
- **Structured logging** with SLF4J

### 6. Modern Java 21 Features
- **Records** for DTOs (immutable data carriers)
- **Sealed interfaces** for type safety
- **Text blocks** for SQL queries
- **Virtual threads** ready (future optimization)

### 7. Database Design
- **Optimistic locking** for concurrent updates
- **Proper indexes** for performance
- **Referential integrity** with constraints
- **Audit fields** (createdAt, updatedAt)

### 8. Error Handling
- **Global exception handler** (centralized)
- **Custom exception** hierarchy
- **Meaningful error messages**
- **Proper HTTP status codes**

---

## Performance Considerations

### Load Testing
- Nginx handles distribution
- Services independently scale
- Kafka handles thousands of events/sec
- PostgreSQL connection pooling (HikariCP)

### Optimization Tips
1. **Database Indexing** - Index frequently queried columns
2. **Caching** - Add Redis for hot data
3. **Async Processing** - Use @Async for long operations
4. **Connection Pooling** - Already configured with HikariCP
5. **Rate Limiting** - Add at Nginx level

### Scalability Path
Current: 1 Order Service + 1 Inventory Service
↓
Horizontal: 3x Order Services + 3x Inventory Services (Nginx load balances)
↓
Vertical: Upgrade service hardware
↓
Advanced: Add Redis caching, implement CQRS pattern

---

## Deployment Recommendations

### For Production

1. **Security**
   - Use HTTPS/TLS
   - Implement JWT authentication
   - Add API rate limiting
   - Secrets management (not hardcoded)

2. **Monitoring**
   - Centralized logging (ELK stack)
   - Distributed tracing (Jaeger)
   - Alert management (PagerDuty)

3. **Infrastructure**
   - Deploy to Kubernetes
   - Use managed databases (AWS RDS)
   - Use managed message brokers (AWS MSK)

4. **CI/CD**
   - GitHub Actions for automated testing
   - Docker image building
   - Automated deployment pipeline

---

## Useful Commands

### Docker Compose
```powershell
# Start services
docker compose up -d

# Stop services
docker compose down

# View logs
docker compose logs -f

# Restart specific service
docker compose restart kafka

# Remove all data
docker compose down -v
```

### Maven
```powershell
# Build project
mvn clean install

# Run tests
mvn test

# Package JAR
mvn package -DskipTests

# Run Spring Boot
mvn spring-boot:run
```

### Curl Testing
```powershell
# GET request
curl http://localhost/orders/health

# POST request
curl -X POST http://localhost/orders `
  -H "Content-Type: application/json" `
  -d '{"productId": 1, "quantity": 5}'

# Update request
curl -X PATCH "http://localhost/orders/1/status?status=CONFIRMED"

# Delete request
curl -X DELETE http://localhost/orders/1
```

---

## Architecture Principles

### SOLID Principles
- **S**ingle Responsibility - Each service has one reason to change
- **O**pen/Closed - Open for extension, closed for modification
- **L**iskov Substitution - Services can be replaced with compatible implementations
- **I**nterface Segregation - Clients depend on specific interfaces
- **D**ependency Inversion - Depend on abstractions, not concretions

### DDD (Domain-Driven Design)
- **Entities** - Order, ProductInventory
- **Value Objects** - OrderStatus, Money (totalPrice)
- **Repositories** - Data access abstraction
- **Services** - Business logic orchestration
- **Events** - OrderCreatedEvent for domain events

### Clean Architecture
┌─────────────────────────────────────────┐
│          Presentation Layer             │
│     (Controller, REST Endpoints)        │
├─────────────────────────────────────────┤
│           Business Logic Layer          │
│      (Service, Use Cases)               │
├─────────────────────────────────────────┤
│        Persistence Layer                │
│    (Repository, ORM, Database)          │
├─────────────────────────────────────────┤
│      Infrastructure Layer               │
│   (Kafka, Nginx, Docker)                │
└─────────────────────────────────────────┘

---

## Conclusion

This project demonstrates a **professional, enterprise-grade microservices architecture** using:

✅ Modern Java and Spring Boot ecosystem
✅ Event-driven asynchronous communication
✅ Containerized deployment with Docker
✅ API Gateway pattern with Nginx
✅ Proper error handling and monitoring
✅ Clean, maintainable code structure
✅ Production-ready patterns and practices

**Next Steps:**
- Deploy to cloud (AWS, GCP, Azure)
- Implement authentication with JWT
- Add comprehensive monitoring and alerting
- Scale horizontally with Kubernetes
- Implement advanced patterns (CQRS, Event Sourcing)

---

## References

- [Spring Boot Documentation](https://docs.spring.io/spring-boot)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Docker Documentation](https://docs.docker.com/)
- [Nginx Documentation](https://nginx.org/en/docs/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)

---

**Author:** Hassnain Ali Khokhar
**Date:** July 2026
**Version:** 1.0.0


🔗 LINKEDIN POST
🎬 "Building microservices is like making The Matrix sequels..." 
Starts simple, looks amazing, but suddenly you have too many services talking to each other and nobody knows what's happening! 😅

I just built a production-ready microservices architecture that actually *makes sense* 🚀

Here's what we shipped:

✨ **The Stack:**
- 2 Independent Microservices (Order + Inventory)
- Apache Kafka for event-driven communication (no direct REST calls causing chaos 🙅)
- Nginx as API Gateway (single entry point FTW)
- Java 21 with modern features (records, sealed classes)
- Docker Compose for local development
- PostgreSQL for data persistence
- Spring Boot 3.2 (the latest stable goodness)

🏗️ **Architecture Highlights:**
→ Services communicate asynchronously via Kafka (loose coupling ✅)
→ Nginx load balances and routes requests (gateway pattern ✅)
→ Each service has its own database (data isolation ✅)
→ Circuit breaker protection (resilience ✅)
→ Prometheus monitoring (observability ✅)

📡 **How It Works:**
1. Client creates order via Nginx gateway
2. Order Service validates & stores order
3. **Event published to Kafka** (Order Created)
4. Inventory Service consumes event asynchronously
5. Both services update their own databases
6. No service directly calls another (decoupled! 🎯)

⚡ **Key Benefits:**
✅ Services scale independently
✅ Failure isolation (one service down ≠ all down)
✅ Easy to add new consumers to Kafka
✅ Better than monolith spaghetti code
✅ Production-ready patterns
✅ Container-ready for K8s deployment

🔧 **Tech Stack Details:**
- Spring Boot 3.2 + Java 21
- Spring Kafka for event streaming
- PostgreSQL databases (order_db + inventory_db)
- Docker Compose v5.3.0 for orchestration
- Nginx as reverse proxy
- Prometheus for monitoring

The entire infrastructure runs locally in Docker, making development frictionless. No more "works on my machine" issues! 🎉

Who else is building event-driven microservices? Drop your architecture in the comments! 

#Microservices #Kafka #Java21 #SpringBoot #Docker #SoftwareArchitecture #EventDriven #Nginx #DistributedSystems #BackendEngineering

---

P.S. - If you're trying this locally, watch out for the Kafka deserialization errors. String-based events > typed JSON in microservices 😉

Alternative Shorter LinkedIn Post (More Casual)
🎬 The Matrix: "What if I told you... you could build microservices that actually work together?" 
*puts on sunglasses* 

Just shipped a complete event-driven microservices setup:

🚀 Order Service → Kafka → Inventory Service (asynchronous vibes ✅)
🌐 Nginx Gateway → Load balancing → Zero coupling
🐘 PostgreSQL → Data isolation → Clean schemas
🐳 Docker Compose → Local dev → Production-ready

The secret sauce? Kafka events instead of direct REST calls. Services don't argue anymore! 😄

Drop a 🔥 if you love clean architecture.

#Microservices #Kafka #Java #SpringBoot #Docker #TechArchitecture

You can copy either the detailed markdown for documentation or the LinkedIn posts to use directly! 🎉issues have already been solved dont add them and i ahve added upto how to run , after that i will add a insomnia json export and i have also added a picture as an exampple inventory-order to show inventory is receiving kafka messagesOrchestrated documentation structure with testing and results sectionsOrchestrated documentation structure with testing and results sections📄 Clean Project Documentation (Continued)
markdown## Testing

### Using Insomnia

Import the provided `insomnia-collection.json` file:

1. Open Insomnia
2. Click **Create** → **File** → **Import**
3.https://github.com/Hktheone/micro-service-example/ also this is the updated projehct structureSynthesized GitHub repository structure to craft comprehensive documentationSynthesized GitHub repository structure to craft comprehensive documentation📖 Complete Project Documentationmarkdown# Microservices Architecture with Kafka, Nginx & Java 21

[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://www.oracle.com/java/technologies/downloads/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-7.5.0-red?logo=apache-kafka)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-29.6.1-blue?logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

A production-ready event-driven microservices architecture demonstrating best practices with Order Service and Inventory Service communicating asynchronously via Apache Kafka.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Project](#running-the-project)
- [API Documentation](#api-documentation)
- [Kafka Event Flow](#kafka-event-flow)
- [Testing with Insomnia](#testing-with-insomnia)
- [Key Features](#key-features)
- [Architecture Decisions](#architecture-decisions)

---

## Project Overview

This is a **production-ready microservices architecture** built with:
- **Java 21** (Latest LTS version with modern features)
- **Spring Boot 3.2** (Latest stable version)
- **Two Independent Microservices** (Order Service & Inventory Service)
- **Apache Kafka** (Event-driven asynchronous communication)
- **Nginx** (Reverse proxy & API Gateway)
- **PostgreSQL** (Persistent data storage)
- **Docker & Docker Compose** (Containerization and orchestration)

### Problem Solved
Modern applications require:
- ✅ **Scalability** - Services scale independently
- ✅ **Resilience** - Failure isolation between services
- ✅ **Asynchronous Communication** - Via Kafka events
- ✅ **Load Balancing** - Through Nginx gateway
- ✅ **Containerization** - Easy deployment with Docker

This project demonstrates all these concepts in a real-world scenario.

---

## System Architecture

### High-Level Diagram

┌─────────────────────────────────────────────────────────────┐
│                         CLIENT                              │
│                   (Browser/API Client)                      │
└───────────┬─────────────────────────────────────────────────┘
            │
            │ HTTP Requests
            ▼
┌─────────────┐
│   NGINX     │  Port 80
│  Gateway    │  (Reverse Proxy & Load Balancer)
└──────┬──────┘
   │
┌──────────────────┼──────────────────┐
│                  │                  │
 │                 │                  │
┌▼────────┐   ┌────▼──────┐    ┌─────▼──────┐
│  Order  │   │ Inventory │    │ Prometheus │
│ Service │   │  Service  │    │  Metrics   │
│ :8081   │   │  :8082    │    │  :9090     │
└┬────────┘   └────┬──────┘    └────────────┘
│                  │
│   ┌──────────────┘
│   │
│   │  Service-to-Service
│   │  Communication (REST)
│   │
└───┴─────────────────────┐
                          │
                 ┌────────▼────────┐
                 │     KAFKA       │
                 │  Message Broker │
                 │     :9092       │
                 │                 │
                 │ order-created   │
                 │ topic           │
                 └────┬────────────┘
                      │
                      │ Events
                      ▼
┌──────────────────────┐
│   Shared Database    │
│    PostgreSQL        │
│     :5432            │
│                      │
│  order_db            │
│  inventory_db        │
└──────────────────────┘

### Workflow: Creating an Order

Client sends: POST /orders { productId: 1, quantity: 5 }
    │
    ▼
Nginx routes to Order Service (Port 8081)
                      │
                      ▼
Order Service validates request
             │
             ▼
Calls Inventory Service via Feign (REST with Circuit Breaker)

Checks stock availability
Reserves stock if available
      │
      ▼
Creates Order in PostgreSQL (PENDING status)
│
▼
Publishes Event to Kafka: "OrderCreated"
{ orderNumber, productId, quantity, totalPrice, timestamp }
           │
           ▼
Returns 201 Created with Order details to Client
     │
     ▼
Inventory Service consumes Kafka event

Listens on "order-created-topic"
Updates internal statistics
Can trigger shipment, notifications, etc.


---

## Technology Stack

### Core Technologies
| Component | Version | Purpose |
|-----------|---------|---------|
| Java | 21 (LTS) | Language with latest features (records, sealed interfaces) |
| Spring Boot | 3.2.0 | Application framework |
| Spring Cloud | 2023.0.0 | Service discovery, load balancing |
| Spring Data JPA | 3.2.0 | ORM with Hibernate |
| Spring Kafka | 3.1.0 | Kafka integration |
| PostgreSQL | 16 | Relational database |
| Kafka | 7.5.0 | Distributed event streaming |
| Zookeeper | 7.5.0 | Kafka coordination |
| Nginx | latest | Reverse proxy & gateway |
| Docker | 29.6.1 | Containerization |
| Docker Compose | 5.3.0 | Orchestration |

### Key Libraries
- **Lombok** - Reduces boilerplate code
- **Resilience4j** - Circuit breaker pattern
- **OpenFeign** - Declarative HTTP client
- **Micrometer** - Metrics collection
- **Prometheus** - Monitoring & alerting
- **Validation API** - Input validation

---

## Project Structure


microservices-project/
│
├── order-service/                          # Service 1
│   ├── pom.xml                             # Maven dependencies
│   ├── src/main/
│   │   ├── java/com/microservices/order/
│   │   │   ├── OrderServiceApplication.java
│   │   │   ├── controller/
│   │   │   │   └── OrderController.java
│   │   │   ├── service/
│   │   │   │   └── OrderService.java
│   │   │   ├── domain/
│   │   │   │   └── Order.java
│   │   │   ├── dto/
│   │   │   │   └── OrderDTOs.java
│   │   │   ├── repository/
│   │   │   │   └── OrderRepository.java
│   │   │   ├── client/
│   │   │   │   └── InventoryServiceClient.java
│   │   │   ├── kafka/
│   │   │   │   └── OrderEventProducer.java
│   │   │   └── exception/
│   │   │       ├── Exceptions.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.yml
│   └── target/
│
├── inventory-service/                      # Service 2
│   ├── pom.xml
│   ├── src/main/
│   │   ├── java/com/microservices/inventory/
│   │   │   ├── InventoryServiceApplication.java
│   │   │   ├── controller/
│   │   │   │   └── InventoryController.java
│   │   │   ├── service/
│   │   │   │   └── InventoryService.java
│   │   │   │   └── OrderEventConsumer.java
│   │   │   ├── domain/
│   │   │   │   └── ProductInventory.java
│   │   │   ├── dto/
│   │   │   │   └── InventoryDTOs.java
│   │   │   ├── repository/
│   │   │   │   └── InventoryRepository.java
│   │   │   ├── kafka/
│   │   │   │   └── OrderEventConsumer.java
│   │   │   └── exception/
│   │   │       ├── Exceptions.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.yml
│   └── target/
│
├── docker-compose.yml                      # Infrastructure orchestration
├── init-db.sql                             # Database initialization
├── nginx.conf                              # Nginx configuration
├── prometheus.yml                          # Metrics configuration
└── README.md                               # This file



---

## Setup Instructions

### Prerequisites
1. **Docker Desktop** (v29.6.1+) - [Download](https://www.docker.com/products/docker-desktop)
2. **Java 21 JDK** - [Download](https://www.oracle.com/java/technologies/downloads/)
3. **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
4. **Git** - [Download](https://git-scm.com/)

### Step 1: Install Docker Desktop

1. Download and install Docker Desktop
2. Start Docker Desktop from Start menu
3. Wait for Docker icon to appear in system tray
4. Verify installation:
```powershell
docker --version
docker compose --version
```

### Step 2: Create Project Structure

```powershell
cd D:\jAVA
mkdir microservices-project
cd microservices-project
```

Create these files in the root directory:
- `docker-compose.yml`
- `nginx.conf`
- `init-db.sql`
- `prometheus.yml`

### Step 3: Start Docker Infrastructure

```powershell
cd D:\jAVA\microservices-project
docker compose up -d
```

Verify all services are running:
```powershell
docker compose ps
```

Expected output:
   NAME              STATUS
postgres_db       Up (healthy)
zookeeper         Up
kafka             Up
nginx_gateway     Up
prometheus        Up

### Step 4: Run Spring Boot Applications

Open two separate PowerShell/CMD terminals:

**Terminal 1 - Order Service:**
```powershell
cd D:\jAVA\microservices-project\order-service
mvn clean install
mvn spring-boot:run
```

**Terminal 2 - Inventory Service:**
```powershell
cd D:\jAVA\microservices-project\inventory-service
mvn clean install
mvn spring-boot:run
```


---

## Monitoring
---

## Monitoring

### Prometheus Metrics
Access: `http://localhost:9090`

**Available Metrics:**
- `http_server_requests_seconds_*` - HTTP endpoint latency
- `process_*` - JVM process metrics
- `kafka_*` - Kafka-specific metrics
- `spring_kafka_*` - Spring Kafka integration metrics

### Health Endpoints

http://localhost:8081/api/actuator/health     # Order Service
http://localhost:8082/api/actuator/health     # Inventory Service
