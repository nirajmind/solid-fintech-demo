package com.niraj.solidproject.controller;

import com.niraj.solidproject.dto.CreateUserRequest;
import com.niraj.solidproject.dto.UserDTO;
import com.niraj.solidproject.exception.UserNotFoundException;
import com.niraj.solidproject.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// Static imports for Mockito and AssertJ
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * 1. @ExtendWith(MockitoExtension.class):
 * This tells JUnit 5 to enable Mockito's annotations (@Mock, @InjectMocks).
 */
@ExtendWith(MockitoExtension.class)
class UserControllerPureTest {

    /**
     * 2. @Mock:
     * This is the pure Mockito annotation. It mocks the
     * UserService interface, completely outside any Spring context.
     * This replaces @MockBean.
     */
    @Mock
    private UserService userService;

    /**
     * 3. @InjectMocks:
     * This is the magic. It creates a *real* instance of UserController
     * and automatically "injects" the @Mock UserService we defined above
     * into its constructor.
     */
    @InjectMocks
    private UserController userController;

    // --- Let's write our tests ---

    @Test
    void testGetUser_whenUserExists_shouldReturnUserDTO() {
        // 1. ARRANGE
        UserDTO testUser = new UserDTO(1L, "Niraj", "niraj@example.com");

        // "Teach" the mock (same as before)
        when(userService.getUserById(1L)).thenReturn(testUser);

        // 2. ACT
        // We call the controller method *directly* (no MockMvc)
        ResponseEntity<UserDTO> response = userController.getUser(1L);

        // 3. ASSERT
        // We check the response object directly
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().name()).isEqualTo("Niraj");
    }

    @Test
    void testGetUser_whenUserNotFound_shouldThrowException() {
        // 1. ARRANGE
        // "Teach" the mock to throw the exception
        when(userService.getUserById(99L)).thenThrow(new UserNotFoundException("User not found"));

        // 2. ACT & 3. ASSERT
        // We assert that calling the method throws the expected exception.
        // NOTE: Our @ControllerAdvice (exception handler) is NOT running
        // because there is no Spring context. This test only checks that
        // the controller correctly lets the exception bubble up.
        assertThrows(UserNotFoundException.class, () -> userController.getUser(99L));
    }

    @Test
    void testCreateUser_whenValidRequest_shouldReturnCreatedUser() {
        // 1. ARRANGE
        CreateUserRequest request = new CreateUserRequest("New User", "new@example.com", "new@example.com", "password");
        UserDTO createdUser = new UserDTO(1L, "New User", "new@example.com");

        // "Teach" the mock
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createdUser);

        // 2. ACT
        ResponseEntity<UserDTO> response = userController.createUser(request);

        // 3. ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().id()).isEqualTo(1L);
    }
}
