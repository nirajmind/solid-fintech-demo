package com.niraj.solidproject.dto;

// A record is an immutable data class. Perfect for DTOs.
public record UserDTO(
        String id,
        String name,
        String email
) {}