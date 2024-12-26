package com.registration.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "JwtTokens")
public class JwtToken {

    @Id
    private String id;
    private String token;
    private String username;

    public JwtToken(String token, String username) {
        this.token = token;
        this.username = username;
    }
}