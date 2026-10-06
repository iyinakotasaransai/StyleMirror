# StyleMirror – AI-Powered Fashion E-Commerce & Virtual Try-On Management Portal

> **A Production-Grade, Full-Stack B.Tech Final-Year Capstone Project**  
> Built with **Spring Boot 3.5.16 (Java 25)**, **MySQL 8.x**, **Vanilla JavaScript (ES6+)**, **MediaPipe Pose**, **Three.js (r128)**, and **Google Gemini 2.5 Flash**.

---

## 🌟 Executive Summary

**StyleMirror** is an enterprise-grade luxury e-commerce and augmented reality apparel portal designed to bridge the gap between physical fitting rooms and modern digital storefronts. Traditional fashion e-commerce suffers from high return rates due to inaccurate sizing and lack of visual context. StyleMirror solves this through:

1. **Client-Side Real-Time Virtual Try-On (AR):** Uses MediaPipe Pose to detect 33 human skeletal landmarks directly within the browser, calculating torso width, shoulder angles, and hip anchors to project transparent garment cuts accurately onto the user's live video stream.
2. **Interactive 3D Fashion Avatar:** Built on Three.js (WebGL), featuring procedural skeletal animation rigs (Idle, Thinking, Nod, Wave) and dynamic mouth articulation synchronized to voice audio.
3. **Google Gemini AI Stylist:** A secure Spring Boot proxy connects to Google Gemini to deliver tailored occasion, color harmony, and fit evaluations without exposing API keys to the browser. Features client rate-limiting, response caching, and deterministic fallbacks.
4. **Voice Synthesis & Synchronized Subtitles:** Native browser `SpeechSynthesis` combined with animated real-time HUD subtitle captions.
5. **Multi-Tenant Enterprise Commerce:** Strict three-tier Role-Based Access Control (**BUYER**, **SELLER**, **ADMIN**) backed by stateless JWT authentication, BCrypt encryption, and MySQL 8 relational schema.

---

## 🏗️ System Architecture & Technology Stack

```
+-----------------------------------------------------------------------------------+
|                                  CLIENT LAYER                                     |
|  - Vanilla JavaScript ES6+ Modules (api.js, tryon.js, avatar.js, ai.js, auth.js)   |
|  - Responsive HTML5 & Modern CSS3 (Dark Atelier Luxury Theme)                      |
|  - MediaPipe Pose (33-point Landmark Detection + EMA Smoothing)                   |
|  - Three.js (r128) WebGL Avatar Engine                                            |
|  - Web Speech API (SpeechSynthesis) + Live Synchronized HUD Subtitles             |
+------------------------------------------^----------------------------------------+
                                           | HTTP REST / JSON (JWT Bearer Token)
+------------------------------------------v----------------------------------------+
|                            SPRING BOOT BACKEND LAYER                              |
|  - Security & Auth: Spring Security 6, JJWT (0.12.5), BCrypt, RBAC Filters         |
|  - Controllers: Auth, Products, Categories, Cart, Wishlist, Orders, Seller, Admin |
|  - Services: Business Logic, Rate Limiting (10 req/min), Caching, File Uploads    |
|  - AI Proxy: Google Gemini Flash Client (Zero Browser Credential Exposure)         |
|  - Data Access: Spring Data JPA, Hibernate ORM, Transaction Management            |
+------------------------------------------^----------------------------------------+
                                           | JDBC / TCP:3306
+------------------------------------------v----------------------------------------+
|                                  DATABASE LAYER                                   |
|  - MySQL 8.x (`stylemirror` schema)                                               |
|  - 17 Relational Tables: Users, Roles, Sellers, Products, Variants, Orders, etc.  |
+-----------------------------------------------------------------------------------+
```

### Detailed Tech Stack Specifications:

