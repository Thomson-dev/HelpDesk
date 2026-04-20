# Help Desk Ticket Management System

A modern, secure REST API for managing customer support tickets with role-based access control, JWT authentication, and SLA tracking.

## 🎯 Features

- **User Management**: Secure registration, JWT-based authentication, and role-based access control
- **Ticket Management**: Create, assign, and track support tickets with automatic priority resolution
- **Issue Categories**: Categorize tickets (Order Not Delivered, Payment Issues, Refund Requests, etc.)
- **SLA Policies**: Define and enforce Response/Resolution time SLAs by priority level
- **Audit Logging**: Track all ticket modifications with user accountability
- **Role-Based Access**: CUSTOMER, AGENT, and ADMIN roles with granular endpoint protection
- **Three-Tier Architecture**: Separation of Controller, Service, and Repository layers

## 🏗️ Tech Stack

- **Framework**: Spring Boot 4.0.5
- **Language**: Java 21
- **Database**: PostgreSQL
- **Security**: JWT (JJWT), Spring Security, BCrypt
- **ORM**: JPA/Hibernate
- **Build**: Maven
- **Lombok**: For boilerplate reduction

## 🚀 Quick Start

### Prerequisites
- Java 21+
- PostgreSQL 12+
- Maven 3.8+

### Installation

1. **Clone and navigate:**
```bash
cd demo
```

2. **Create PostgreSQL database:**
```bash
createdb helpdesk_db
```

3. **Configure database** in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk_db
spring.datasource.username=postgres
spring.datasource.password=1212
```

4. **Start the application:**
```bash
mvn spring-boot:run
```

Server runs on `http://localhost:8080`

## 🔐 Authentication & Authorization

### User Roles
- **CUSTOMER**: Create tickets, view own tickets only
- **AGENT**: View and update assigned tickets, change ticket status
- **ADMIN**: Full system access, manage users, SLA policies, view all tickets/audit logs

### JWT Flow
1. Register → Get JWT token (defaults to CUSTOMER role)
2. Login → Get new JWT token
3. Use token in `Authorization: Bearer <token>` header for protected endpoints
4. Admin assigns elevated roles via `/api/auth/assign-role` endpoint

## 📡 API Endpoints

### Authentication
```
POST   /api/auth/register          Register new user (CUSTOMER)
POST   /api/auth/login              Get JWT token
POST   /api/auth/assign-role        Assign role to user (ADMIN ONLY)
```

### Tickets
```
POST   /api/tickets                         Create ticket
GET    /api/tickets                         Get all tickets (ADMIN)
GET    /api/tickets/my-tickets              Get my tickets (CUSTOMER)
GET    /api/tickets/assigned-to-me          Get assigned tickets (AGENT/ADMIN)
GET    /api/tickets/breached                Get SLA breached tickets (ADMIN)
PUT    /api/tickets/{id}/status             Update ticket status
PUT    /api/tickets/{id}/assign/{agentId}   Assign ticket to agent (ADMIN)
```

### SLA Policies
```
POST   /api/sla-policies            Create SLA policy (ADMIN ONLY)
GET    /api/sla-policies            Get all policies (ADMIN)
PUT    /api/sla-policies/{id}       Update policy (ADMIN ONLY)
```

### Audit Logs
```
GET    /api/audit-logs/**           Get audit logs (ADMIN ONLY)
```

## 📝 Example Workflows

### 1. Register & Login
```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"pass123"}'

# Response: 
# {"token":"eyJhbGc...","name":"John Doe","email":"john@example.com","role":"CUSTOMER"}

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"pass123"}'
```

### 2. Create SLA Policy (Admin)
```bash
curl -X POST http://localhost:8080/api/sla-policies \
  -H "Authorization: Bearer <admin-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "priority":"CRITICAL",
    "responseTimeHours":1,
    "resolutionTimeHours":4
  }'
```

### 3. Create Ticket (Customer)
```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Authorization: Bearer <customer-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Order not delivered",
    "description":"My order arrived 2 weeks late",
    "category":"ORDER_NOT_DELIVERED"
  }'

# Priority auto-resolved to CRITICAL
# SLA dates auto-calculated: response in 1 hour, resolution in 4 hours
```

### 4. Assign Ticket (Admin)
```bash
curl -X PUT http://localhost:8080/api/tickets/{ticketId}/assign/{agentId} \
  -H "Authorization: Bearer <admin-token>"

# Status changes to IN_PROGRESS
# Audit log created
```

## 🎯 Issue Categories & Auto-Priority Mapping

