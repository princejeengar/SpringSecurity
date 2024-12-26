package com.registration.service.impl;

import com.registration.dto.AppUserDTO;
import com.registration.dto.AuthResponse;
import com.registration.entity.AppUser;
import com.registration.entity.JwtToken;
import com.registration.entity.MtRole;
import com.registration.entity.UserRole;
import com.registration.enums.Role;
import com.registration.exception.UserAlreadyExistsException;
import com.registration.repository.AppUserRepository;
import com.registration.repository.JwtTokenRepository;
import com.registration.repository.MtRoleRepository;
import com.registration.repository.UserRoleRepository;
import com.registration.service.AuthService;
import com.registration.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MtRoleRepository mtRoleRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenRepository jwtTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse signup(AppUserDTO userDTO) {
        if (appUserRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        AppUser user = new AppUser();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        AppUser savedUser = appUserRepository.save(user);

        for (String roleName : userDTO.getUserRoles()) {
            Role roleEnum;
            try {
                roleEnum = Role.valueOf(roleName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + roleName);
            }

            MtRole mtRole = mtRoleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

            UserRole userRoleEntry = new UserRole();
            userRoleEntry.setUser(savedUser);
            userRoleEntry.setRole(mtRole);
            userRoleRepository.save(userRoleEntry);
        }

        List<String> roles = userDTO.getUserRoles();
        String token = jwtUtil.generateToken(savedUser.getUsername(), roles);

        JwtToken jwtToken = new JwtToken();
        jwtToken.setToken(token);
        jwtToken.setUsername(savedUser.getUsername());
        jwtTokenRepository.save(jwtToken);

        return new AuthResponse(token, savedUser.getUsername(), roles.getFirst());
    }

    @Override
    public AuthResponse login(String username, String password) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        List<String> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(userRole -> userRole.getRole().getName().toString())
                .collect(Collectors.toList());

        if (roles.isEmpty()) {
            throw new RuntimeException("User does not have any roles assigned");
        }

        String token = jwtUtil.generateToken(username, roles);

        jwtTokenRepository.save(new JwtToken(null, token, username));

        return new AuthResponse(token, username, String.join(",", roles));
    }

    @Override
    public void logout(String token) {
        if (!jwtTokenRepository.existsByToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        jwtTokenRepository.deleteByToken(token);
    }
}