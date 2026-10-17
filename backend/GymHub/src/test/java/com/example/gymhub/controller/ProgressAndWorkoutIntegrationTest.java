package com.example.gymhub.controller;

import com.example.gymhub.dto.BodyMeasurementRequest;
import com.example.gymhub.dto.RegisterRequest;
import com.example.gymhub.dto.WorkoutLogRequest;
import com.example.gymhub.dto.WorkoutPlanRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ProgressAndWorkoutIntegrationTest {

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
        RegisterRequest request = new RegisterRequest(name, email, password, 25, 175.0, 70.0, "muscle", "user");
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
        mockMvc.perform(get("/api/progress/workouts"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/progress/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"exerciseName\":\"Squats\",\"sets\":3,\"reps\":10,\"durationMinutes\":15}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/progress/workouts/stats"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/progress/measurements"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/progress/measurements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"weight\":75.5}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/workouts/plan"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/workouts/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"planName\":\"Rank S: Titan\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void test2_CreateAndRetrieveWorkoutLogs() throws Exception {
        String email = "workout_test_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Workout Logger", email, "LoggerPass123");

        WorkoutLogRequest workoutReq = new WorkoutLogRequest(
                "Leg Day Protocol",
                "Barbell Squats",
                "Strength",
                4,
                12,
                35,
                280,
                80.0,
                "Strong form, clean depth",
                LocalDate.now()
        );

        MvcResult postResult = mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.workoutName").value("Leg Day Protocol"))
                .andExpect(jsonPath("$.exerciseName").value("Barbell Squats"))
                .andExpect(jsonPath("$.exerciseType").value("Strength"))
                .andExpect(jsonPath("$.sets").value(4))
                .andExpect(jsonPath("$.reps").value(12))
                .andExpect(jsonPath("$.durationMinutes").value(35))
                .andExpect(jsonPath("$.caloriesBurned").value(280))
                .andExpect(jsonPath("$.weightUsed").value(80.0))
                .andExpect(jsonPath("$.notes").value("Strong form, clean depth"))
                .andReturn();

        assertFalse(postResult.getResponse().getContentAsString().toLowerCase().contains("password"));

        // Retrieve workout logs
        MvcResult getResult = mockMvc.perform(get("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exerciseName").value("Barbell Squats"))
                .andExpect(jsonPath("$[0].sets").value(4))
                .andExpect(jsonPath("$[0].durationMinutes").value(35))
                .andReturn();

        assertFalse(getResult.getResponse().getContentAsString().toLowerCase().contains("password"));

        // Retrieve workout statistics
        mockMvc.perform(get("/api/progress/workouts/stats")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWorkouts").value(1))
                .andExpect(jsonPath("$.totalDurationMinutes").value(35))
                .andExpect(jsonPath("$.totalCaloriesBurned").value(280))
                .andExpect(jsonPath("$.averageDurationMinutes").value(35.0));
    }

    @Test
    void test3_CreateAndRetrieveBodyMeasurements() throws Exception {
        String email = "measurement_test_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Measurement Logger", email, "LoggerPass123");

        BodyMeasurementRequest measurementReq = new BodyMeasurementRequest(
                74.5,
                102.0,
                178.0,
                LocalDate.now()
        );

        MvcResult postResult = mockMvc.perform(post("/api/progress/measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(measurementReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.weight").value(74.5))
                .andExpect(jsonPath("$.chest").value(102.0))
                .andExpect(jsonPath("$.height").value(178.0))
                .andReturn();

        assertFalse(postResult.getResponse().getContentAsString().toLowerCase().contains("password"));

        // Retrieve measurements
        MvcResult getResult = mockMvc.perform(get("/api/progress/measurements")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].weight").value(74.5))
                .andExpect(jsonPath("$[0].chest").value(102.0))
                .andReturn();

        assertFalse(getResult.getResponse().getContentAsString().toLowerCase().contains("password"));
    }

    @Test
    void test4_CreateAndRetrieveWorkoutPlan() throws Exception {
        String email = "plan_test_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Plan User", email, "PlanPass123");

        // Initial plan should have default suggestion
        mockMvc.perform(get("/api/workouts/plan")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("Rank E: Novice"))
                .andExpect(jsonPath("$.disclaimer").isNotEmpty());

        // Update plan
        WorkoutPlanRequest planReq = new WorkoutPlanRequest("Rank S: Titan", "Extreme hypertrophy and maximum load.");

        MvcResult postResult = mockMvc.perform(post("/api/workouts/plan")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("Rank S: Titan"))
                .andExpect(jsonPath("$.description").value("Extreme hypertrophy and maximum load."))
                .andExpect(jsonPath("$.disclaimer").isNotEmpty())
                .andReturn();

        assertFalse(postResult.getResponse().getContentAsString().toLowerCase().contains("password"));

        // Retrieve updated plan
        mockMvc.perform(get("/api/workouts/plan")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("Rank S: Titan"));
    }

    @Test
    void test5_UserIsolation_NoCrossUserAccess() throws Exception {
        String emailUserA = "isolation_a_" + System.currentTimeMillis() + "@gymhub.com";
        String tokenUserA = registerAndGetToken("Iso User A", emailUserA, "IsoPass123");

        String emailUserB = "isolation_b_" + System.currentTimeMillis() + "@gymhub.com";
        String tokenUserB = registerAndGetToken("Iso User B", emailUserB, "IsoPass123");

        // User A logs a workout
        WorkoutLogRequest workoutA = new WorkoutLogRequest("Workout A", "Bench Press", 3, 10, 20, 70.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + tokenUserA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutA)))
                .andExpect(status().isCreated());

        // User B logs a workout
        WorkoutLogRequest workoutB = new WorkoutLogRequest("Workout B", "Deadlifts", 5, 5, 30, 120.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + tokenUserB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutB)))
                .andExpect(status().isCreated());

        // User A must only see Bench Press
        MvcResult resA = mockMvc.perform(get("/api/progress/workouts")
                        .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isOk())
                .andReturn();
        String contentA = resA.getResponse().getContentAsString();
        assertTrue(contentA.contains("Bench Press"));
        assertFalse(contentA.contains("Deadlifts"));

        // User B must only see Deadlifts
        MvcResult resB = mockMvc.perform(get("/api/progress/workouts")
                        .header("Authorization", "Bearer " + tokenUserB))
                .andExpect(status().isOk())
                .andReturn();
        String contentB = resB.getResponse().getContentAsString();
        assertTrue(contentB.contains("Deadlifts"));
        assertFalse(contentB.contains("Bench Press"));

        // User A logs measurement
        mockMvc.perform(post("/api/progress/measurements")
                        .header("Authorization", "Bearer " + tokenUserA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BodyMeasurementRequest(71.0, 95.0, 175.0, LocalDate.now()))))
                .andExpect(status().isCreated());

        // User B logs measurement
        mockMvc.perform(post("/api/progress/measurements")
                        .header("Authorization", "Bearer " + tokenUserB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BodyMeasurementRequest(85.0, 110.0, 180.0, LocalDate.now()))))
                .andExpect(status().isCreated());

        // User A's measurements must not contain 85.0
        MvcResult mResA = mockMvc.perform(get("/api/progress/measurements")
                        .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(mResA.getResponse().getContentAsString().contains("71.0"));
        assertFalse(mResA.getResponse().getContentAsString().contains("85.0"));

        // User A sets Plan A, User B sets Plan B
        mockMvc.perform(post("/api/workouts/plan")
                        .header("Authorization", "Bearer " + tokenUserA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WorkoutPlanRequest("Rank E: Novice", "Beginner"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/workouts/plan")
                        .header("Authorization", "Bearer " + tokenUserB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WorkoutPlanRequest("Rank S: Titan", "Advanced"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/workouts/plan")
                        .header("Authorization", "Bearer " + tokenUserA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("Rank E: Novice"));

        mockMvc.perform(get("/api/workouts/plan")
                        .header("Authorization", "Bearer " + tokenUserB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planName").value("Rank S: Titan"));
    }

    @Test
    void test6_ValidationRejectsInvalidValues() throws Exception {
        String email = "validation_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Val User", email, "ValPass123");

        // WorkoutLog with blank exercise name
        WorkoutLogRequest invalidWorkout1 = new WorkoutLogRequest("Routine", "", 3, 10, 20, 50.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidWorkout1)))
                .andExpect(status().isBadRequest());

        // WorkoutLog with non-positive sets
        WorkoutLogRequest invalidWorkout2 = new WorkoutLogRequest("Routine", "Pullups", 0, 10, 20, 50.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidWorkout2)))
                .andExpect(status().isBadRequest());

        // WorkoutLog with non-positive duration (0 minutes)
        WorkoutLogRequest invalidDuration = new WorkoutLogRequest("Routine", "Planks", 3, 10, 0, 0.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDuration)))
                .andExpect(status().isBadRequest());

        // WorkoutLog with unreasonable duration (> 1440 min)
        WorkoutLogRequest invalidLongDuration = new WorkoutLogRequest("Routine", "Running", 1, 1, 5000, 0.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLongDuration)))
                .andExpect(status().isBadRequest());

        // WorkoutLog with negative calories
        WorkoutLogRequest invalidCalories = new WorkoutLogRequest("Routine", "Pushups", 3, 10, 20, -50.0, LocalDate.now());
        invalidCalories.setCaloriesBurned(-100);
        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCalories)))
                .andExpect(status().isBadRequest());

        // WorkoutLog with future date
        WorkoutLogRequest futureWorkout = new WorkoutLogRequest("Future Routine", "Squats", 3, 10, 20, 0.0, LocalDate.now().plusDays(5));
        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(futureWorkout)))
                .andExpect(status().isBadRequest());

        // Measurement with negative weight
        BodyMeasurementRequest invalidMeasurement = new BodyMeasurementRequest(-5.0, 90.0, 170.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMeasurement)))
                .andExpect(status().isBadRequest());

        // Measurement with unreasonable weight (> 500 kg)
        BodyMeasurementRequest invalidHeavyWeight = new BodyMeasurementRequest(750.0, 90.0, 170.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidHeavyWeight)))
                .andExpect(status().isBadRequest());

        // Measurement with unreasonable chest (> 300 cm)
        BodyMeasurementRequest invalidChest = new BodyMeasurementRequest(70.0, 450.0, 170.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidChest)))
                .andExpect(status().isBadRequest());

        // Measurement with future date
        BodyMeasurementRequest futureMeasurement = new BodyMeasurementRequest(70.0, 95.0, 170.0, LocalDate.now().plusDays(2));
        mockMvc.perform(post("/api/progress/measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(futureMeasurement)))
                .andExpect(status().isBadRequest());

        // Plan with blank name
        WorkoutPlanRequest invalidPlan = new WorkoutPlanRequest("", "Blank plan");
        mockMvc.perform(post("/api/workouts/plan")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPlan)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test7_DashboardSummaryReflectsWorkoutsAndPlan() throws Exception {
        String email = "dash_sync_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Sync User", email, "SyncPass123");

        // Select Plan
        mockMvc.perform(post("/api/workouts/plan")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new WorkoutPlanRequest("Rank C: Warrior", "Intense conditioning."))))
                .andExpect(status().isOk());

        // Log 2 workouts
        WorkoutLogRequest w1 = new WorkoutLogRequest("Day 1", "Pushups", 3, 15, 10, null, LocalDate.now());
        WorkoutLogRequest w2 = new WorkoutLogRequest("Day 2", "Pullups", 3, 8, 15, null, LocalDate.now());

        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(w1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(w2)))
                .andExpect(status().isCreated());

        // Log a new measurement to verify latest weight update
        BodyMeasurementRequest m1 = new BodyMeasurementRequest(78.5, 105.0, 175.0, LocalDate.now());
        mockMvc.perform(post("/api/progress/measurements")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(m1)))
                .andExpect(status().isCreated());

        // Verify Dashboard Summary has 2 workouts, updated weight, and Rank C: Warrior plan
        mockMvc.perform(get("/api/user/dashboard-summary")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activePlan").value("Rank C: Warrior"))
                .andExpect(jsonPath("$.totalWorkouts").value(2))
                .andExpect(jsonPath("$.weight").value(78.5));
    }

    @Test
    void test8_WorkoutWithExerciseTypeAndOptionalSetsReps() throws Exception {
        String email = "cardio_test_" + System.currentTimeMillis() + "@gymhub.com";
        String token = registerAndGetToken("Cardio Runner", email, "CardioPass123");

        // Cardio workout with workout name, exercise type, duration, calories burned, date, notes
        WorkoutLogRequest cardioReq = new WorkoutLogRequest(
                "Morning Endurance",
                "Cardio Running",
                45,
                420,
                LocalDate.now(),
                "Outdoor steady state tempo run"
        );

        mockMvc.perform(post("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardioReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.workoutName").value("Morning Endurance"))
                .andExpect(jsonPath("$.exerciseType").value("Cardio Running"))
                .andExpect(jsonPath("$.durationMinutes").value(45))
                .andExpect(jsonPath("$.caloriesBurned").value(420))
                .andExpect(jsonPath("$.notes").value("Outdoor steady state tempo run"));

        // Retrieve workouts
        mockMvc.perform(get("/api/progress/workouts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workoutName").value("Morning Endurance"))
                .andExpect(jsonPath("$[0].durationMinutes").value(45))
                .andExpect(jsonPath("$[0].caloriesBurned").value(420));
    }
}
