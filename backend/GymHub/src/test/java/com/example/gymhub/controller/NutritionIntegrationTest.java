package com.example.gymhub.controller;

import com.example.gymhub.dto.MealLogRequest;
import com.example.gymhub.dto.RegisterRequest;
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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class NutritionIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private String registerAndGetToken(String name, String email, String password) throws Exception {
        RegisterRequest request = new RegisterRequest(name, email, password, 25, 175.0, 72.0, "muscle", "user");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("token").asText();
    }

    @Test
    void test1_UnauthenticatedRequestsRejected() throws Exception {
        mockMvc.perform(get("/api/nutrition/meals"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/nutrition/meals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mealName\":\"Oatmeal\",\"mealType\":\"breakfast\",\"calories\":350}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/nutrition/meals/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/nutrition/summary"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test2_CreateAndRetrieveMeals() throws Exception {
        String email = "meal_user_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Meal Eater", email, "MealPass123");

        MealLogRequest meal1 = new MealLogRequest(
                "Oatmeal & Protein Shake",
                "breakfast",
                LocalDate.now(),
                450,
                35.0,
                55.0,
                8.0,
                "Pre-workout fuel"
        );

        MvcResult postRes = mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meal1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.mealName").value("Oatmeal & Protein Shake"))
                .andExpect(jsonPath("$.mealType").value("BREAKFAST"))
                .andExpect(jsonPath("$.calories").value(450))
                .andExpect(jsonPath("$.protein").value(35.0))
                .andExpect(jsonPath("$.carbohydrates").value(55.0))
                .andExpect(jsonPath("$.fats").value(8.0))
                .andExpect(jsonPath("$.notes").value("Pre-workout fuel"))
                .andReturn();

        assertFalse(postRes.getResponse().getContentAsString().toLowerCase().contains("password"));

        // Log second meal
        MealLogRequest meal2 = new MealLogRequest(
                "Chicken, Rice & Veggies",
                "lunch",
                LocalDate.now(),
                650,
                50.0,
                70.0,
                12.0,
                "Post-workout feast"
        );

        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meal2)))
                .andExpect(status().isCreated());

        // Retrieve all meals
        MvcResult getRes = mockMvc.perform(get("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andReturn();

        assertFalse(getRes.getResponse().getContentAsString().toLowerCase().contains("password"));

        // Retrieve by date filter
        mockMvc.perform(get("/api/nutrition/meals?date=" + LocalDate.now())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void test3_DailySummaryCalculations() throws Exception {
        String email = "summary_user_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Summary Eater", email, "SummaryPass123");

        // Log 2 meals for today
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealLogRequest("Breakfast Eggs", "breakfast", LocalDate.now(), 400, 28.0, 30.0, 15.0, null))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealLogRequest("Salmon & Sweet Potato", "dinner", LocalDate.now(), 600, 42.0, 50.0, 18.0, null))))
                .andExpect(status().isCreated());

        // Get daily summary for today
        mockMvc.perform(get("/api/nutrition/summary?date=" + LocalDate.now())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalories").value(1000))
                .andExpect(jsonPath("$.totalProtein").value(70.0))
                .andExpect(jsonPath("$.totalCarbohydrates").value(80.0))
                .andExpect(jsonPath("$.totalFats").value(33.0))
                .andExpect(jsonPath("$.mealCount").value(2));

        // Get summary without query param (defaults to today)
        mockMvc.perform(get("/api/nutrition/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalories").value(1000))
                .andExpect(jsonPath("$.mealCount").value(2));
    }

    @Test
    void test4_DeleteOwnMeal() throws Exception {
        String email = "del_user_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Delete User", email, "DelPass123");

        // Create meal
        MvcResult res = mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealLogRequest("Protein Bar", "snack", LocalDate.now(), 220, 20.0, 22.0, 7.0, null))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(res.getResponse().getContentAsString());
        long mealId = node.get("id").asLong();

        // Delete meal
        mockMvc.perform(delete("/api/nutrition/meals/" + mealId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Meal log deleted successfully"));

        // Verify meal is no longer returned
        mockMvc.perform(get("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // Verify summary is 0
        mockMvc.perform(get("/api/nutrition/summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalories").value(0))
                .andExpect(jsonPath("$.mealCount").value(0));
    }

    @Test
    void test5_UserDataIsolation_NoCrossUserAccessOrDeletion() throws Exception {
        String emailUserA = "user_a_nutri_" + System.currentTimeMillis() + "@gymhub.com";
        String tokenUserA = registerAndGetToken("User A", emailUserA, "PassAlpha123");

        String emailUserB = "user_b_nutri_" + System.currentTimeMillis() + "@gymhub.com";
        String tokenUserB = registerAndGetToken("User B", emailUserB, "PassBeta123");

        // User A logs Meal A
        MvcResult resA = mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + tokenUserA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealLogRequest("User A Salmon", "dinner", LocalDate.now(), 550, 45.0, 20.0, 22.0, null))))
                .andExpect(status().isCreated())
                .andReturn();
        long mealAId = objectMapper.readTree(resA.getResponse().getContentAsString()).get("id").asLong();

        // User B logs Meal B
        MvcResult resB = mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + tokenUserB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealLogRequest("User B Ribeye Steak", "dinner", LocalDate.now(), 850, 60.0, 0.0, 50.0, null))))
                .andExpect(status().isCreated())
                .andReturn();
        long mealBId = objectMapper.readTree(resB.getResponse().getContentAsString()).get("id").asLong();

        // User A must only see Meal A
        MvcResult getA = mockMvc.perform(get("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isOk())
                .andReturn();
        String contentA = getA.getResponse().getContentAsString();
        assertTrue(contentA.contains("User A Salmon"));
        assertFalse(contentA.contains("User B Ribeye Steak"));

        // User B must only see Meal B
        MvcResult getB = mockMvc.perform(get("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + tokenUserB))
                .andExpect(status().isOk())
                .andReturn();
        String contentB = getB.getResponse().getContentAsString();
        assertTrue(contentB.contains("User B Ribeye Steak"));
        assertFalse(contentA.contains("User B Ribeye Steak"));

        // User A attempts to delete User B's meal -> 404 Not Found
        mockMvc.perform(delete("/api/nutrition/meals/" + mealBId)
                        .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isNotFound());

        // Verify User B's meal was NOT deleted
        mockMvc.perform(get("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + tokenUserB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].mealName").value("User B Ribeye Steak"));

        // User A summary is 550, User B summary is 850
        mockMvc.perform(get("/api/nutrition/summary")
                        .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalories").value(550));

        mockMvc.perform(get("/api/nutrition/summary")
                        .header("Authorization", "Bearer " + tokenUserB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalories").value(850));
    }

    @Test
    void test6_ValidationRejectsInvalidValues() throws Exception {
        String email = "val_nutri_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Val User", email, "ValPass123");

        // Missing/blank meal name
        MealLogRequest blankName = new MealLogRequest("", "lunch", LocalDate.now(), 500, 30.0, 40.0, 10.0, null);
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankName)))
                .andExpect(status().isBadRequest());

        // Invalid meal type
        MealLogRequest invalidType = new MealLogRequest("Cake", "dessert", LocalDate.now(), 500, 30.0, 40.0, 10.0, null);
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidType)))
                .andExpect(status().isBadRequest());

        // Negative calories
        MealLogRequest negativeCalories = new MealLogRequest("Chicken", "lunch", LocalDate.now(), -150, 30.0, 40.0, 10.0, null);
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeCalories)))
                .andExpect(status().isBadRequest());

        // Unreasonable calories (> 10000)
        MealLogRequest hugeCalories = new MealLogRequest("Feast", "dinner", LocalDate.now(), 25000, 30.0, 40.0, 10.0, null);
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hugeCalories)))
                .andExpect(status().isBadRequest());

        // Negative protein
        MealLogRequest negativeProtein = new MealLogRequest("Protein Shake", "snack", LocalDate.now(), 200, -10.0, 10.0, 2.0, null);
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeProtein)))
                .andExpect(status().isBadRequest());

        // Negative carbs
        MealLogRequest negativeCarbs = new MealLogRequest("Rice", "lunch", LocalDate.now(), 200, 5.0, -50.0, 1.0, null);
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeCarbs)))
                .andExpect(status().isBadRequest());

        // Negative fats
        MealLogRequest negativeFats = new MealLogRequest("Avocado", "snack", LocalDate.now(), 200, 3.0, 10.0, -15.0, null);
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(negativeFats)))
                .andExpect(status().isBadRequest());

        // Unreasonable macros (> 1000g)
        MealLogRequest hugeProtein = new MealLogRequest("Super Shake", "snack", LocalDate.now(), 500, 1500.0, 10.0, 5.0, null);
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hugeProtein)))
                .andExpect(status().isBadRequest());

        // Future meal date
        MealLogRequest futureMeal = new MealLogRequest("Future Lunch", "lunch", LocalDate.now().plusDays(2), 500, 30.0, 40.0, 10.0, null);
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(futureMeal)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test7_DashboardSummaryReflectsTodayCalories() throws Exception {
        String email = "dash_nutri_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Dash Nutri User", email, "DashNutri123");

        // Initial dashboard summary should have 0 todayCalories
        mockMvc.perform(get("/api/user/dashboard-summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todayCalories").value(0));

        // Log 2 meals today: 450 + 350 = 800
        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealLogRequest("Breakfast", "breakfast", LocalDate.now(), 450, 30.0, 40.0, 10.0, null))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/nutrition/meals")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MealLogRequest("Lunch", "lunch", LocalDate.now(), 350, 25.0, 35.0, 8.0, null))))
                .andExpect(status().isCreated());

        // Dashboard summary should now reflect 800 todayCalories
        mockMvc.perform(get("/api/user/dashboard-summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.todayCalories").value(800));
    }
}
