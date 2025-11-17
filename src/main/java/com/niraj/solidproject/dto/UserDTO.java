package com.niraj.solidproject.dto;

// A record is an immutable data class. Perfect for DTOs.
public record UserDTO(
        Long id,
        String name,
        String email
) {}