| Component | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Backend Framework** | Spring Boot | 3.5.16 | Enterprise REST API and application container |
| **Language** | Java (JDK) | 25 | Core server-side implementation |
| **Security** | Spring Security + JJWT | 0.12.5 | Stateless JWT authentication and role authorization |
| **Password Hashing** | BCrypt (Cost Factor 10) | Standard | Cryptographic one-way password storage |
| **Persistence** | Spring Data JPA / Hibernate | 6.x | Object-Relational Mapping (ORM) and transactions |
| **Database** | MySQL Server | 8.0+ | Relational data persistence with foreign keys & indexes |
| **Frontend Framework** | Vanilla HTML5 / CSS3 / ES6+ | Modern | Lightweight, high-performance web interface (No heavy SPA bloat) |
| **Computer Vision** | MediaPipe Pose | 0.5.1675469404 | 33-point real-time pose tracking & shoulder geometry |
| **3D Graphics** | Three.js | r128 | WebGL procedural fashion mannequin and gesture rigs |
| **Generative AI** | Google Gemini API | 2.5 Flash | AI fashion recommendations and style consultation |
| **Voice Synthesis** | Web Speech API | Standard | Natural voice reading of recommendations |
| **Build Tool** | Apache Maven | 3.9+ | Dependency management and artifact compilation |

---

## 👥 User Roles & Access Capabilities

### 1. Buyer
- **Authentication:** Registration with instant JWT issuance, secure login, persistent session.
- **Catalog & Discovery:** Search by keywords, category filter tabs, price sorting, variant inspection.
- **Virtual Fitting Room:** Real-time webcam AR try-on with torso tracking, photo upload fallback, garment switcher.
- **AI Stylist:** Request real-time occasion & fit consultations powered by Gemini with audio voice playback and subtitles.
- **Wishlist & Cart:** Save favorites with duplicate protection, dynamic cart item quantity steppers, live subtotal computation.
- **Checkout & Tracking:** Simulated multi-payment gateway (Credit Card, UPI, COD), atomic inventory validation, historical order tracking with timeline statuses.

### 2. Seller
- **Merchant Studio Dashboard:** Key performance metrics (lifetime sales, active items, order counts), critical low-inventory alerts (&le; 15 units).
- **Garment Management:** Add new apparel items with title, brand, description, category, and active status toggle.
- **Variant Studio:** Configure multiple variants (Size XS-XXL, Color, Price, Stock units, unique SKU code).
- **Try-On Asset Studio:** Upload transparent PNG try-on overlays and 3D avatar `.glb` files with immediate AR validation.
- **Order Fulfillment:** Real-time view of customer orders containing boutique items, live status updater (`PLACED` &rarr; `CONFIRMED` &rarr; `PROCESSING` &rarr; `SHIPPED` &rarr; `DELIVERED`).
- **Atelier Analytics:** Gross sales graphs, average order value (AOV), and inventory replenishment alerts.

### 3. Administrator
- **Governance Console:** System-wide metrics (total users, registered sellers, global catalog size, platform gross volume).
- **User Management:** Full user directory with ability to suspend or reactivate accounts.
- **Seller Moderation:** Review pending seller applications, approve boutiques, or revoke/block offending stores.
- **Taxonomy CRUD:** Create, update, and manage global fashion categories.
- **Global Order Tracking:** Monitor all transactions across all sellers with status override capability.
- **Intelligence Reports:** Real-time visualization of order pipeline status distribution, category volume, AR session counts, and Gemini AI query telemetry.

---

## 🔑 Pre-Configured Demo Accounts

All demo accounts are automatically initialized and synchronized on application launch:

| Role | Email Address | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@stylemirror.com` | `Admin@123` | Full administrative control suite & platform governance |
| **SELLER** | `seller@stylemirror.com` | `Seller@123` | Atelier studio, product CRUD, inventory, order fulfillment |
| **BUYER** | `buyer@stylemirror.com` | `Buyer@123` | Catalog browsing, live try-on, cart, checkout, order tracking |

---

## 🛠️ Step-by-Step Installation & Setup

### Prerequisites
- **Java Development Kit (JDK):** Version 17 or higher (`java -version`)
- **Apache Maven:** Version 3.8+ (`mvn -version`)
- **MySQL Server:** Version 8.0+ running on `localhost:3306`

### 1. Database Configuration
Ensure your MySQL server is running. Create the database:
```sql
CREATE DATABASE IF NOT EXISTS stylemirror CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Check `src/main/resources/application.properties` to ensure credentials match your MySQL setup:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/stylemirror?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=MyNewPass123!
```

> **Note:** The application includes database initialization scripts in `database/schema.sql` and `database/seed.sql`. Additionally, `DataInitializer.java` automatically runs on startup to seed required roles, demo accounts, and catalog items if empty!

### 2. Configure Google Gemini API (Optional)
To enable live Gemini AI recommendations, set your Gemini API key in `src/main/resources/application.properties` or via environment variable:
```properties
gemini.api.key=${GEMINI_API_KEY:your_actual_api_key_here}
```
*If left as default, the backend's resilient fallback engine automatically generates context-aware luxury fashion recommendations without throwing errors.*

### 3. Build & Run the Backend
Open a terminal in the project root directory and execute:
```bash
# Compile and package application
mvn clean package -DskipTests

