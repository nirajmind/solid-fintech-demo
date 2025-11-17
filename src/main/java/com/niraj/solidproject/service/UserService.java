package com.niraj.solidproject.service;

import com.niraj.solidproject.dto.CreateUserRequest;
import com.niraj.solidproject.dto.UserDTO;

public interface UserService {

    UserDTO getUserById(String id);
    UserDTO createUser(CreateUserRequest request);
}
