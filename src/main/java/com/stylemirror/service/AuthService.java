package com.stylemirror.service;

import com.stylemirror.dto.AuthRequest;
import com.stylemirror.dto.AuthResponse;
import com.stylemirror.dto.RegisterRequest;
import com.stylemirror.dto.UserDto;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);
    UserDto getCurrentUser(Long userId);
}