# Run the Spring Boot server
mvn spring-boot:run
```

The server will launch at:
```
http://localhost:8080
```

### 4. Run the Automated Test Suite
To execute the comprehensive unit and integration test suite:
```bash
mvn test
```

---

## 🌐 Complete REST API Specification

All backend endpoints are prefixed with `/api`. Authenticated routes require the HTTP header `Authorization: Bearer <jwt_token>`.

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Register new user as `BUYER` or `SELLER` |
| `POST` | `/api/auth/login` | Public | Authenticate user and receive JWT bearer token |
| `GET` | `/api/auth/me` | Authenticated | Retrieve current user profile and role details |

### Garment Catalog (`/api/products`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/products` | Public | Fetch active products (supports `?categoryId` and `?search`) |
| `GET` | `/api/products/{id}` | Public | Fetch product details, variants, images, and try-on asset |
| `GET` | `/api/products/search?query=` | Public | Search products by name or brand |
| `GET` | `/api/products/category/{id}` | Public | Filter garments by category ID |
| `POST` | `/api/products` | Seller, Admin | Create a new product with variants & try-on overlay |
| `PUT` | `/api/products/{id}` | Seller, Admin | Update existing product details |
| `DELETE` | `/api/products/{id}` | Seller, Admin | Deactivate product from catalog |
| `GET` | `/api/products/{id}/variants` | Public | Retrieve variants for product |
| `POST` | `/api/products/{id}/variants` | Seller, Admin | Add variant to product |
| `PUT` | `/api/variants/{id}` | Seller, Admin | Update variant price, stock, or size |
| `DELETE` | `/api/variants/{id}` | Seller, Admin | Remove variant |

### Taxonomy Categories (`/api/categories`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/categories` | Public | List all active categories |
| `GET` | `/api/categories/{id}` | Public | Get single category details |
| `POST` | `/api/categories` | Admin | Create new apparel category |
| `PUT` | `/api/categories/{id}` | Admin | Update existing category |
| `DELETE` | `/api/categories/{id}` | Admin | Deactivate category |

### Shopping Cart (`/api/cart`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/cart` | Authenticated | Fetch current user cart items and subtotal |
| `POST` | `/api/cart/items` | Authenticated | Add garment variant to cart |
| `PUT` | `/api/cart/items/{id}` | Authenticated | Update item quantity |
| `DELETE` | `/api/cart/items/{id}` | Authenticated | Remove specific item from cart |
| `DELETE` | `/api/cart` | Authenticated | Clear all items from cart |

### Wishlist (`/api/wishlist`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/wishlist` | Authenticated | Fetch user wishlist items |
| `POST` | `/api/wishlist/{productId}` | Authenticated | Add product to wishlist (prevents duplicates) |
| `DELETE` | `/api/wishlist/{productId}` | Authenticated | Remove product from wishlist |

### Checkout & Orders (`/api/orders`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/orders` | Authenticated | Place order from cart with simulated payment |
| `GET` | `/api/orders` | Authenticated | List all historical orders for current user |
| `GET` | `/api/orders/{id}` | Authenticated | Get detailed order summary and line items |
| `PUT` | `/api/orders/{id}/status` | Seller, Admin | Update order fulfillment status |

### Seller Atelier (`/api/seller`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/seller/dashboard` | Seller, Admin | Fetch seller stats, sales, and low-inventory alerts |
| `GET` | `/api/seller/products` | Seller, Admin | List products owned by this seller |
| `GET` | `/api/seller/orders` | Seller, Admin | List orders containing items from this seller |
| `GET` | `/api/seller/statistics` | Seller, Admin | Boutique revenue breakdown and inventory health |

