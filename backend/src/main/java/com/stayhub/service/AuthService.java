package com.stayhub.service;

import com.stayhub.dto.AuthResponse;
import com.stayhub.dto.LoginRequest;
import com.stayhub.dto.RefreshTokenRequest;
import com.stayhub.dto.RegisterRequest;
import com.stayhub.dto.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);
}
