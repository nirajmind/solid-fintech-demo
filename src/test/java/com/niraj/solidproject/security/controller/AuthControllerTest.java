package com.niraj.solidproject.security.controller;

import com.niraj.solidproject.controller.AuthController;
import com.niraj.solidproject.exception.InvalidCredentialsException;
import com.niraj.solidproject.security.JwtUtil;
import com.niraj.solidproject.security.dto.AuthRequest;
import com.niraj.solidproject.security.dto.AuthResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * REPLACEMENT 1:
 * Use @ExtendWith(MockitoExtension.class) instead of @WebMvcTest.
 * This tells JUnit 5 to enable Mockito's annotations.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    /**
     * REPLACEMENT 2:
     * Use @Mock for all dependencies instead of @MockBean.
     * These are pure Mockito mocks.
     */
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    /**
     * REPLACEMENT 3:
     * Use @InjectMocks for the class we are testing.
     * Mockito will automatically inject the @Mock beans above
     * into the constructor of AuthController.
     */
    @InjectMocks
    private AuthController authController;

    @Test
    void testLogin_Success_shouldReturnJwt() {
        // 1. ARRANGE
        AuthRequest request = new AuthRequest("user", "password");
        UserDetails testUser = new User("user", "password", new ArrayList<>());
        String fakeJwt = "fake.token.string";

        // "Teach" the mocks
        // When authenticate is called, do nothing (don't throw)
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername("user")).thenReturn(testUser);
        when(jwtUtil.generateToken(testUser)).thenReturn(fakeJwt);

        // 2. ACT
        // Call the method directly, not via MockMvc
        ResponseEntity<AuthResponse> response = authController.createAuthenticationToken(request);

        // 3. ASSERT
        // Check the ResponseEntity and its body directly
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jwt()).isEqualTo(fakeJwt);
    }

    @Test
    void testLogin_Failure_shouldThrowException() {
        // 1. ARRANGE
        AuthRequest request = new AuthRequest("user", "wrongpassword");

        // "Teach" the mock to throw the *original* Spring exception
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Assert that calling the controller method throws our *custom* exception.
        // This confirms the controller's try-catch logic is working.
        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> {
                    // This is the direct call
                    authController.createAuthenticationToken(request);
                }
        );

        // Optional: Check the exception message
        assertThat(exception.getMessage()).isEqualTo("Incorrect username or password");
    }
}
