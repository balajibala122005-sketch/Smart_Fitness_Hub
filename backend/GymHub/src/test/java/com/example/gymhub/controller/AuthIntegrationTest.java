package com.example.gymhub.controller;

import com.example.gymhub.dto.LoginRequest;
import com.example.gymhub.dto.RegisterRequest;
import com.example.gymhub.entity.User;
import com.example.gymhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void test1_RegisterSuccess() throws Exception {
        String uniqueEmail = "reg_test_" + System.currentTimeMillis() + "@gymhub.com";
        RegisterRequest request = new RegisterRequest(
                "Test Athlete",
                uniqueEmail,
                "StrongPassword123",
                25,
                175.0,
                70.0,
                "muscle",
                "user"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(uniqueEmail))
                .andExpect(jsonPath("$.user.name").value("Test Athlete"))
                .andExpect(jsonPath("$.user.role").value("USER"));
    }

    @Test
    void test2_DuplicateEmailRegistrationReturnsConflict() throws Exception {
        String duplicateEmail = "duplicate_" + System.currentTimeMillis() + "@gymhub.com";
        RegisterRequest request = new RegisterRequest(
                "First User",
                duplicateEmail,
                "Password123",
                28,
                180.0,
                75.0,
                "muscle",
                "user"
        );

        // First registration: 201 Created
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second registration with same email: 409 Conflict
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void test3_PasswordIsStoredAsBcryptHashNotPlaintext() throws Exception {
        String rawPassword = "MySuperSecretPassword!";
        String email = "bcrypt_test_" + System.currentTimeMillis() + "@gymhub.com";
        RegisterRequest request = new RegisterRequest(
                "Security Tester",
                email,
                rawPassword,
                30,
                182.0,
                80.0,
                "weight_loss",
                "user"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        User savedUser = userRepository.findByEmail(email).orElse(null);
        assertNotNull(savedUser, "Saved user must exist in database");

        // Verify password is NOT plaintext
        assertNotEquals(rawPassword, savedUser.getPassword(), "Database password must not be stored in plaintext");

        // Verify BCrypt hash signature
        assertTrue(savedUser.getPassword().startsWith("$2a$") || savedUser.getPassword().startsWith("$2b$") || savedUser.getPassword().startsWith("$2y$"),
                "Password must have BCrypt salt and hash prefix");

        // Verify BCrypt match
        assertTrue(passwordEncoder.matches(rawPassword, savedUser.getPassword()), "BCrypt encoder must match raw password with hash");
    }

    @Test
    void test4_LoginSuccess() throws Exception {
        String email = "login_success_" + System.currentTimeMillis() + "@gymhub.com";
        String password = "LoginPass123";

        RegisterRequest regRequest = new RegisterRequest(
                "Login User",
                email,
                password,
                24,
                170.0,
                65.0,
                "muscle",
                "user"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(email, password, "user");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.name").value("Login User"));
    }

    @Test
    void test5_IncorrectCredentialsReturnHttp401() throws Exception {
        String email = "wrong_pass_" + System.currentTimeMillis() + "@gymhub.com";
        RegisterRequest regRequest = new RegisterRequest(
                "Valid User",
                email,
                "CorrectPassword123",
                26,
                175.0,
                70.0,
                "muscle",
                "user"
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        // Incorrect password
        LoginRequest badPasswordRequest = new LoginRequest(email, "WrongPassword!");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badPasswordRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));

        // Non-existent email
        LoginRequest nonExistentEmailRequest = new LoginRequest("unknown_" + System.currentTimeMillis() + "@gymhub.com", "SomePass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentEmailRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void test6_ProtectedEndpointRejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test7_ProtectedEndpointAcceptsValidJwt() throws Exception {
        String email = "jwt_user_" + System.currentTimeMillis() + "@gymhub.com";
        String password = "JwtPassword123";

        RegisterRequest regRequest = new RegisterRequest(
                "JWT Athlete",
                email,
                password,
                29,
                178.0,
                76.0,
                "muscle",
                "user"
        );

        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseJson = regResult.getResponse().getContentAsString();
        // Parse token
        tools.jackson.databind.JsonNode rootNode = objectMapper.readTree(responseJson);
        String token = rootNode.get("token").asText();

        assertNotNull(token);

        // Access protected endpoint with Bearer token
        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("JWT Athlete"));
    }

    @Test
    void test8_HealthEndpointRemainsPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("Gym Hub Backend"));
    }
}
