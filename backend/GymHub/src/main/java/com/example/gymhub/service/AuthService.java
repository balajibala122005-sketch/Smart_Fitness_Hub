package com.example.gymhub.service;

import com.example.gymhub.config.JwtUtil;
import com.example.gymhub.dto.AuthResponse;
import com.example.gymhub.dto.LoginRequest;
import com.example.gymhub.dto.RegisterRequest;
import com.example.gymhub.dto.UserDto;
import com.example.gymhub.entity.Role;
import com.example.gymhub.entity.User;
import com.example.gymhub.exception.UserAlreadyExistsException;
import com.example.gymhub.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException("ID already registered in the database: " + normalizedEmail);
        }

        // Security rule: Public registration must always default to USER clearance
        Role assignedRole = Role.USER;

        // Hash password securely with BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                normalizedEmail,
                hashedPassword,
                request.getAge(),
                request.getHeight(),
                request.getWeight(),
                request.getGoal(),
                assignedRole
        );

        User savedUser = userRepository.save(user);

        // Generate token for auto-login or immediate verification
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name(), savedUser.getId());

        return AuthResponse.success(token, "Bio-Registration successful. Proceed to Link.", UserDto.fromEntity(savedUser));
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Compare submitted password with BCrypt hash in database
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Optional clearance check if role was selected on the login form
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            String selectedRole = request.getRole().trim().toUpperCase();
            // Map common aliases (e.g. "admin" for "SADMIN")
            if (!user.getRole().name().equalsIgnoreCase(selectedRole)) {
                throw new BadCredentialsException("Invalid email or password");
            }
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        return AuthResponse.success(token, "Access granted, " + user.getName(), UserDto.fromEntity(user));
    }

    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email != null ? email.trim().toLowerCase() : null)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        return UserDto.fromEntity(user);
    }
}
