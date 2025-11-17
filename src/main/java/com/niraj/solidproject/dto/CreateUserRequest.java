package com.niraj.solidproject.dto;

public record CreateUserRequest(
        String name,
        String username, // New
        String email,
        String password  // New
) {}