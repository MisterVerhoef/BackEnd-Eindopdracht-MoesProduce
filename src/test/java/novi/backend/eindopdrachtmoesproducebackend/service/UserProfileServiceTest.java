package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UploadedFileRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private UserProfileRepository userProfileRepositoryMock;

    @Mock
    private UploadedFileRepository uploadedFileRepositoryMock;

    @Mock
    private UploadedFileService uploadedFileServiceMock;

    @InjectMocks
    private UserProfileService userProfileService;

    private User testUser;
    private UserProfile testProfile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("TestUser");
        testUser.setEmail("test@example.com");

        testProfile = new UserProfile();
        testProfile.setId(10L);
        testProfile.setName("TestProfile");
        testProfile.setDoB(LocalDate.of(2000, 1, 1));
        testProfile.setAddress("TestAddress");
        testProfile.setUser(testUser);

        testUser.setUserProfile(testProfile);
    }

    @Test
    void getUserProfile_Success() {
        // Arrange
        when(userRepositoryMock.findByUsernameIgnoreCase(testUser.getUsername())).thenReturn(java.util.Optional.of(testUser));

        // Act
        UserProfileDto result = userProfileService.getUserProfile(testUser.getUsername());

        //Assert
        assertNotNull(result);
        assertEquals(testProfile.getName(), result.getName());
        assertEquals(testProfile.getUsername(), result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepositoryMock).findByUsernameIgnoreCase("TestUser");
    }

    @Test
    void getUserProfile_FailsUserNotFound() {
        // ARRANGE
        when(userRepositoryMock.findByUsernameIgnoreCase("Unknown"))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userProfileService.getUserProfile("Unknown"));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void getUserProfile_FailsProfileNull() {
        // ARRANGE
        testUser.setUserProfile(null);
        when(userRepositoryMock.findByUsernameIgnoreCase("TestUser"))
                .thenReturn(Optional.of(testUser));

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userProfileService.getUserProfile("TestUser"));
        assertTrue(ex.getMessage().contains("Profile not found"));
    }

    @Test
    void saveUserProfile_Success() {
        // Arrange
        UserProfile newProfile = new UserProfile();
        newProfile.setName("testProfile");
        doReturn(newProfile).when(userProfileRepositoryMock).save(newProfile);

        //Act
        userProfileService.saveUserProfile(newProfile);

        //Assert
        verify(userProfileRepositoryMock).save(newProfile);
    }

    @Test
    void updateUserProfile_Success() {
        // ARRANGE
        when(userRepositoryMock.findByUsernameIgnoreCase("TestUser"))
                .thenReturn(Optional.of(testUser));
        when(userProfileRepositoryMock.save(any(UserProfile.class))).thenReturn(testProfile);
        when(userRepositoryMock.save(any(User.class))).thenReturn(testUser);

        UserProfileDto dto = new UserProfileDto();
        dto.setUsername("NewUsername");
        dto.setEmail("new@example.com");
        dto.setName("NewName");
        dto.setDoB(LocalDate.of(1999, 12, 12));
        dto.setAddress("NewAddress");


        when(userRepositoryMock.existsByUsernameIgnoreCase("NewUsername")).thenReturn(false);
        when(userRepositoryMock.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);

        // ACT

        UserProfileDto result = userProfileService.updateUserProfile("TestUser", dto);

        // ASSERT
        assertNotNull(result);
        assertEquals("NewName", result.getName());
        assertEquals("NewUsername", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepositoryMock).save(testUser);
        verify(userProfileRepositoryMock).save(testProfile);

    }

    @Test
    void updateUserProfile_FailsUserNotFound() {
        // ARRANGE
        when(userRepositoryMock.findByUsernameIgnoreCase("NonExistent"))
                .thenReturn(Optional.empty());

        UserProfileDto dto = new UserProfileDto();
        dto.setName("any");

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userProfileService.updateUserProfile("NonExistent", dto));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void updateUserProfile_FailsUsernameExists() {
        // ARRANGE
        when(userRepositoryMock.findByUsernameIgnoreCase("TestUser"))
                .thenReturn(Optional.of(testUser));

        UserProfileDto dto = new UserProfileDto();
        dto.setUsername("ExistingName");  // conflict
        when(userRepositoryMock.existsByUsernameIgnoreCase("ExistingName")).thenReturn(true);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userProfileService.updateUserProfile("TestUser", dto));
        assertTrue(ex.getMessage().contains("Username already taken"));
    }

    @Test
    void updateUserProfile_FailsEmailExists() {
        // ARRANGE
        when(userRepositoryMock.findByUsernameIgnoreCase("TestUser"))
                .thenReturn(Optional.of(testUser));

        UserProfileDto dto = new UserProfileDto();
        dto.setEmail("exist@example.com");
        when(userRepositoryMock.existsByEmailIgnoreCase("exist@example.com")).thenReturn(true);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userProfileService.updateUserProfile("TestUser", dto));
        assertTrue(ex.getMessage().contains("Email already taken"));
    }



}