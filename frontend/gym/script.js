/**
 * Gym Hub - Unified Frontend API Client & Authentication Helper
 * Connects existing frontend modules to the Spring Boot REST backend.
 */

const GymApi = (() => {
    const API_BASE_URL = 'http://localhost:8080/api';
    const TOKEN_KEY = 'gym_token';
    const USER_KEY = 'gym_user_info';
    const LEGACY_USER_KEY = 'currentUser'; // For backward compatibility with existing pages

    /**
     * Core fetch wrapper with JSON serialization, Bearer token injection,
     * and standardized error handling.
     */
    async function request(endpoint, options = {}) {
        const url = `${API_BASE_URL}${endpoint.startsWith('/') ? endpoint : '/' + endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            ...(options.headers || {})
        };

        const token = getToken();
        if (token && !options.skipAuth) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);

            // Handle HTTP 401 Unauthorized (Expired / Invalid Token)
            if (response.status === 401 && !options.skipAuthRedirect) {
                // If on protected page, clear and redirect to login
                if (!window.location.pathname.endsWith('index.html')) {
                    logout();
                    return null;
                }
            }

            // Parse response body if present
            const contentType = response.headers.get('content-type');
            let data = null;
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                data = await response.text();
            }

            if (!response.ok) {
                let errorMessage = 'Request failed';
                if (data && typeof data === 'object') {
                    errorMessage = data.message || data.error || JSON.stringify(data);
                } else if (typeof data === 'string' && data.trim()) {
                    errorMessage = data;
                }
                const error = new Error(errorMessage);
                error.status = response.status;
                error.data = data;
                throw error;
            }

            return data;
        } catch (err) {
            // Network connection failure (e.g. backend not running)
            if (err.name === 'TypeError' && err.message.includes('fetch')) {
                const connError = new Error('Cannot connect to Gym Hub Backend on ' + API_BASE_URL + '. Please ensure Spring Boot is running.');
                connError.status = 0;
                throw connError;
            }
            throw err;
        }
    }

    /* ---------------- Token & Session Management ---------------- */

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function getUser() {
        try {
            const userStr = localStorage.getItem(USER_KEY) || localStorage.getItem(LEGACY_USER_KEY);
            return userStr ? JSON.parse(userStr) : null;
        } catch (e) {
            return null;
        }
    }

    function isAuthenticated() {
        const token = getToken();
        const user = getUser();
        return !!(token && user);
    }

    function saveSession(token, user) {
        if (token) {
            localStorage.setItem(TOKEN_KEY, token);
        }
        if (user) {
            // Store only safe user details - NEVER store plaintext passwords
            const safeUser = {
                id: user.id,
                name: user.name,
                email: user.email,
                role: user.role,
                age: user.age,
                height: user.height,
                weight: user.weight,
                goal: user.goal
            };
            localStorage.setItem(USER_KEY, JSON.stringify(safeUser));
            localStorage.setItem(LEGACY_USER_KEY, JSON.stringify(safeUser));
        }
    }

    function logout() {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        localStorage.removeItem(LEGACY_USER_KEY);
        window.location.href = 'index.html';
    }

    /* ---------------- Auth API Operations ---------------- */

    /**
     * Authenticate user with email and password
     */
    async function login(email, password, role = '') {
        const payload = {
            email: email.trim().toLowerCase(),
            password: password,
            role: role || undefined
        };

        const result = await request('/auth/login', {
            method: 'POST',
            body: JSON.stringify(payload),
            skipAuth: true
        });

        if (result && result.token && result.user) {
            saveSession(result.token, result.user);
        }

        return result;
    }

    /**
     * Register a new user ID with fitness biometrics
     */
    async function register(userData) {
        const payload = {
            name: userData.name,
            email: userData.email.trim().toLowerCase(),
            password: userData.password,
            age: userData.age ? parseInt(userData.age, 10) : null,
            height: userData.height ? parseFloat(userData.height) : null,
            weight: userData.weight ? parseFloat(userData.weight) : null,
            goal: userData.goal || '',
            role: userData.role || 'user'
        };

        return await request('/auth/register', {
            method: 'POST',
            body: JSON.stringify(payload),
            skipAuth: true
        });
    }

    /**
     * Get fresh user profile from backend via JWT
     */
    async function getProfile() {
        return await request('/user/profile', { method: 'GET' });
    }

    /**
     * Get comprehensive dashboard summary with user biometrics and stats
     */
    async function getDashboardSummary() {
        return await request('/user/dashboard-summary', { method: 'GET' });
    }

    /**
     * Record a workout log
     */
    async function logWorkout(workoutData) {
        return await request('/progress/workouts', {
            method: 'POST',
            body: JSON.stringify(workoutData)
        });
    }

    /**
     * Get user workout history
     */
    async function getWorkoutLogs() {
        return await request('/progress/workouts', { method: 'GET' });
    }

    /**
     * Get user workout statistics
     */
    async function getWorkoutStats() {
        return await request('/progress/workouts/stats', { method: 'GET' });
    }

    /**
     * Record a body measurement
     */
    async function logMeasurement(measurementData) {
        return await request('/progress/measurements', {
            method: 'POST',
            body: JSON.stringify(measurementData)
        });
    }

    /**
     * Get user body measurement history
     */
    async function getMeasurements() {
        return await request('/progress/measurements', { method: 'GET' });
    }

    /**
     * Save / select workout plan
     */
    async function saveWorkoutPlan(planName, description = '') {
        return await request('/workouts/plan', {
            method: 'POST',
            body: JSON.stringify({ planName, description })
        });
    }

    /**
     * Get user workout plan
     */
    async function getWorkoutPlan() {
        return await request('/workouts/plan', { method: 'GET' });
    }

    /**
     * Nutrition API Operations
     */
    async function logMeal(mealData) {
        return await request('/nutrition/meals', {
            method: 'POST',
            body: JSON.stringify(mealData)
        });
    }

    async function getMeals(date = '') {
        const query = date ? `?date=${encodeURIComponent(date)}` : '';
        return await request(`/nutrition/meals${query}`, { method: 'GET' });
    }

    async function deleteMeal(mealId) {
        return await request(`/nutrition/meals/${mealId}`, { method: 'DELETE' });
    }

    async function getDailyNutritionSummary(date = '') {
        const query = date ? `?date=${encodeURIComponent(date)}` : '';
        return await request(`/nutrition/summary${query}`, { method: 'GET' });
    }

    /**
     * Check backend health
     */
    async function checkHealth() {
        return await request('/health', { method: 'GET', skipAuth: true });
    }

    return {
        API_BASE_URL,
        request,
        login,
        register,
        getProfile,
        getDashboardSummary,
        checkHealth,
        logWorkout,
        getWorkoutLogs,
        getWorkoutStats,
        logMeasurement,
        getMeasurements,
        saveWorkoutPlan,
        getWorkoutPlan,
        logMeal,
        getMeals,
        deleteMeal,
        getDailyNutritionSummary,
        getToken,
        getUser,
        saveSession,
        isAuthenticated,
        logout
    };
})();

// Attach to window for global access across all pages
window.GymApi = GymApi;
