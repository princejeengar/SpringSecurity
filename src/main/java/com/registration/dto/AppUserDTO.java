package com.registration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDTO {
    private Long id;
    private String email;
    private String username;
    private String password;
    private List<String> userRoles;
    private boolean isActive;
    private LocalDateTime createdAt;
}