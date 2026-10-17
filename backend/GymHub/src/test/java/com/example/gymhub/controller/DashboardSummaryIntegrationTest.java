package com.example.gymhub.controller;

import com.example.gymhub.dto.RegisterRequest;
import com.example.gymhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DashboardSummaryIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private String registerAndGetToken(String name, String email, String password, Integer age, Double height, Double weight, String goal) throws Exception {
        RegisterRequest request = new RegisterRequest(name, email, password, age, height, weight, goal, "user");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("token").asText();
    }

    @Test
    void test1_DashboardSummaryRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/user/dashboard-summary"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test2_ValidJwtRetrievesCorrectDashboardSummary() throws Exception {
        String email = "summary_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Titan Alpha", email, "TitanPass123", 27, 180.0, 81.0, "muscle");

        MvcResult result = mockMvc.perform(get("/api/user/dashboard-summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Titan Alpha"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.height").value(180.0))
                .andExpect(jsonPath("$.weight").value(81.0))
                .andExpect(jsonPath("$.fitnessGoal").value("muscle"))
                .andExpect(jsonPath("$.totalWorkouts").value(0))
                .andExpect(jsonPath("$.bmi").value(25.0))
                .andExpect(jsonPath("$.bmiStatus").value("Above Baseline"))
                .andReturn();

        // Verify response contains NO password or password hash
        String responseContent = result.getResponse().getContentAsString();
        assertFalse(responseContent.toLowerCase().contains("password"), "Response must not contain password or password hash");
    }

    @Test
    void test3_UserCannotRetrieveAnotherUsersData() throws Exception {
        String emailUserA = "user_a_" + System.currentTimeMillis() + "@gymhub.com";
        String tokenUserA = registerAndGetToken("User Alpha", emailUserA, "PassAlpha123", 22, 175.0, 68.0, "muscle");

        String emailUserB = "user_b_" + System.currentTimeMillis() + "@gymhub.com";
        String tokenUserB = registerAndGetToken("User Beta", emailUserB, "PassBeta123", 35, 165.0, 60.0, "weight_loss");

        // Request with Token A must strictly return User A's data
        mockMvc.perform(get("/api/user/dashboard-summary")
                        .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User Alpha"))
                .andExpect(jsonPath("$.email").value(emailUserA))
                .andExpect(jsonPath("$.fitnessGoal").value("muscle"));

        // Request with Token B must strictly return User B's data
        mockMvc.perform(get("/api/user/dashboard-summary")
                        .header("Authorization", "Bearer " + tokenUserB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User Beta"))
                .andExpect(jsonPath("$.email").value(emailUserB))
                .andExpect(jsonPath("$.fitnessGoal").value("weight_loss"));
    }

    @Test
    void test4_ProfileEndpointStillWorks() throws Exception {
        String email = "profile_check_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Profile Check", email, "PassCheck123", 25, 172.0, 67.0, "muscle");

        mockMvc.perform(get("/api/user/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Profile Check"))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void test5_HealthEndpointStillWorks() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("Gym Hub Backend"));
    }
}
