☕ Coffee Shop Backend

This is the backend service for a coffee shop management system, built with Spring Boot. It provides RESTful APIs to support user roles, order and drink management, and secure access control. The frontend for this application is developed separately using React.

👥 User Roles
- Coffee Shop Owner:
  - Registers and creates a coffee shop.
  - Can add or remove workers (Waiters, Bartenders, Managers).
- Workers:
  - Must be assigned to a coffee shop to perform actions.
  - Can create, update, and delete orders.
  - Can add or remove drinks from existing orders.
  - Can add new drinks (only if assigned to a coffee shop).

🔐 Security
- Implemented with Spring Security using a custom authentication provider.
- Session-based authentication: Only the homepage is publicly accessible; all other routes require login.
- Users can register, log in, and log out.
- JWT support is planned for future updates.

📦 Core Features
- Coffee shop owners manage their shop and staff.
- Workers manage customer orders and drinks.
- Strict role-based access control:
  - Only owners can manage workers.
  - Only workers can manage orders.
  - Both owners and workers can add drinks (with restrictions).

🧪 API & Testing
- Fully RESTful API design.
- API endpoints are documented and testable via Swagger UI.

🗄️ Technology Stack
- Backend: Spring Boot
- Frontend: React (in a separate repository)
- Security: Spring Security (custom auth provider, session-based)
- Database: In-memory H2
  - No advanced database features (e.g., materialized views)