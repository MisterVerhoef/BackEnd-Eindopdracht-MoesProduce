package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.dtos.LoginResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import novi.backend.eindopdrachtmoesproducebackend.security.CustomUserDetails;
import novi.backend.eindopdrachtmoesproducebackend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManagerMock;
    @Mock
    private UserDetailsService userDetailsServiceMock;
    @Mock
    private JwtUtil jwtUtilMock;
    @Mock
    private UserRepository userRepositoryMock;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("TestUser");
        testUser.setPassword("encodedPass");
        testUser.setEmail("test@example.com");
    }

    @Test
    void login_shouldReturnJwtToken_whenValidCredentials() {
        // ARRANGE
        String usernameOrEmail = "TestUser";
        String password = "secret";


        Authentication authMock = mock(Authentication.class);
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        // userRepository
        when(userRepositoryMock.findByUsernameOrEmail(usernameOrEmail))
                .thenReturn(Optional.of(testUser));


        when(jwtUtilMock.generateToken(any(CustomUserDetails.class)))
                .thenReturn("fake.jwt.token");

        // ACT
        LoginResponseDto response = authenticationService.login(usernameOrEmail, password);

        // ASSERT
        assertNotNull(response);
        assertEquals("fake.jwt.token", response.getToken());

        verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepositoryMock).findByUsernameOrEmail(usernameOrEmail);
        verify(jwtUtilMock).generateToken(any(CustomUserDetails.class));
    }

    @Test
    void login_ShouldThrowRuntimeException_WhenUserNotFound() {
        // ARRANGE
        String usernameOrEmail = "UnknownUser";
        String password = "wrongPass";


        Authentication authMock = mock(Authentication.class);
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        when(userRepositoryMock.findByUsernameOrEmail(usernameOrEmail))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authenticationService.login(usernameOrEmail, password));

        assertTrue(ex.getMessage().contains("Invalid username/email or password"));

        verify(authenticationManagerMock, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepositoryMock, times(1))
                .findByUsernameOrEmail(usernameOrEmail);
        verify(jwtUtilMock, never())
                .generateToken(any(CustomUserDetails.class));
    }
}

