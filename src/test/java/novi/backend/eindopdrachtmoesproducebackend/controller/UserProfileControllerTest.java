package novi.backend.eindopdrachtmoesproducebackend.controller;

import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UploadedFileResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.service.UserProfileService;
import novi.backend.eindopdrachtmoesproducebackend.service.UploadedFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UploadedFileService uploadedFileService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserProfileController userProfileController;

    private UserProfileDto userProfileDto;
    private String username;

    @BeforeEach
    void setUp() {
        // Arrange
        username = "testUser";
        userProfileDto = new UserProfileDto();
        userProfileDto.setName("Test");


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setContextPath("");
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);
    }

    @Test
    void getUserProfile_Success() {
        // Arrange
        when(authentication.getName()).thenReturn(username);
        when(userProfileService.getUserProfile(username)).thenReturn(userProfileDto);

        // Act
        ResponseEntity<?> response = userProfileController.getUserProfile(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userProfileDto, response.getBody());
        verify(userProfileService).getUserProfile(username);
    }

    @Test
    void getUserProfile_NullAuthentication() {
        // Arrange - no setup needed for null authentication test

        // Act
        ResponseEntity<?> response = userProfileController.getUserProfile(null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getUserProfile_ServiceException() {
        // Arrange
        when(authentication.getName()).thenReturn(username);
        when(userProfileService.getUserProfile(username)).thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<?> response = userProfileController.getUserProfile(authentication);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void updateUserProfile_Success() {
        // Arrange
        when(authentication.getName()).thenReturn(username);
        when(userProfileService.updateUserProfile(eq(username), any(UserProfileDto.class)))
                .thenReturn(userProfileDto);

        // Act
        ResponseEntity<UserProfileDto> response = userProfileController.updateUserProfile(userProfileDto, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userProfileDto, response.getBody());
    }

    @Test
    void updateUserProfile_NullAuthentication() {
        // Arrange - using userProfileDto from setUp

        // Act
        ResponseEntity<UserProfileDto> response = userProfileController.updateUserProfile(userProfileDto, null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void uploadProfileImage_Success() {
        // Arrange
        when(authentication.getName()).thenReturn(username);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        UserProfile userProfile = new UserProfile();
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName("test.jpg");
        when(userProfileService.getUserProfileEntity(username)).thenReturn(userProfile);
        when(uploadedFileService.storeFile(file)).thenReturn(uploadedFile);

        // Act
        ResponseEntity<UploadedFileResponseDto> response = userProfileController.uploadProfileImage(file, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test.jpg", response.getBody().getFileName());
        assertTrue(response.getBody().getFileUrl().startsWith("http://localhost:8080/uploads/"));
    }

    @Test
    void uploadProfileImage_ServiceException() {
        // Arrange
        when(authentication.getName()).thenReturn(username);
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        when(userProfileService.getUserProfileEntity(username)).thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<UploadedFileResponseDto> response = userProfileController.uploadProfileImage(file, authentication);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