### Administrator Governance (`/api/admin`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admin/dashboard` | Admin | System-wide statistics and recent orders |
| `GET` | `/api/admin/users` | Admin | List all registered user accounts |
| `PUT` | `/api/admin/users/{id}/toggle` | Admin | Suspend or reactivate user account |
| `GET` | `/api/admin/sellers` | Admin | List all registered merchants and stores |
| `PUT` | `/api/admin/sellers/{id}/approve` | Admin | Approve pending seller store |
| `PUT` | `/api/admin/sellers/{id}/block` | Admin | Block offending seller store |
| `GET` | `/api/admin/products` | Admin | List all catalog products across all sellers |
| `GET` | `/api/admin/orders` | Admin | List all platform orders |

### Virtual Try-On & Assets (`/api/tryon`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/tryon/{productId}` | Public | Retrieve overlay PNG path and 3D model path |
| `POST` | `/api/tryon/session` | Public | Log try-on engagement session telemetry |

### Generative AI Stylist (`/api/ai`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/ai/recommend` | Public/Auth | Request Gemini style advice for garment/variant context |

### Upload Management (`/api/uploads`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/uploads/product-image` | Seller, Admin | Upload garment photo to `/uploads/products` |
| `POST` | `/api/uploads/tryon-overlay` | Seller, Admin | Upload transparent try-on PNG to `/uploads/tryon` |
| `POST` | `/api/uploads/avatar` | Seller, Admin | Upload 3D `.glb` model to `/uploads/models` |

### System Reports (`/api/reports`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reports` | Seller, Admin | Comprehensive revenue, try-on, and AI analytics |

---

## 🪞 Virtual Try-On (MediaPipe Pose) Engineering Breakdown

The live webcam fitting room (`src/main/resources/static/js/tryon.js`) performs real-time body tracking entirely on the client's GPU via HTML5 Canvas:

1. **Webcam Acquisition:** Requests high-resolution video stream via `navigator.mediaDevices.getUserMedia({ video: { facingMode: 'user', width: 1280, height: 720 } })`. If camera access is denied, the UI gracefully defaults to the **Photo Upload Fallback**.
2. **Landmark Extraction:** Processes frames through MediaPipe Pose, extracting 33 landmarks:
   - Left Shoulder (`#11`) & Right Shoulder (`#12`)
   - Left Hip (`#23`) & Right Hip (`#24`)
   - Nose (`#0`) for head orientation reference
3. **Geometry Calculation:**
   - **Torso Midpoint:** $C_x = \frac{x_{11} + x_{12}}{2}$, $C_y = \frac{y_{11} + y_{12}}{2}$
   - **Shoulder Width:** $W_{raw} = \sqrt{(x_{12} - x_{11})^2 + (y_{12} - y_{11})^2}$
   - **Shoulder Tilt Angle:** $\theta = \operatorname{atan2}(y_{12} - y_{11}, x_{12} - x_{11})$
   - **Torso Height:** $H_{torso} = \sqrt{(C_x - Hip_x)^2 + (C_y - Hip_y)^2}$
4. **Exponential Moving Average (EMA) Smoothing:**
   To prevent frame jitter and high-frequency camera noise:
   $$S_t = \alpha \cdot X_t + (1 - \alpha) \cdot S_{t-1} \quad (\text{with } \alpha = 0.25)$$
5. **Stable-Frame Gate (`TRYON_READY`):**
   The system monitors consecutive frame displacement. Only when the torso movement delta remains below $1.5\%$ across 10 consecutive frames is the state switched from `SEARCHING_POSE` to `TRYON_READY`.
6. **Canvas Transform Matrix:**
   The canvas context applies:
   ```javascript
   ctx.save();
   ctx.translate(smoothedCenter.x, smoothedCenter.y);
   ctx.rotate(smoothedAngle);
   ctx.drawImage(overlayImg, -width / 2, -height * 0.15, width, height);
   ctx.restore();
   ```

---

## 🎭 3D Avatar & Voice Synthesis Engineering Breakdown

