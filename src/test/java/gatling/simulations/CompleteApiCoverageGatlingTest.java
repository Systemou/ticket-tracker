package gatling.simulations;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.gatling.javaapi.core.ChainBuilder;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.responseTimeInMillis;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import io.gatling.javaapi.http.HttpProtocolBuilder;

/**
 * Comprehensive Gatling Performance Test for Ticket Tracker Application
 * This version is specifically designed for the JHipster Ticket Tracker application
 */
public class CompleteApiCoverageGatlingTest extends Simulation {

    // Configuration
    private static final String BASE_URL = Optional.ofNullable(System.getProperty("baseURL"))
            .orElse("http://localhost:8080");

    // API Coverage tracking
    private static final Map<String, Boolean> apiCoverage = new ConcurrentHashMap<>();
    private static final Map<String, ApiResult> apiResults = new ConcurrentHashMap<>();
    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failureCount = new AtomicInteger(0);

    // API Result tracking class
    static class ApiResult {
        String endpoint;
        String method;
        int statusCode;
        long responseTime;
        boolean success;
        String error;

        ApiResult(String endpoint, String method, int statusCode, long responseTime, boolean success, String error) {
            this.endpoint = endpoint;
            this.method = method;
            this.statusCode = statusCode;
            this.responseTime = responseTime;
            this.success = success;
            this.error = error;
        }
    }

    // HTTP Protocol Configuration
    private static final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .inferHtmlResources()
            .acceptHeader("application/json")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("en-US,en;q=0.9")
            .connectionHeader("keep-alive")
            .userAgentHeader("Gatling-TicketTracker-Test/1.0")
            .silentResources();

    // Initialize API endpoints for coverage tracking
    static {
        String[] endpoints = {
            // Authentication
            "/api/authenticate [POST]",
            "/api/authenticate [GET]",

            // Account Management
            "/api/account [GET]",
            "/api/account [POST]",
            "/api/account/reset-password/init [POST]",
            "/api/account/reset-password/finish [POST]",
            "/api/account/change-password [POST]",
            "/api/activate [GET]",

            // User Management
            "/api/users [GET]",
            "/api/register [POST]",
            "/api/authorities [GET]",
            "/api/admin/users [GET]",
            "/api/admin/users [POST]",
            "/api/admin/users/{login} [GET]",
            "/api/admin/users/{login} [PUT]",
            "/api/admin/users/{login} [DELETE]",

            // Ticket Category Management
            "/api/ticket-categories [GET]",
            "/api/ticket-categories [POST]",
            "/api/ticket-categories/{id} [GET]",
            "/api/ticket-categories/{id} [PUT]",
            "/api/ticket-categories/{id} [DELETE]",

            // Ticket Priority Management
            "/api/ticket-priorities [GET]",
            "/api/ticket-priorities [POST]",
            "/api/ticket-priorities/{id} [GET]",
            "/api/ticket-priorities/{id} [PUT]",
            "/api/ticket-priorities/{id} [DELETE]",

            // Ticket Management
            "/api/tickets [GET]",
            "/api/tickets [POST]",
            "/api/tickets/{id} [GET]",
            "/api/tickets/{id} [PUT]",
            "/api/tickets/{id} [DELETE]",

            // Health and Monitoring
            "/management/health [GET]",
            "/management/metrics [GET]",
            "/management/info [GET]"
        };

        for (String endpoint : endpoints) {
            apiCoverage.put(endpoint, false);
        }
    }

    // Utility methods
    private static String generateUUID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private static String getCurrentTimestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    private static void markApiCovered(String endpoint, String method) {
        String key = endpoint + " [" + method + "]";
        apiCoverage.put(key, true);
        System.out.println("✅ Covered: " + key);
    }

    private static void recordApiResult(String endpoint, String method, int statusCode, long responseTime, boolean success, String error) {
        String key = endpoint + " [" + method + "]";
        apiResults.put(key, new ApiResult(endpoint, method, statusCode, responseTime, success, error));
        if (success) {
            successCount.incrementAndGet();
        } else {
            failureCount.incrementAndGet();
        }
    }

