package com.niraj.solidproject.security.dto;

public record AuthRequest(
    String username,
    String password
) {}
