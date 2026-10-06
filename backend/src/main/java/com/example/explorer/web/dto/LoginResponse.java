package com.example.explorer.web.dto;

public record LoginResponse(
    String token,
    String username,
    String role,
    Long expiresIn
) {}