| Category | Auto-Priority |
|----------|---------------|
| ORDER_NOT_DELIVERED | CRITICAL |
| PAYMENT_NOT_CONFIRMED | CRITICAL |
| WRONG_ITEM_RECEIVED | HIGH |
| REFUND_REQUEST | HIGH |
| DAMAGED_ITEM | HIGH |
| ACCOUNT_ISSUE | MEDIUM |
| GENERAL_ENQUIRY | LOW |

## 📊 Entity Relationships

```
User (1) ──── (N) Ticket [customer_id]
User (1) ──── (N) Ticket [agent_id, assigned agent]
Ticket (1) ──── (N) AuditLog
SlaPolicy (1) ──── (N) Ticket [implicit via priority]
```

## 🛡️ Security Features

- ✅ JWT token-based stateless authentication
- ✅ BCrypt password hashing (10-round salting)
- ✅ Role-based access control (RBAC) on all endpoints
- ✅ Secure role assignment (ADMIN-only `/api/auth/assign-role`)
- ✅ Users can't self-elevate roles
- ✅ Audit logging for all modifications
- ✅ CSRF protection disabled (stateless JWT)
- ✅ Spring Security integration

## 📋 Database Schema

### Users Table
```sql
- id: UUID (PK)
- name: VARCHAR (NOT NULL)
- email: VARCHAR UNIQUE (NOT NULL)
- password: VARCHAR (hashed)
- role: ENUM {CUSTOMER, AGENT, ADMIN}
```

### Tickets Table
```sql
- id: UUID (PK)
- title: VARCHAR
- description: TEXT
- category: ENUM {ORDER_NOT_DELIVERED, ...}
- status: ENUM {OPEN, IN_PROGRESS, RESOLVED, CLOSED}
- priority: ENUM {LOW, MEDIUM, HIGH, CRITICAL}
- created_at: TIMESTAMP
- response_due_at: TIMESTAMP
- resolution_due_at: TIMESTAMP
- sla_breached: BOOLEAN
- customer_id: UUID (FK → User)
- agent_id: UUID (FK → User)
```

### SLA Policies Table
```sql
- id: BIGINT (PK)
- priority: ENUM (UNIQUE)
- response_time_hours: INT
- resolution_time_hours: INT
```

### Audit Logs Table
```sql
- id: BIGINT (PK)
- ticket_id: UUID (FK)
- performed_by_id: UUID (FK → User)
- action: TEXT
- created_at: TIMESTAMP
```

## ⚙️ Configuration

Key properties in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk_db
spring.jpa.hibernate.ddl-auto=update

# Logging
logging.level.com.thomson.demo=DEBUG
logging.level.org.springframework.security=DEBUG

# JWT (in JwtUtil.java)
SECRET = "your-very-long-secret-key-at-least-32-chars!!"
EXPIRATION_MS = 86400000 (24 hours)
```

## 🔄 State Management

**Ticket Status Flow:**
```
OPEN → IN_PROGRESS → RESOLVED → CLOSED
```

**SLA Breach Logic:**
- Checked daily/on-demand via scheduler
- Triggered when current time > responseDueAt or resolutionDueAt
- Flagged in `slaBreached` field

## 📚 Folder Structure

```
src/main/java/com/thomson/demo/
├── controller/          # REST endpoints
├── service/            # Business logic
├── repository/         # Database queries
├── entity/             # JPA entities
├── dto/                # Request/Response objects
├── enums/              # Role, Status, Priority, Category
├── security/           # JWT, authentication, security config
└── DemoApplication.java
```

## 🚨 Error Handling

All errors return consistent JSON format:

```json
{
  "timestamp": "2026-04-19T17:45:30.123",
  "status": 400,
  "message": "Email already in use",
  "error": "Bad Request"
}
```

## 🧪 Testing Checklist

- [ ] Register user (defaults to CUSTOMER)
- [ ] Login and verify JWT works
- [ ] Create SLA policies (ADMIN)
- [ ] Create ticket (auto-priority assigned)
- [ ] View own tickets (CUSTOMER)
- [ ] Assign ticket to agent (ADMIN)
- [ ] Update ticket status (AGENT/ADMIN)
- [ ] View audit logs (ADMIN)
- [ ] Assign role to user (ADMIN)
- [ ] Verify 403 Forbidden on unauthorized endpoints

## 🔮 Future Enhancements

- [ ] Email notifications on ticket updates
- [ ] Automated SLA breach alerts
- [ ] Ticket comments/conversation threads
- [ ] Ticket escalation rules
- [ ] Advanced search/filtering
- [ ] Dashboard analytics
- [ ] Batch ticket operations
- [ ] API rate limiting
- [ ] Webhook notifications

## 📄 License

MIT License - Feel free to use and modify

---

**Built with ❤️ using Spring Boot, JWT, and PostgreSQL**
