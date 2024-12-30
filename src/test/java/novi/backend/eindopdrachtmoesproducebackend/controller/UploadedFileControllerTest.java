package novi.backend.eindopdrachtmoesproducebackend.controller;

import novi.backend.eindopdrachtmoesproducebackend.models.Advert;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import novi.backend.eindopdrachtmoesproducebackend.security.JwtUtil;
import novi.backend.eindopdrachtmoesproducebackend.security.SecurityConfig;
import novi.backend.eindopdrachtmoesproducebackend.service.AdvertService;
import novi.backend.eindopdrachtmoesproducebackend.service.UploadedFileService;
import novi.backend.eindopdrachtmoesproducebackend.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UploadedFileController.class)
@Import(SecurityConfig.class)
class UploadedFileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UploadedFileService uploadedFileService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private AdvertService advertService;

    @MockBean
    private UserRepository userRepository;

    private UserProfile testUserProfile;
    private Advert testAdvert;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {
        testUserProfile = new UserProfile();
        testUserProfile.setId(1L);

        testAdvert = new Advert();
        testAdvert.setId(1L);

        testFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );
    }

    @Test
    @WithMockUser(username = "testuser")
    void uploadProfileImage_ShouldUploadImageAndReturnResponseDto() throws Exception {
        // Arrange
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName("test-image.jpg");
        MultipartFile file = new MockMultipartFile("file", "test-image.jpg", "image/jpeg", "content".getBytes());
        when(userProfileService.getUserProfileEntity("testuser")).thenReturn(testUserProfile);
        when(uploadedFileService.storeFile(any(MultipartFile.class), any(UserProfile.class))).thenReturn(uploadedFile);

        // Act & Assert
        mockMvc.perform(multipart("/api/uploads/profile")
                        .file("file", "test-image.jpg".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test-image.jpg"))
                .andExpect(jsonPath("$.fileUrl").exists());


        verify(userProfileService).getUserProfileEntity("testuser");
        verify(uploadedFileService).storeFile(any(MultipartFile.class), eq(testUserProfile));
        verify(userProfileService).saveUserProfile(testUserProfile);
    }


    @Test
    @WithMockUser(username = "testuser")
    void uploadAdvertImage_ShouldUploadImageAndReturnResponseDto() throws Exception {
        // Arrange
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName("test-image.jpg");
        MultipartFile file = new MockMultipartFile("file", "test-image.jpg", "image/jpeg", "content".getBytes());
        when(advertService.getAdvertEntityById(1L)).thenReturn(testAdvert);
        when(uploadedFileService.storeFile(any(MultipartFile.class), any(Advert.class))).thenReturn(uploadedFile);

        // Act & Assert
        mockMvc.perform(multipart("/api/uploads/advert/1")
                        .file("file", "test-image.jpg".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test-image.jpg"))
                .andExpect(jsonPath("$.fileUrl").exists());


        verify(advertService).getAdvertEntityById(1L);
        verify(uploadedFileService).storeFile(any(MultipartFile.class), eq(testAdvert));
        verify(advertService).addImageToAdvert(eq(1L), eq("test-image.jpg"), eq("testuser"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void uploadProfileImage_ShouldReturnInternalServerErrorWhenExceptionOccurs() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test-image.jpg", "image/jpeg", "content".getBytes());
        when(userProfileService.getUserProfileEntity("testuser"))
                .thenThrow(new RuntimeException("Something went wrong"));

        // Act & Assert
        mockMvc.perform(multipart("/api/uploads/profile")
                        .file("file", "test-image.jpg".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is5xxServerError());


        verify(userProfileService).getUserProfileEntity("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void uploadAdvertImage_ShouldReturnInternalServerErrorWhenExceptionOccurs() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test-image.jpg", "image/jpeg", "content".getBytes());
        when(advertService.getAdvertEntityById(1L))
                .thenThrow(new RuntimeException("Something went wrong"));

        // Act & Assert
        mockMvc.perform(multipart("/api/uploads/advert/1")
                        .file("file", "test-image.jpg".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is5xxServerError());


        verify(advertService).getAdvertEntityById(1L);
    }

}