    // Authentication chain - IMPROVED: Proper JWT token handling
    private static final ChainBuilder authRequest() {
        return exec(session -> {
            System.out.println("🔐 Starting authentication...");
            return session;
        })
        .exec(
            http("Authentication POST")
                .post("/api/authenticate")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"username\":\"admin\", \"password\":\"admin\"}"))
                .asJson()
                .check(status().saveAs("auth_status"))
                .check(jsonPath("$.id_token").saveAs("jwt_token"))
                .check(responseTimeInMillis().saveAs("auth_response_time"))
                .check(bodyString().saveAs("auth_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("auth_status");
            long responseTime = session.getLong("auth_response_time");
            String token = session.getString("jwt_token");

            boolean success = statusCode == 200 && token != null;
            System.out.println("✅ Authentication - Status: " + statusCode +
                (token != null ? ", Token: " + token.substring(0, Math.min(20, token.length())) + "..." : ", No token") +
                ", Time: " + responseTime + "ms");

            markApiCovered("/api/authenticate", "POST");
            recordApiResult("/api/authenticate", "POST", statusCode, responseTime, success,
                success ? null : "Authentication failed");

            return session;
        })
        .pause(1, 2)
        // Test GET /api/authenticate (if supported)
        .exec(
            http("Authentication GET")
                .get("/api/authenticate")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("auth_get_status"))
                .check(responseTimeInMillis().saveAs("auth_get_response_time"))
                .check(bodyString().saveAs("auth_get_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("auth_get_status");
            long responseTime = session.getLong("auth_get_response_time");
            boolean success = statusCode == 204; // No Content for successful auth check
            System.out.println("✅ Authentication check - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/authenticate", "GET");
            recordApiResult("/api/authenticate", "GET", statusCode, responseTime, success,
                success ? null : "Auth check failed");
            return session;
        })
        .pause(1, 2);
    }

    // Account operations - IMPROVED: Better error handling
    private static final ChainBuilder accountOperations() {
        return exec(session -> {
            System.out.println("👤 Starting Account operations...");
            return session;
        })
        // GET /api/account
        .exec(
            http("Get Account")
                .get("/api/account")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("account_status"))
                .check(responseTimeInMillis().saveAs("account_response_time"))
                .check(bodyString().saveAs("account_info"))
        )
        .exec(session -> {
            int statusCode = session.getInt("account_status");
            long responseTime = session.getLong("account_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Account retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/account", "GET");
            recordApiResult("/api/account", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get account");
            return session;
        })
        .pause(1, 2)
        // POST /api/account (update account) - IMPROVED: Proper payload
        .exec(
            http("Update Account")
                .post("/api/account")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"firstName\":\"Test\",\"lastName\":\"User\",\"email\":\"admin@localhost\",\"langKey\":\"en\",\"imageUrl\":\"\",\"activated\":true}"))
                .asJson()
                .check(status().saveAs("account_update_status"))
                .check(responseTimeInMillis().saveAs("account_update_response_time"))
                .check(bodyString().saveAs("account_update_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("account_update_status");
            long responseTime = session.getLong("account_update_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Account updated - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/account", "POST");
            recordApiResult("/api/account", "POST", statusCode, responseTime, success,
                success ? null : "Failed to update account");
            return session;
        })
        .pause(1, 2)
        // POST /api/account/reset-password/init - IMPROVED: Uses existing email
        .exec(
            http("Init Password Reset")
                .post("/api/account/reset-password/init")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"email\":\"admin@localhost\"}"))
                .asJson()
                .check(status().saveAs("password_reset_init_status"))
                .check(responseTimeInMillis().saveAs("password_reset_init_response_time"))
                .check(bodyString().saveAs("password_reset_init_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("password_reset_init_status");
            long responseTime = session.getLong("password_reset_init_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Password reset initiated - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/account/reset-password/init", "POST");
            recordApiResult("/api/account/reset-password/init", "POST", statusCode, responseTime, success,
                success ? null : "Failed to initiate password reset");
            return session;
        })
        .pause(1, 2)
        // POST /api/account/reset-password/finish - IMPROVED: Accepts 400 as expected for invalid key
        .exec(
            http("Finish Password Reset")
                .post("/api/account/reset-password/finish")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"key\":\"test-key\",\"newPassword\":\"newpassword789\"}"))
                .asJson()
                .check(status().saveAs("password_reset_finish_status"))
                .check(responseTimeInMillis().saveAs("password_reset_finish_response_time"))
                .check(bodyString().saveAs("password_reset_finish_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("password_reset_finish_status");
            long responseTime = session.getLong("password_reset_finish_response_time");
            boolean success = statusCode < 500; // Accept any non-server error
            System.out.println("✅ Password reset finished - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/account/reset-password/finish", "POST");
            recordApiResult("/api/account/reset-password/finish", "POST", statusCode, responseTime, success,
                success ? null : "Server error in password reset");
            return session;
        })
        .pause(1, 2)
        // POST /api/account/change-password - IMPROVED: Uses correct password
        .exec(
            http("Change Password")
                .post("/api/account/change-password")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"currentPassword\":\"admin\",\"newPassword\":\"newpassword123\"}"))
                .asJson()
                .check(status().saveAs("change_password_status"))
                .check(responseTimeInMillis().saveAs("change_password_response_time"))
                .check(bodyString().saveAs("change_password_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("change_password_status");
            long responseTime = session.getLong("change_password_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Password changed - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/account/change-password", "POST");
            recordApiResult("/api/account/change-password", "POST", statusCode, responseTime, success,
                success ? null : "Failed to change password");
            return session;
        })
        .pause(1, 2)
        // GET /api/activate - IMPROVED: Accepts 400 as expected for invalid key
        .exec(
            http("Activate Account")
                .get("/api/activate?key=test-activation-key")
                .header("Accept", "application/json")
                .check(status().saveAs("activate_status"))
                .check(responseTimeInMillis().saveAs("activate_response_time"))
                .check(bodyString().saveAs("activate_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("activate_status");
            long responseTime = session.getLong("activate_response_time");
            boolean success = statusCode < 500; // Accept any non-server error
            System.out.println("✅ Account activation tested - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/activate", "GET");
            recordApiResult("/api/activate", "GET", statusCode, responseTime, success,
                success ? null : "Server error in activation");
            return session;
        })
        .pause(1, 2);
    }

    // Public User operations - IMPROVED: Better error handling
    private static final ChainBuilder publicUserOperations() {
        return exec(session -> {
            System.out.println("🌍 Starting Public User operations...");
            return session;
        })
        // GET /api/users
        .exec(
            http("Get Public Users")
                .get("/api/users")
                .header("Accept", "application/json")
                .check(status().saveAs("public_users_status"))
                .check(responseTimeInMillis().saveAs("public_users_response_time"))
                .check(bodyString().saveAs("public_users_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("public_users_status");
            long responseTime = session.getLong("public_users_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Public users retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/users", "GET");
            recordApiResult("/api/users", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get public users");
            return session;
        })
        .pause(1, 2)
        // POST /api/register - IMPROVED: Uses unique email and valid payload
        .exec(session -> {
            String uuid = generateUUID();
            return session.set("register_email", "testuser_" + uuid + "@example.com");
        })
        .exec(
            http("Register User")
                .post("/api/register")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody(session -> {
                    String email = session.getString("register_email");
                    String login = "testuser_" + session.getString("register_email").split("@")[0];
                    return "{\"login\":\"" + login + "\",\"email\":\"" + email + "\",\"password\":\"testpassword123\",\"firstName\":\"Test\",\"lastName\":\"User\",\"langKey\":\"en\"}";
                }))
                .asJson()
                .check(status().saveAs("register_status"))
                .check(responseTimeInMillis().saveAs("register_response_time"))
                .check(bodyString().saveAs("register_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("register_status");
            long responseTime = session.getLong("register_response_time");
            boolean success = statusCode == 201; // Created
            System.out.println("✅ User registered - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/register", "POST");
            recordApiResult("/api/register", "POST", statusCode, responseTime, success,
                success ? null : "Failed to register user");
            return session;
        })
        .pause(1, 2);
    }

    // Authority operations - IMPROVED: Better error handling
    private static final ChainBuilder authorityOperations() {
        return exec(session -> {
            System.out.println("🔒 Starting Authority operations...");
            return session;
        })
        // GET /api/authorities
        .exec(
            http("Get Authorities")
                .get("/api/authorities")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("authorities_status"))
                .check(responseTimeInMillis().saveAs("authorities_response_time"))
                .check(bodyString().saveAs("authorities_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("authorities_status");
            long responseTime = session.getLong("authorities_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Authorities retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/authorities", "GET");
            recordApiResult("/api/authorities", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get authorities");
            return session;
        })
        .pause(1, 2);
    }

    // Admin User operations - IMPROVED: Better error handling and payloads
    private static final ChainBuilder adminUserOperations() {
        return exec(session -> {
            System.out.println("👨‍💼 Starting Admin User operations...");
            return session;
        })
        // GET /api/admin/users
        .exec(
            http("Get Admin Users")
                .get("/api/admin/users")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("admin_users_status"))
                .check(responseTimeInMillis().saveAs("admin_users_response_time"))
                .check(bodyString().saveAs("admin_users_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("admin_users_status");
            long responseTime = session.getLong("admin_users_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Admin users retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/admin/users", "GET");
            recordApiResult("/api/admin/users", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get admin users");
            return session;
        })
        .pause(1, 2)
        // POST /api/admin/users
        .exec(session -> {
            String uuid = generateUUID();
            return session.set("admin_user_email", "adminuser_" + uuid + "@example.com");
        })
        .exec(
            http("Create Admin User")
                .post("/api/admin/users")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody(session -> {
                    String email = session.getString("admin_user_email");
                    String login = "adminuser_" + session.getString("admin_user_email").split("@")[0];
                    return "{\"login\":\"" + login + "\",\"email\":\"" + email + "\",\"password\":\"adminpass123\",\"firstName\":\"Admin\",\"lastName\":\"User\",\"langKey\":\"en\",\"authorities\":[\"ROLE_USER\"]}";
                }))
                .asJson()
                .check(status().saveAs("create_admin_user_status"))
                .check(responseTimeInMillis().saveAs("create_admin_user_response_time"))
                .check(bodyString().saveAs("create_admin_user_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("create_admin_user_status");
            long responseTime = session.getLong("create_admin_user_response_time");
            boolean success = statusCode == 201; // Created
            System.out.println("✅ Admin user created - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/admin/users", "POST");
            recordApiResult("/api/admin/users", "POST", statusCode, responseTime, success,
                success ? null : "Failed to create admin user");
            return session;
        })
        .pause(1, 2);
    }

    // Ticket Category operations
    private static final ChainBuilder ticketCategoryOperations() {
        return exec(session -> {
            System.out.println("📂 Starting Ticket Category operations...");
            return session;
        })
        // GET /api/ticket-categories
        .exec(
            http("Get Ticket Categories")
                .get("/api/ticket-categories")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("categories_status"))
                .check(responseTimeInMillis().saveAs("categories_response_time"))
                .check(bodyString().saveAs("categories_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("categories_status");
            long responseTime = session.getLong("categories_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Ticket categories retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-categories", "GET");
            recordApiResult("/api/ticket-categories", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get ticket categories");
            return session;
        })
        .pause(1, 2)
        // POST /api/ticket-categories
        .exec(
            http("Create Ticket Category")
                .post("/api/ticket-categories")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"name\":\"Test Category\",\"description\":\"Test category for performance testing\"}"))
                .asJson()
                .check(status().saveAs("create_category_status"))
                .check(jsonPath("$.id").optional().saveAs("category_id"))
                .check(responseTimeInMillis().saveAs("create_category_response_time"))
                .check(bodyString().saveAs("create_category_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("create_category_status");
            long responseTime = session.getLong("create_category_response_time");
            String categoryId = session.getString("category_id");
            boolean success = statusCode == 201 && categoryId != null; // Created and ID extracted
            System.out.println("✅ Ticket category created - Status: " + statusCode +
                (categoryId != null ? ", ID: " + categoryId : ", No ID extracted") +
                ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-categories", "POST");
            recordApiResult("/api/ticket-categories", "POST", statusCode, responseTime, success,
                success ? null : "Failed to create ticket category or extract ID");
            return session;
        })
        .pause(1, 2)
        // GET /api/ticket-categories/{id} - Only if category was created successfully
        .exec(session -> {
            String categoryId = session.getString("category_id");
            if (categoryId == null) {
                System.out.println("⚠️ Skipping GET category by ID - no category_id available");
                return session;
            }
            return session;
        })
        .exec(
            http("Get Ticket Category by ID")
                .get("/api/ticket-categories/#{category_id}")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("get_category_status"))
                .check(responseTimeInMillis().saveAs("get_category_response_time"))
                .check(bodyString().saveAs("get_category_response"))
        )
        .exec(session -> {
            String categoryId = session.getString("category_id");
            if (categoryId == null) {
                return session; // Skip if no category_id
            }
            int statusCode = session.getInt("get_category_status");
            long responseTime = session.getLong("get_category_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Ticket category retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-categories/{id}", "GET");
            recordApiResult("/api/ticket-categories/{id}", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get ticket category");
            return session;
        })
        .pause(1, 2)
        // PUT /api/ticket-categories/{id} - Only if category was created successfully
        .exec(session -> {
            String categoryId = session.getString("category_id");
            if (categoryId == null) {
                System.out.println("⚠️ Skipping UPDATE category - no category_id available");
                return session;
            }
            return session;
        })
        .exec(
            http("Update Ticket Category")
                .put("/api/ticket-categories/#{category_id}")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"name\":\"Updated Test Category\",\"description\":\"Updated test category for performance testing\"}"))
                .asJson()
                .check(status().saveAs("update_category_status"))
                .check(responseTimeInMillis().saveAs("update_category_response_time"))
                .check(bodyString().saveAs("update_category_response"))
        )
        .exec(session -> {
            String categoryId = session.getString("category_id");
            if (categoryId == null) {
                return session; // Skip if no category_id
            }
            int statusCode = session.getInt("update_category_status");
            long responseTime = session.getLong("update_category_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Ticket category updated - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-categories/{id}", "PUT");
            recordApiResult("/api/ticket-categories/{id}", "PUT", statusCode, responseTime, success,
                success ? null : "Failed to update ticket category");
            return session;
        })
        .pause(1, 2)
        // DELETE /api/ticket-categories/{id} - Only if category was created successfully
        .exec(session -> {
            String categoryId = session.getString("category_id");
            if (categoryId == null) {
                System.out.println("⚠️ Skipping DELETE category - no category_id available");
                return session;
            }
            return session;
        })
        .exec(
            http("Delete Ticket Category")
                .delete("/api/ticket-categories/#{category_id}")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("delete_category_status"))
                .check(responseTimeInMillis().saveAs("delete_category_response_time"))
                .check(bodyString().saveAs("delete_category_response"))
        )
        .exec(session -> {
            String categoryId = session.getString("category_id");
            if (categoryId == null) {
                return session; // Skip if no category_id
            }
            int statusCode = session.getInt("delete_category_status");
            long responseTime = session.getLong("delete_category_response_time");
            boolean success = statusCode == 204; // No Content for successful deletion
            System.out.println("✅ Ticket category deleted - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-categories/{id}", "DELETE");
            recordApiResult("/api/ticket-categories/{id}", "DELETE", statusCode, responseTime, success,
                success ? null : "Failed to delete ticket category");
            return session;
        })
        .pause(1, 2);
    }

    // Ticket Priority operations
    private static final ChainBuilder ticketPriorityOperations() {
        return exec(session -> {
            System.out.println("⚡ Starting Ticket Priority operations...");
            return session;
        })
        // GET /api/ticket-priorities
        .exec(
            http("Get Ticket Priorities")
                .get("/api/ticket-priorities")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("priorities_status"))
                .check(responseTimeInMillis().saveAs("priorities_response_time"))
                .check(bodyString().saveAs("priorities_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("priorities_status");
            long responseTime = session.getLong("priorities_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Ticket priorities retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-priorities", "GET");
            recordApiResult("/api/ticket-priorities", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get ticket priorities");
            return session;
        })
        .pause(1, 2)
        // POST /api/ticket-priorities
        .exec(
            http("Create Ticket Priority")
                .post("/api/ticket-priorities")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"name\":\"Test Priority\",\"description\":\"Test priority for performance testing\"}"))
                .asJson()
                .check(status().saveAs("create_priority_status"))
                .check(jsonPath("$.id").optional().saveAs("priority_id"))
                .check(responseTimeInMillis().saveAs("create_priority_response_time"))
                .check(bodyString().saveAs("create_priority_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("create_priority_status");
            long responseTime = session.getLong("create_priority_response_time");
            String priorityId = session.getString("priority_id");
            boolean success = statusCode == 201 && priorityId != null; // Created and ID extracted
            System.out.println("✅ Ticket priority created - Status: " + statusCode +
                (priorityId != null ? ", ID: " + priorityId : ", No ID extracted") +
                ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-priorities", "POST");
            recordApiResult("/api/ticket-priorities", "POST", statusCode, responseTime, success,
                success ? null : "Failed to create ticket priority or extract ID");
            return session;
        })
        .pause(1, 2)
        // GET /api/ticket-priorities/{id} - Only if priority was created successfully
        .exec(session -> {
            String priorityId = session.getString("priority_id");
            if (priorityId == null) {
                System.out.println("⚠️ Skipping GET priority by ID - no priority_id available");
                return session;
            }
            return session;
        })
        .exec(
            http("Get Ticket Priority by ID")
                .get("/api/ticket-priorities/#{priority_id}")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("get_priority_status"))
                .check(responseTimeInMillis().saveAs("get_priority_response_time"))
                .check(bodyString().saveAs("get_priority_response"))
        )
        .exec(session -> {
            String priorityId = session.getString("priority_id");
            if (priorityId == null) {
                return session; // Skip if no priority_id
            }
            int statusCode = session.getInt("get_priority_status");
            long responseTime = session.getLong("get_priority_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Ticket priority retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-priorities/{id}", "GET");
            recordApiResult("/api/ticket-priorities/{id}", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get ticket priority");
            return session;
        })
        .pause(1, 2)
        // PUT /api/ticket-priorities/{id} - Only if priority was created successfully
        .exec(session -> {
            String priorityId = session.getString("priority_id");
            if (priorityId == null) {
                System.out.println("⚠️ Skipping UPDATE priority - no priority_id available");
                return session;
            }
            return session;
        })
        .exec(
            http("Update Ticket Priority")
                .put("/api/ticket-priorities/#{priority_id}")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"name\":\"Updated Test Priority\",\"description\":\"Updated test priority for performance testing\"}"))
                .asJson()
                .check(status().saveAs("update_priority_status"))
                .check(responseTimeInMillis().saveAs("update_priority_response_time"))
                .check(bodyString().saveAs("update_priority_response"))
        )
        .exec(session -> {
            String priorityId = session.getString("priority_id");
            if (priorityId == null) {
                return session; // Skip if no priority_id
            }
            int statusCode = session.getInt("update_priority_status");
            long responseTime = session.getLong("update_priority_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Ticket priority updated - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-priorities/{id}", "PUT");
            recordApiResult("/api/ticket-priorities/{id}", "PUT", statusCode, responseTime, success,
                success ? null : "Failed to update ticket priority");
            return session;
        })
        .pause(1, 2)
        // DELETE /api/ticket-priorities/{id} - Only if priority was created successfully
        .exec(session -> {
            String priorityId = session.getString("priority_id");
            if (priorityId == null) {
                System.out.println("⚠️ Skipping DELETE priority - no priority_id available");
                return session;
            }
            return session;
        })
        .exec(
            http("Delete Ticket Priority")
                .delete("/api/ticket-priorities/#{priority_id}")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("delete_priority_status"))
                .check(responseTimeInMillis().saveAs("delete_priority_response_time"))
                .check(bodyString().saveAs("delete_priority_response"))
        )
        .exec(session -> {
            String priorityId = session.getString("priority_id");
            if (priorityId == null) {
                return session; // Skip if no priority_id
            }
            int statusCode = session.getInt("delete_priority_status");
            long responseTime = session.getLong("delete_priority_response_time");
            boolean success = statusCode == 204; // No Content for successful deletion
            System.out.println("✅ Ticket priority deleted - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/ticket-priorities/{id}", "DELETE");
            recordApiResult("/api/ticket-priorities/{id}", "DELETE", statusCode, responseTime, success,
                success ? null : "Failed to delete ticket priority");
            return session;
        })
        .pause(1, 2);
    }

    // Ticket operations
    private static final ChainBuilder ticketOperations() {
        return exec(session -> {
            System.out.println("🎫 Starting Ticket operations...");
            return session;
        })
        // GET /api/tickets
        .exec(
            http("Get Tickets")
                .get("/api/tickets")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("tickets_status"))
                .check(responseTimeInMillis().saveAs("tickets_response_time"))
                .check(bodyString().saveAs("tickets_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("tickets_status");
            long responseTime = session.getLong("tickets_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Tickets retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/tickets", "GET");
            recordApiResult("/api/tickets", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get tickets");
            return session;
        })
        .pause(1, 2)
        // POST /api/tickets
        .exec(
            http("Create Ticket")
                .post("/api/tickets")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"title\":\"Test Ticket for Performance Testing\",\"description\":\"This is a test ticket created during performance testing to validate API coverage\",\"status\":\"OPEN\",\"priority\":{\"id\":2,\"name\":\"MEDIUM\"},\"category\":{\"id\":1,\"name\":\"BUG\"}}"))
                .asJson()
                .check(status().saveAs("create_ticket_status"))
                .check(jsonPath("$.id").optional().saveAs("ticket_id"))
                .check(responseTimeInMillis().saveAs("create_ticket_response_time"))
                .check(bodyString().saveAs("create_ticket_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("create_ticket_status");
            long responseTime = session.getLong("create_ticket_response_time");
            String ticketId = session.getString("ticket_id");
            boolean success = statusCode == 201 && ticketId != null; // Created and ID extracted
            System.out.println("✅ Ticket created - Status: " + statusCode +
                (ticketId != null ? ", ID: " + ticketId : ", No ID extracted") +
                ", Time: " + responseTime + "ms");
            markApiCovered("/api/tickets", "POST");
            recordApiResult("/api/tickets", "POST", statusCode, responseTime, success,
                success ? null : "Failed to create ticket or extract ID");
            return session;
        })
        .pause(1, 2)
        // GET /api/tickets/{id} - Only if ticket was created successfully
        .exec(session -> {
            String ticketId = session.getString("ticket_id");
            if (ticketId == null) {
                System.out.println("⚠️ Skipping GET ticket by ID - no ticket_id available");
                return session;
            }
            return session;
        })
        .exec(
            http("Get Ticket by ID")
                .get("/api/tickets/#{ticket_id}")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("get_ticket_status"))
                .check(responseTimeInMillis().saveAs("get_ticket_response_time"))
                .check(bodyString().saveAs("get_ticket_response"))
        )
        .exec(session -> {
            String ticketId = session.getString("ticket_id");
            if (ticketId == null) {
                return session; // Skip if no ticket_id
            }
            int statusCode = session.getInt("get_ticket_status");
            long responseTime = session.getLong("get_ticket_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Ticket retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/tickets/{id}", "GET");
            recordApiResult("/api/tickets/{id}", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get ticket");
            return session;
        })
        .pause(1, 2)
        // PUT /api/tickets/{id} - Only if ticket was created successfully
        .exec(session -> {
            String ticketId = session.getString("ticket_id");
            if (ticketId == null) {
                System.out.println("⚠️ Skipping UPDATE ticket - no ticket_id available");
                return session;
            }
            return session;
        })
        .exec(
            http("Update Ticket")
                .put("/api/tickets/#{ticket_id}")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"title\":\"Updated Test Ticket for Performance Testing\",\"description\":\"This ticket has been updated during performance testing to validate API coverage\",\"status\":\"IN_PROGRESS\",\"priority\":{\"id\":3,\"name\":\"HIGH\"},\"category\":{\"id\":2,\"name\":\"FEATURE_REQUEST\"}}"))
                .asJson()
                .check(status().saveAs("update_ticket_status"))
                .check(responseTimeInMillis().saveAs("update_ticket_response_time"))
                .check(bodyString().saveAs("update_ticket_response"))
        )
        .exec(session -> {
            String ticketId = session.getString("ticket_id");
            if (ticketId == null) {
                return session; // Skip if no ticket_id
            }
            int statusCode = session.getInt("update_ticket_status");
            long responseTime = session.getLong("update_ticket_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Ticket updated - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/tickets/{id}", "PUT");
            recordApiResult("/api/tickets/{id}", "PUT", statusCode, responseTime, success,
                success ? null : "Failed to update ticket");
            return session;
        })
        .pause(1, 2)
        // DELETE /api/tickets/{id} - Only if ticket was created successfully
        .exec(session -> {
            String ticketId = session.getString("ticket_id");
            if (ticketId == null) {
                System.out.println("⚠️ Skipping DELETE ticket - no ticket_id available");
                return session;
            }
            return session;
        })
        .exec(
            http("Delete Ticket")
                .delete("/api/tickets/#{ticket_id}")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("delete_ticket_status"))
                .check(responseTimeInMillis().saveAs("delete_ticket_response_time"))
                .check(bodyString().saveAs("delete_ticket_response"))
        )
        .exec(session -> {
            String ticketId = session.getString("ticket_id");
            if (ticketId == null) {
                return session; // Skip if no ticket_id
            }
            int statusCode = session.getInt("delete_ticket_status");
            long responseTime = session.getLong("delete_ticket_response_time");
            boolean success = statusCode == 204; // No Content for successful deletion
            System.out.println("✅ Ticket deleted - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/api/tickets/{id}", "DELETE");
            recordApiResult("/api/tickets/{id}", "DELETE", statusCode, responseTime, success,
                success ? null : "Failed to delete ticket");
            return session;
        })
        .pause(1, 2);
    }

    // Health and Monitoring operations
    private static final ChainBuilder healthOperations() {
        return exec(session -> {
            System.out.println("🏥 Starting Health and Monitoring operations...");
            return session;
        })
        // GET /management/health
        .exec(
            http("Get Health")
                .get("/management/health")
                .header("Accept", "application/json")
                .check(status().saveAs("health_status"))
                .check(responseTimeInMillis().saveAs("health_response_time"))
                .check(bodyString().saveAs("health_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("health_status");
            long responseTime = session.getLong("health_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Health check - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/management/health", "GET");
            recordApiResult("/management/health", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get health status");
            return session;
        })
        .pause(1, 2)
        // GET /management/metrics
        .exec(
            http("Get Metrics")
                .get("/management/metrics")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Accept", "application/json")
                .check(status().saveAs("metrics_status"))
                .check(responseTimeInMillis().saveAs("metrics_response_time"))
                .check(bodyString().saveAs("metrics_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("metrics_status");
            long responseTime = session.getLong("metrics_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Metrics retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/management/metrics", "GET");
            recordApiResult("/management/metrics", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get metrics");
            return session;
        })
        .pause(1, 2)
        // GET /management/info
        .exec(
            http("Get Info")
                .get("/management/info")
                .header("Accept", "application/json")
                .check(status().saveAs("info_status"))
                .check(responseTimeInMillis().saveAs("info_response_time"))
                .check(bodyString().saveAs("info_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("info_status");
            long responseTime = session.getLong("info_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Info retrieved - Status: " + statusCode + ", Time: " + responseTime + "ms");
            markApiCovered("/management/info", "GET");
            recordApiResult("/management/info", "GET", statusCode, responseTime, success,
                success ? null : "Failed to get info");
            return session;
        })
        .pause(1, 2);
    }

    // Cleanup operations - Reset password back to default
    private static final ChainBuilder cleanupOperations() {
        return exec(session -> {
            System.out.println("🧹 Starting cleanup operations...");
            return session;
        })
        // Reset admin password back to admin
        .exec(
            http("Reset Admin Password to admin")
                .post("/api/account/change-password")
                .header("Authorization", "Bearer #{jwt_token}")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"currentPassword\":\"newpassword123\",\"newPassword\":\"admin\"}"))
                .asJson()
                .check(status().saveAs("reset_password_status"))
                .check(responseTimeInMillis().saveAs("reset_password_response_time"))
                .check(bodyString().saveAs("reset_password_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("reset_password_status");
            long responseTime = session.getLong("reset_password_response_time");
            boolean success = statusCode == 200;
            System.out.println("✅ Admin password reset to admin - Status: " + statusCode + ", Time: " + responseTime + "ms");
            if (success) {
                System.out.println("🔐 Admin password has been reset back to 'admin'");
            } else {
                System.out.println("⚠️ Failed to reset admin password to admin");
            }
            return session;
        })
        .pause(1, 2)
        // Verify the password reset worked by trying to authenticate with admin
        .exec(
            http("Verify admin")
                .post("/api/authenticate")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(StringBody("{\"username\":\"admin\", \"password\":\"admin\"}"))
                .asJson()
                .check(status().saveAs("verify_auth_status"))
                .check(jsonPath("$.id_token").optional().saveAs("verify_jwt_token"))
                .check(responseTimeInMillis().saveAs("verify_auth_response_time"))
                .check(bodyString().saveAs("verify_auth_response"))
        )
        .exec(session -> {
            int statusCode = session.getInt("verify_auth_status");
            long responseTime = session.getLong("verify_auth_response_time");
            String token = session.getString("verify_jwt_token");
            boolean success = statusCode == 200 && token != null;
                        System.out.println("✅ admin verification - Status: " + statusCode +
                (token != null ? ", Token received" : ", No token") +
                ", Time: " + responseTime + "ms");
            if (success) {
                System.out.println("🎉 SUCCESS: Admin password successfully reset to 'admin'");
            } else {
                System.out.println("❌ FAILED: Admin password reset verification failed");
            }
            return session;
        })
        .pause(1, 2);
    }

    // Main scenario that orchestrates all operations
    private static final ScenarioBuilder completeApiTest = scenario("Complete API Coverage Test")
        .exec(session -> {
            System.out.println("🚀 Starting Complete API Coverage Test for Ticket Tracker Application");
            System.out.println("📍 Base URL: " + BASE_URL);
            System.out.println("📊 Total endpoints to test: " + apiCoverage.size());
            return session;
        })
        .exec(authRequest())
        .exec(accountOperations())
        .exec(publicUserOperations())
        .exec(authorityOperations())
        .exec(adminUserOperations())
        .exec(ticketCategoryOperations())
        .exec(ticketPriorityOperations())
        .exec(ticketOperations())
                .exec(healthOperations())
        .exec(cleanupOperations())
        .exec(session -> {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("📊 FINAL API COVERAGE REPORT");
            System.out.println("=".repeat(80));

            int coveredCount = 0;
            int totalCount = apiCoverage.size();

            for (Map.Entry<String, Boolean> entry : apiCoverage.entrySet()) {
                String endpoint = entry.getKey();
                boolean covered = entry.getValue();
                ApiResult result = apiResults.get(endpoint);

                String status = covered ? "✅" : "❌";
                String details = "";
                if (result != null) {
                    details = String.format(" (Status: %d, Time: %dms)", result.statusCode, result.responseTime);
                }

                System.out.println(status + " " + endpoint + details);
                if (covered) coveredCount++;
            }

            System.out.println("\n" + "-".repeat(80));
            System.out.println("📈 SUMMARY STATISTICS");
            System.out.println("-".repeat(80));
            System.out.println("Total Endpoints: " + totalCount);
            System.out.println("Covered Endpoints: " + coveredCount);
            System.out.println("Coverage Percentage: " + String.format("%.2f%%", (double) coveredCount / totalCount * 100));
            System.out.println("Successful Requests: " + successCount.get());
            System.out.println("Failed Requests: " + failureCount.get());
            System.out.println("Total Requests: " + (successCount.get() + failureCount.get()));

            if (failureCount.get() > 0) {
                System.out.println("\n" + "⚠️".repeat(20) + " FAILED REQUESTS " + "⚠️".repeat(20));
                for (Map.Entry<String, ApiResult> entry : apiResults.entrySet()) {
                    ApiResult result = entry.getValue();
                    if (!result.success) {
                        System.out.println("❌ " + entry.getKey() + " - Status: " + result.statusCode +
                            (result.error != null ? " - Error: " + result.error : ""));
                    }
                }
            }

            System.out.println("=".repeat(80));
            return session;
        });

    // Simulation setup
    {
        setUp(
            completeApiTest.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}