### Three.js Procedural Avatar (`src/main/resources/static/js/avatar.js`)
- Initializes a WebGL canvas with ambient and directional rim lighting.
- Constructs a high-fashion mannequin rig with head, neck, torso, shoulders, and articulation joints.
- Dynamically loads `.glb` / `.gltf` assets if provided, or renders procedural geometry with materials tuned for luxury aesthetics.
- **Procedural Animation Engine:**
  - `IDLE`: Gentle sinusoidal breathing and natural subtle sway.
  - `THINKING`: Head tilts upward at a $15^\circ$ angle with one arm raised in contemplative gesture.
  - `NOD`: Sinusoidal pitch nodding confirming style match.
  - `WAVE`: Arm raises and oscillates along the roll axis in greeting.
  - `SPEAKING`: Real-time jaw displacement rhythmically opens and closes while voice synthesis is active.

### Voice & HUD Subtitles (`src/main/resources/static/js/voice.js`)
- Utilizes the browser's `window.speechSynthesis`.
- Automatically selects the highest-fidelity English voice available on the host OS.
- Dispatches speech events (`onstart`, `onboundary`, `onend`) that synchronize both avatar mouth animation and the floating bottom-center subtitle bar.

---

## 🔒 Security & Data Integrity Highlights

- **Stateless Authentication:** Every request is authenticated via Bearer JWT tokens with an HMAC-SHA256 signature key.
- **Strict Role-Based Authorization:** Every controller endpoint is guarded by `@PreAuthorize("hasAuthority('ROLE_...')")`.
- **Seller Ownership Validation:** When a seller performs operations on products or orders, the backend strictly verifies `product.seller.user.id == currentUser.id`, preventing horizontal privilege escalation.
- **Zero API Key Leakage:** The Google Gemini API key is stored strictly on the server and is never passed to frontend client scripts.
- **AI Rate Limiting & Resilience:** Built-in in-memory rate limiting enforces a maximum of 10 AI queries per user per minute. Responses are cached to prevent duplicate Gemini invocations, and deterministic fallbacks guarantee zero UI breakage.

---

## 📁 Repository Structure

```
Edunet pro/
├── database/
│   ├── schema.sql              # MySQL DDL for all 17 tables & constraints
│   └── seed.sql                # Production seed data (users, roles, products, variants)
├── pom.xml                     # Maven project configuration
├── README.md                   # Comprehensive architectural documentation
└── src/
    ├── main/
    │   ├── java/com/stylemirror/
    │   │   ├── config/         # SecurityConfig, WebConfig, CORS, ResourceHandlers
    │   │   ├── controller/     # 12 REST Controllers (Auth, Product, Cart, Admin, etc.)
    │   │   ├── dto/            # 23 Request/Response Data Transfer Objects
    │   │   ├── entity/         # 16 JPA Entities matching MySQL schema 1:1
    │   │   ├── enums/          # RoleType, OrderStatus, ApprovalStatus, PaymentStatus
    │   │   ├── exception/      # GlobalExceptionHandler and custom exceptions
    │   │   ├── repository/     # 16 Spring Data JPA Repositories
    │   │   ├── security/       # JwtTokenProvider, UserPrincipal, JwtAuthFilter
    │   │   ├── service/        # Service interfaces and implementations
    │   │   └── util/           # DataInitializer (auto-sync demo users & seed)
    │   └── resources/
    │       ├── application.properties
    │       └── static/         # Frontend static resources
    │           ├── css/        # Modular CSS (global, components, ecommerce, dashboard)
    │           ├── js/         # Vanilla ES6 modules (tryon, avatar, ai, auth, api)
    │           ├── pages/      # HTML5 Pages for Buyer, Seller, and Admin
    │           ├── assets/     # High-res try-on overlays and product graphics
    │           └── index.html  # Luxury Atelier Landing Page
    └── test/                   # Comprehensive JUnit 5 & SpringBootTest suite
        └── java/com/stylemirror/
            ├── AuthServiceTest.java
            ├── ProductServiceTest.java
            ├── CartServiceTest.java
            ├── OrderServiceTest.java
            └── StyleMirrorApplicationTests.java
```

---

## 🏆 Final Verification & Quality Assurance

- **Java Compiler:** `0` errors, `0` warnings.
- **Maven Build:** Successfully compiles and packages executable jar.
- **Full Test Suite:** All unit and integration tests passing.
- **Browser Compatibility:** Compatible with Chrome, Edge, Firefox, and Safari with WebGL and Webcam support.
