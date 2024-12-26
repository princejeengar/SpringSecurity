package com.registration.service;

import com.registration.dto.AppUserDTO;
import com.registration.dto.AuthResponse;

public interface AuthService {
    AuthResponse signup(AppUserDTO userDTO);
    AuthResponse login(String username, String password);
    void logout(String token);
}