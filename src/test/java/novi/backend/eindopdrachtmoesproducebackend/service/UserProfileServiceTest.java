package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
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
    void saveUserProfile_WhenSaveFails_ShouldThrowException() {
        // ARRANGE
        UserProfile profile = new UserProfile();
        doThrow(new RuntimeException("Database error")).when(userProfileRepositoryMock).save(profile);

        // ACT & ASSERT
        assertThrows(RuntimeException.class, () -> userProfileService.saveUserProfile(profile));
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

    @Test
    void getUserProfileEntity_Success() {
        // ARRANGE
        when(userRepositoryMock.findByUsernameIgnoreCase("TestUser"))
                .thenReturn(Optional.of(testUser));

        // ACT
        UserProfile result = userProfileService.getUserProfileEntity("TestUser");

        // ASSERT
        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(userRepositoryMock).findByUsernameIgnoreCase("TestUser");
    }

    @Test
    void getUserProfileEntity_FailsNoUser() {
        // ARRANGE
        when(userRepositoryMock.findByUsernameIgnoreCase("NoUser"))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userProfileService.getUserProfileEntity("NoUser"));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void getUserProfileEntity_FailsNoProfile() {
        // ARRANGE
        testUser.setUserProfile(null);
        when(userRepositoryMock.findByUsernameIgnoreCase("TestUser"))
                .thenReturn(Optional.of(testUser));

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userProfileService.getUserProfileEntity("TestUser"));
        assertTrue(ex.getMessage().contains("Profile not found"));
    }

    @Test
    void updateProfileImage_Success() {
        // ARRANGE
        when(userRepositoryMock.findByUsernameIgnoreCase("TestUser"))
                .thenReturn(Optional.of(testUser));

        when(uploadedFileRepositoryMock.save(any(UploadedFile.class))).thenAnswer(invocation -> {
            UploadedFile uf = invocation.getArgument(0);
            uf.setId(999L);
            return uf;
        });
        when(userProfileRepositoryMock.save(any(UserProfile.class)))
                .thenReturn(testProfile);

        // ACT
        userProfileService.updateProfileImage("TestUser", "testPic.png");

        // ASSERT

        UploadedFile img = testProfile.getProfileImage();
        assertNotNull(img);
        assertEquals("testPic.png", img.getFileName());
        assertEquals(999L, img.getId());
        assertEquals(testProfile, img.getUserProfile());

        verify(userRepositoryMock).findByUsernameIgnoreCase("TestUser");
        verify(uploadedFileRepositoryMock).save(img);
        verify(userProfileRepositoryMock).save(testProfile);
    }

    @Test
    void updateProfileImage_FailsWhenNoUser() {
        // ARRANGE
        when(userRepositoryMock.findByUsernameIgnoreCase("Unknown"))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userProfileService.updateProfileImage("Unknown", "testPic.png"));
        assertTrue(ex.getMessage().contains("User not found"));
        verify(uploadedFileRepositoryMock, never()).save(any());
        verify(userProfileRepositoryMock, never()).save(any());
    }


    @Test
    void assignImageToUserProfile_Success() {
        // ARRANGE
        when(userProfileRepositoryMock.findById(10L)).thenReturn(Optional.of(testProfile));
        when(uploadedFileRepositoryMock.save(any(UploadedFile.class))).thenAnswer(invocation -> {
            UploadedFile uf = invocation.getArgument(0);
            uf.setId(888L);
            return uf;
        });

        // ACT
        userProfileService.assignImageToUserProfile(10L, "assigned.png");

        // ASSERT
        assertNotNull(testProfile.getProfileImage());
        assertEquals("assigned.png", testProfile.getProfileImage().getFileName());
        assertEquals(888L, testProfile.getProfileImage().getId());
        verify(userProfileRepositoryMock).findById(10L);
        verify(uploadedFileRepositoryMock).save(any(UploadedFile.class));
        verify(userProfileRepositoryMock).save(testProfile);
    }

    @Test
    void assignImageToUserProfile_FailsNotFound() {
        // ARRANGE
        when(userProfileRepositoryMock.findById(999L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userProfileService.assignImageToUserProfile(999L, "file.png"));
        assertTrue(ex.getMessage().contains("UserProfile not found"));
        verify(uploadedFileRepositoryMock, never()).save(any());
        verify(userProfileRepositoryMock, never()).save(any());
    }

    @Test
    void convertDtoToUserProfile_AllFieldsNull_ShouldCreateEmptyProfile() {
        // Arrange
        UserProfileDto dto = new UserProfileDto(null, null, null, null, null);

        // Act
        UserProfile profile = userProfileService.convertDtoToUserProfile(dto);

        // Assert
        assertNotNull(profile);
        assertNull(profile.getName());
        assertNull(profile.getDoB());
        assertNull(profile.getAddress());
    }

    @Test
    void convertDtoToUserProfile_PartialFields_ShouldCreateProfile() {
        // Arrange
        UserProfileDto dto = new UserProfileDto("TestName", LocalDate.now(), null, null, null);

        // Act
        UserProfile profile = userProfileService.convertDtoToUserProfile(dto);

        // Assert
        assertNotNull(profile);
        assertEquals("TestName", profile.getName());
        assertEquals(LocalDate.now(), profile.getDoB());
        assertNull(profile.getAddress());
    }


    @Test
    void getUserProfile_WithoutProfileImage_ShouldNotIncludeImageUrl() {
        // Arrange
        testProfile.setProfileImage(null);
        when(userRepositoryMock.findByUsernameIgnoreCase("TestUser")).thenReturn(Optional.of(testUser));

        // Act
        UserProfileDto result = userProfileService.getUserProfile("TestUser");

        // Assert
        assertNull(result.getProfileImageUrl());
    }


}