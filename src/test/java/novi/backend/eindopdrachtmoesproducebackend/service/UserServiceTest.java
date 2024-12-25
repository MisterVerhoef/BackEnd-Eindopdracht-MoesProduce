package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.dtos.UserResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import novi.backend.eindopdrachtmoesproducebackend.securtiy.CustomUserDetails;
import novi.backend.eindopdrachtmoesproducebackend.securtiy.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @Mock
    private JwtUtil jwtUtilMock;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encryptedPassword");
        testUser.addRole(User.Role.USER);
        testUser.setTermsAccepted(true);
    }


    @Test
    void registerUser_Success() {
        // Arrange
        String rawPassword = "testPassword";
        when(userRepositoryMock.existsByEmailIgnoreCase(testUser.getEmail())).thenReturn(false);
        when(userRepositoryMock.existsByUsernameIgnoreCase(testUser.getUsername())).thenReturn(false);
        when(passwordEncoderMock.encode(rawPassword)).thenReturn("encryptedPassword");
        when(userRepositoryMock.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        // Act

        User result = userService.registerUser(testUser.getEmail(), testUser.getUsername(), rawPassword, testUser.isTermsAccepted());

        // Assert
        assertNotNull(result.getId());
        assertEquals(100L, result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getPassword(), result.getPassword());

        UserProfile profile = result.getUserProfile();
        assertNotNull(profile);
        assertEquals("testUser", profile.getName());

        verify(userRepositoryMock).save(any(User.class));


    }

    @Test
    void registerUser_EmailAlreadyExists() {
        // Arrange
        String email = "test@example.com";
        when(userRepositoryMock.existsByEmailIgnoreCase(email)).thenReturn(true);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(email, "testUser", "testPassword", true);
        });
        assertEquals("Email is al in gebruik", ex.getMessage());
        verify(userRepositoryMock, never()).save(any(User.class));

    }

    @Test
    void registerUser_UsernameAlreadyExists() {
        // Arrange
        String username = "testUser";
        when(userRepositoryMock.existsByUsernameIgnoreCase(username)).thenReturn(true);

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.registerUser("test@example.com", username, "testPassword", true);
        });

        assertEquals("Gebruikersnaam is al in gebruik", ex.getMessage());
        verify(userRepositoryMock, never()).save(any(User.class));
    }

    @Test
    void registerUser_FailsTermsNotAccepted() {
        // ARRANGE
        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.registerUser("mail@example.com", "NoTerms", "pass", false));
        assertTrue(ex.getMessage().contains("Voorwaarden moeten worden geaccepteerd"));
    }

    @Test
    void loginUser_Success() {
        // Arrange
        when(userRepositoryMock.findByUsernameOrEmail("testUser")).thenReturn(Optional.of(testUser));
        when(passwordEncoderMock.matches("testPassword", testUser.getPassword())).thenReturn(true);
        when(jwtUtilMock.generateToken(any(CustomUserDetails.class))).thenReturn("testToken");

        // Act
        String token = userService.loginUser("testUser", "testPassword");

        // Assert
        assertEquals("testToken", token);
        verify(jwtUtilMock).generateToken(any(CustomUserDetails.class));
    }

    @Test
    void loginUser_FailsWhenUserNotFound() {
        //Arrange
        when(userRepositoryMock.findByUsernameOrEmail("Unknown"))
                .thenReturn(Optional.empty());

        //Act & Assert

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> userService.loginUser("Unknown", "pass"));
        assertTrue(ex.getMessage().contains("Invalid username/email or password"));
        verify(userRepositoryMock).findByUsernameOrEmail("Unknown");
        verify(jwtUtilMock, never()).generateToken(any(CustomUserDetails.class));
    }

    @Test
    void loginUser_FailsWhenPasswordDoesNotMatch() {
        //Arrange
        when(userRepositoryMock.findByUsernameOrEmail("testUser"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoderMock.matches("wrongPassword", testUser.getPassword()))
                .thenReturn(false);

        //Act & Assert
        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> userService.loginUser("testUser", "wrongPassword"));
        assertTrue(ex.getMessage().contains("Invalid username/email or password"));
        verify(jwtUtilMock, never()).generateToken(any(CustomUserDetails.class));
    }

    @Test
    void getUserById_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepositoryMock).findById(1L);

    }

    @Test
    void getUserById_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepositoryMock.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUserById(1L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getUserByUsername_Success() {
        // ARRANGE
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(userRepositoryMock.existsByUsernameIgnoreCase("NewName")).thenReturn(false);
        when(userRepositoryMock.save(any(User.class))).thenReturn(testUser);

        // ACT
        User updated = userService.updateUser(1L, "new@example.com", "NewName");

        // ASSERT
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("NewName", updated.getUsername());
        verify(userRepositoryMock).save(testUser);
    }

    @Test
    void updateUser_FailsEmailAlreadyExists() {
        // ARRANGE
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.existsByEmailIgnoreCase("new@example.com")).thenReturn(true);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUser(1L, "new@example.com", "AnyName"));
        assertTrue(ex.getMessage().contains("Email already in use"));
        verify(userRepositoryMock, never()).save(any(User.class));
    }

    @Test
    void updateUser_FailsUsernameAlreadyExists() {
        // ARRANGE
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.existsByUsernameIgnoreCase("NewName")).thenReturn(true);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUser(1L, "email@somemail.com", "NewName"));
        assertTrue(ex.getMessage().contains("Username already in use"));
        verify(userRepositoryMock, never()).save(any(User.class));
    }

    @Test
    void changePassword_Success() {
        // ARRANGE
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoderMock.matches("oldPassword", testUser.getPassword())).thenReturn(true);
        when(passwordEncoderMock.encode("newPassword")).thenReturn("newPassword");

        // ACT
        userService.changePassword(1L, "oldPassword", "newPassword");

        /// ASSERT
        verify(userRepositoryMock).save(testUser);
    }

 @Test
    void changePassword_FailsWrongCurrentPassword(){
        //Arrange
     when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
     when(passwordEncoderMock.matches("wrongPass", "encryptedPassword")).thenReturn(false);

     //Act & Assert

     IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> userService.changePassword(1L, "wrongPass", "newPass"));
     assertTrue(ex.getMessage().contains("Current password is incorrect"));
     verify(userRepositoryMock, never()).save(any(User.class));
 }

    @Test
    void deleteUser_Success() {
        //Arrange
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepositoryMock).delete(testUser);

        //Act
        userService.deleteUser(1L);

        //Arrange
        verify(userRepositoryMock).delete(testUser);


    }

    @Test
    void deleteUser_FailUserNotFound() {
        //Arrange
        when(userRepositoryMock.findById(999L)).thenReturn(Optional.empty());

        //Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(999L));
        assertTrue(ex.getMessage().contains("User not found with id: 999"));

    }

    @Test
    void changeUserRole_Success() {
        //Arrange
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepositoryMock.save(testUser)).thenReturn(testUser);


        //Act
        userService.changeUserRole(1L, User.Role.ADMIN);

        //Assert
        assertTrue(testUser.getRoles().contains(User.Role.ADMIN));
        assertEquals(1, testUser.getRoles().size());
        verify(userRepositoryMock).save(testUser);

    }

    @Test
    void promoteToSeller_Success() {
        //Arrange
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(testUser));

        //Act
        userService.promoteToSeller(1L);

        //Assert
        assertTrue(testUser.getRoles().contains(User.Role.SELLER));
        assertTrue(testUser.getRoles().contains(User.Role.USER));
        verify(userRepositoryMock).save(testUser);
    }

    @Test
    void getAllUsers_Success() {
        // ARRANGE
        User anotherUser = new User("some@example.com", "someUser", "encodedXYZ", User.Role.USER);
        anotherUser.setId(2L);

        when(userRepositoryMock.findAll()).thenReturn(List.of(testUser, anotherUser));

        // ACT
        List<UserResponseDto> result = userService.getAllUsers();

        // ASSERT
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(userRepositoryMock).findAll();

    }


}