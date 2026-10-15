package com.campuspoll.campus_poll_system.service;

import com.campuspoll.campus_poll_system.dto.AuthResponse;
import com.campuspoll.campus_poll_system.dto.LoginRequest;
import com.campuspoll.campus_poll_system.dto.RegisterRequest;
import com.campuspoll.campus_poll_system.dto.UserDto;
import com.campuspoll.campus_poll_system.entity.User;
import com.campuspoll.campus_poll_system.exception.BadRequestException;
import com.campuspoll.campus_poll_system.exception.ConflictException;
import com.campuspoll.campus_poll_system.exception.ResourceNotFoundException;
import com.campuspoll.campus_poll_system.repository.UserRepository;
import com.campuspoll.campus_poll_system.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email address is already registered: " + request.getEmail());
        }

        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new ConflictException("Student ID is already registered: " + request.getStudentId());
        }

        // Public registration MUST ALWAYS assign STUDENT role
        User user = new User();
        user.setName(request.getFullName());
        user.setStudentId(request.getStudentId());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("STUDENT");

        User savedUser = userRepository.save(user);

        String token = jwtUtils.generateToken(savedUser.getEmail(), savedUser.getRole(), savedUser.getId());
        UserDto userDto = toUserDto(savedUser);

        return new AuthResponse(token, userDto);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole(), user.getId());
        UserDto userDto = toUserDto(user);

        return new AuthResponse(token, userDto);
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return toUserDto(user);
    }

    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getStudentId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
