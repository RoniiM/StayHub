package com.stayhub.service.impl;

import com.stayhub.dto.AuthResponse;
import com.stayhub.dto.LoginRequest;
import com.stayhub.dto.RefreshTokenRequest;
import com.stayhub.dto.RegisterRequest;
import com.stayhub.dto.UserResponse;
import com.stayhub.entity.User;
import com.stayhub.entity.enums.UserRole;
import com.stayhub.exception.DuplicateEmailException;
import com.stayhub.exception.InvalidCredentialsException;
import com.stayhub.exception.InvalidJwtException;
import com.stayhub.mapper.UserMapper;
import com.stayhub.repository.UserRepository;
import com.stayhub.security.JwtService;
import com.stayhub.service.AuthService;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            AuthenticationManager authenticationManager,
                            JwtService jwtService,
                            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration rejected: email already in use");
            throw new DuplicateEmailException("Email is already registered: " + request.email());
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .enabled(true)
                .roles(new HashSet<>(Set.of(UserRole.ROLE_GUEST)))
                .build();

        User saved = userRepository.save(user);
        log.info("User registered: userId={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (AuthenticationException ex) {
            log.warn("Failed login attempt for email: {}", request.email());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User logged in: userId={}", user.getId());
        return new AuthResponse(accessToken, refreshToken, "Bearer");
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refresh(RefreshTokenRequest request) {
        String token = request.refreshToken();

        try {
            if (!jwtService.isRefreshToken(token)) {
                throw new InvalidJwtException("Provided token is not a valid refresh token");
            }

            String email = jwtService.extractUsername(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new InvalidJwtException("Refresh token does not match any known user"));

            if (!jwtService.isTokenValid(token, user)) {
                throw new InvalidJwtException("Refresh token is invalid or expired");
            }

            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            log.info("Access token refreshed: userId={}", user.getId());
            return new AuthResponse(newAccessToken, newRefreshToken, "Bearer");
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Rejected refresh attempt with invalid or expired token");
            throw new InvalidJwtException("Invalid or expired refresh token");
        }
    }
}
