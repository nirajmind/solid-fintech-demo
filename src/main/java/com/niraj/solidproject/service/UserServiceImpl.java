package com.niraj.solidproject.service;

import com.niraj.solidproject.dto.CreateUserRequest;
import com.niraj.solidproject.dto.UserDTO;
import com.niraj.solidproject.exception.UserNotFoundException;
import com.niraj.solidproject.model.User;
import com.niraj.solidproject.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // We need this injected

    // Dependency Inversion: We depend on the abstraction (interface)
    // Spring injects the concrete implementation (UserRepository)
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        // Map from Entity -> DTO
        return new UserDTO(user.getId(), user.getName(), user.getEmail());
    }

    @Override
    public UserDTO createUser(CreateUserRequest request) {
        // Map from DTO -> Entity
        User newUser = new User(
                request.name(),
                request.username(),
                request.email(),
                // CRITICAL: Encode the password before saving!
                passwordEncoder.encode(request.password()),
                "ROLE_USER" // Default role
        );

        User savedUser = userRepository.save(newUser);

        return new UserDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }
}
