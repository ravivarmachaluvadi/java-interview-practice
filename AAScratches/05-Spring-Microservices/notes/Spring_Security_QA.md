# Spring Security Q&A

Interview-prep notes on Spring Security: the core components, how application-to-application APIs are secured with JWT and why that scales, and the OAuth 2.0 grant types.

## Table of Contents

1. [What does LDAP stand for?](#1-what-does-ldap-stand-for)
2. [What are the main components of Spring Security?](#2-what-are-the-main-components-of-spring-security)
3. [Walk me through how your application-to-application APIs are secured using JWT. How does it help scalability?](#3-walk-me-through-how-your-application-to-application-apis-are-secured-using-jwt-how-does-it-help-scalability)
4. [What are the grant types in OAuth 2.0 (Authorization Code, Client Credentials, etc.) used for authentication and access tokens?](#4-what-are-the-grant-types-in-oauth-20-authorization-code-client-credentials-etc-used-for-authentication-and-access-tokens)

## 1. What does LDAP stand for?

LDAP stands for Lightweight Directory Access Protocol.

## 2. What are the main components of Spring Security?

### Authentication

- **Purpose:** Verifies the identity of a user (i.e., "Who are you?").
- **Key components:**
  - `Authentication` – Represents the authentication token (username, password, authorities, etc.).
  - `AuthenticationManager` – The main interface responsible for processing an `Authentication` request. Common implementation: `ProviderManager`.
  - `AuthenticationProvider` – Performs the actual authentication logic (e.g., username/password validation). Example: `DaoAuthenticationProvider` uses a `UserDetailsService` to load user info.
  - `UserDetailsService` – Loads user-specific data (usually from a database).
  - `UserDetails` – Represents the user's identity (username, password, roles, etc.).
  - `PasswordEncoder` – Encodes and verifies passwords (e.g., `BCryptPasswordEncoder`).

### Security Filters (Filter Chain)

- **Purpose:** The heart of Spring Security — intercepts requests before they reach your controllers.
- **Key components:**
  - `SecurityFilterChain` – Defines the order and configuration of filters.
- **Key filters:**
  - `UsernamePasswordAuthenticationFilter` – Handles form login.
  - `BasicAuthenticationFilter` – Handles HTTP Basic authentication.
  - `BearerTokenAuthenticationFilter` – Used for JWT/OAuth2 bearer tokens.
  - `ExceptionTranslationFilter` – Handles authentication and access-denied exceptions.
  - `SecurityContextPersistenceFilter` – Loads and stores the `SecurityContext` in session.
  - `LogoutFilter` – Handles logout requests.

### OAuth2 / JWT / SSO Integration

- **Purpose:** Supports modern authentication mechanisms.
- OAuth2 Client / Resource Server – For integration with external identity providers (Google, GitHub, etc.).
- JWT Support – For stateless authentication in REST APIs.
- OpenID Connect (OIDC) – Extends OAuth2 for user identity management.

## 3. Walk me through how your application-to-application APIs are secured using JWT. How does it help scalability?

Excellent — this is one of the most common and high-impact questions for experienced backend developers. Let's break it down step by step so you can explain it clearly and confidently in an interview.

### The Scenario

- We have multiple microservices (e.g., User Service, Order Service, Payment Service).
- These services communicate application-to-application via REST APIs.
- We want:
  - Secure communication
  - No session overhead
  - Scalable and stateless APIs
- So we use JWT (JSON Web Token)-based authentication.

### How JWT-based Security Works (Step-by-Step Flow)

#### Step 1: Authentication & Token Generation

- The client (or another service) sends credentials (client ID + secret / username + password) to the Authentication Service.
- The Authentication Service validates the credentials (e.g., via `UserDetailsService`, OAuth2 or LDAP).
- On success, it issues a JWT token signed with a private secret key (or asymmetric key).
- Example JWT structure: `Header.Payload.Signature`
  - Header – algorithm, token type
  - Payload – claims (userId, roles, expiry, etc.)
  - Signature – digital signature (HMAC or RSA)
- Example payload:

```json
{
  "sub": "user123",
  "role": "ADMIN",
  "exp": 1735116557
}
```

#### Step 2: Service-to-Service Call

- Now, when Service A calls Service B, it attaches the JWT token in the HTTP header:

```text
Authorization: Bearer <jwt-token>
```

- Service B validates the token locally:
  - Verifies the signature using the shared secret (or public key)
  - Checks the expiry time
  - Extracts roles or scopes from the payload
- If valid → allows the request.
- If invalid → returns `401 Unauthorized`.

#### Step 3: Stateless Authorization

Since JWTs are self-contained (they carry user info + signature), no session lookup or DB call is needed for each request. That's why it's perfect for distributed microservice systems — every service can independently verify tokens.

### Key Components in Spring Security

Typical setup:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
```

And your `JwtAuthenticationFilter` would:

- Extract the JWT from the `Authorization` header
- Validate the token
- Populate `SecurityContextHolder` with an `Authentication`

Example:

```java
String token = resolveToken(request);
if (tokenProvider.validateToken(token)) {
    Authentication auth = tokenProvider.getAuthentication(token);
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```

### How JWT Helps with Scalability

| Aspect | Traditional Session-Based | JWT-Based |
| --- | --- | --- |
| Session storage | Needs centralized session store (DB/Redis) | No session store — stateless |
| Server state | Stateful | Stateless |
| Horizontal scaling | Harder — sessions must replicate | Easy — any instance can validate the token |
| Performance | Extra DB/Redis lookup per request | Direct local verification |
| Inter-service auth | Complex — share sessions or tokens manually | Simple — share secret/public key |

**Result:**

- Easier horizontal scaling — just add more service instances
- No sticky sessions
- Lightweight authentication
- Works seamlessly across regions & load balancers

### Typical Architecture

```text
                ┌──────────────────────┐
                │   Auth Service       │
                │ (issues JWT tokens)  │
                └──────────┬───────────┘
                           │
                ┌──────────▼───────────┐
                │  Service A           │
                │ (includes JWT filter)│
                └──────────┬───────────┘
                           │
        Authorization: Bearer <JWT>
                           │
                ┌──────────▼───────────┐
                │  Service B           │
                │ (validates JWT)      │
                └──────────────────────┘
```

Each service can independently verify JWTs → no need to depend on a shared session system.

### Security Considerations

- Use short-lived access tokens (e.g., 15 mins)
- Use refresh tokens for reissuing new access tokens
- Always use HTTPS (to prevent token interception)
- Store secrets securely (e.g., AWS Secrets Manager)
- Use asymmetric keys (RSA) for microservice environments

### How to Explain in an Interview

> "In our microservice architecture, we secure inter-service communication using JWT-based stateless authentication. Each service validates JWT tokens locally, so there's no dependency on a central session store. This significantly improves scalability — because authentication is lightweight, and any instance can handle any request without sharing session state."

## 4. What are the grant types in OAuth 2.0 (Authorization Code, Client Credentials, etc.) used for authentication and access tokens?

OAuth 2.0 defines different grant types (flows) for obtaining access tokens depending on the type of client (web app, mobile app, service, etc.) and the security context.

### Main OAuth 2.0 Grant Types

#### 1. Authorization Code Grant (most common)

- **Used by:** Web & mobile apps (with a backend server).
- **Purpose:** Securely obtain tokens on behalf of a user.
- **Flow:**
  1. User is redirected to the authorization server (e.g., Google Login).
  2. User authenticates and authorizes the app.
  3. The authorization server redirects back with an authorization code.
  4. The app's backend exchanges this code for an access token (and optionally a refresh token).
- **Advantages:**
  - Most secure (tokens are never exposed to the browser).
  - Supports refresh tokens.
  - Suitable for confidential clients (with server-side secret).
- **Use case:** Typical login for web apps — e.g., "Login with Google".

#### 2. Authorization Code Grant with PKCE (Proof Key for Code Exchange)

- **Used by:** Mobile and SPA (Single Page Apps).
- **Purpose:** Prevent code interception attacks (used when there's no backend to safely store a client secret).
- **Flow:** Same as Authorization Code Grant, but includes:
  - A `code_verifier` (random string generated by the client)
  - A `code_challenge` (hashed version of the verifier, sent in step 1)
  - The authorization server verifies both to ensure authenticity.
- **Advantages:**
  - Secure for public clients (mobile, SPA).
  - Replaces the old Implicit flow.
- **Use case:** "Login with Google" from a mobile or JavaScript SPA.

#### 3. Client Credentials Grant

- **Used by:** Machine-to-machine (M2M) communication.
- **Purpose:** Server or service authenticates itself — no user involved.
- **Flow:**
  1. Client (service) sends its `client_id` + `client_secret` to the authorization server.
  2. Receives an access token.
  3. Uses the token to call another service.
- **Advantages:**
  - Simple and direct.
  - Ideal for backend services or microservices.
- **Use case:** A microservice calling another internal API securely.

#### 4. Resource Owner Password Credentials Grant (Deprecated / Legacy)

- **Used by:** Trusted first-party apps only.
- **Purpose:** User provides username & password directly to the client app.
- **Flow:**
  1. User enters credentials in the app.
  2. App sends credentials to the authorization server.
  3. Authorization server returns an access token.
- **Disadvantages:**
  - Exposes user credentials to the client.
  - Not recommended by OAuth 2.1.
- **Use case:** Legacy systems; replaced by Authorization Code + PKCE.

#### 5. Implicit Grant (Deprecated)

- **Used by:** Old browser-based apps (SPAs).
- **Purpose:** Quickly obtain an access token via browser redirect (no backend).
- **Flow:** User authorizes via redirect, and the access token is sent in the URL fragment (`#access_token`).
- **Disadvantages:**
  - Token exposed to browser & logs.
  - No refresh token.
  - Removed in OAuth 2.1 (use PKCE instead).

### Additional / Extension Flows

#### 6. Refresh Token Grant

- **Used to:** Get a new access token without user re-login.
- **Flow:**
  1. Client sends the refresh token to the authorization server.
  2. Server returns a new access token (and possibly a new refresh token).
- **Use case:** Long-lived sessions (e.g., mobile app stays logged in).

#### 7. Device Authorization Grant (Device Flow)

- **Used by:** Devices without a browser or keyboard (e.g., TVs, IoT devices).
- **Flow:**
  1. Device asks the user to visit a URL on another device.
  2. User authorizes there.
  3. Device polls the token endpoint until authorized.
  4. Access token issued.
- **Use case:** Logging into your Netflix account on a smart TV.

### Grant Type Summary

| Grant Type | User Interaction | Use Case | Recommended? | Tokens Returned |
| --- | --- | --- | --- | --- |
| Authorization Code | Yes | Web apps | Yes | Access + Refresh |
| Authorization Code + PKCE | Yes | Mobile / SPA | Yes (preferred) | Access + Refresh |
| Client Credentials | No | M2M APIs | Yes | Access |
| Password | Yes | Legacy trusted apps | No | Access + Refresh |
| Implicit | Yes | Old SPAs | No | Access |
| Refresh Token | No | Token renewal | Yes | Access |
| Device Code | Yes (via external device) | TVs / IoT | Yes | Access + Refresh |
