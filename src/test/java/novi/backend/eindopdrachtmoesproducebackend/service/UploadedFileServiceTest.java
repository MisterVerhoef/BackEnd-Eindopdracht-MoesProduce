package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.models.Advert;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UploadedFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UploadedFileServiceTest {


    @Mock
    private UploadedFileRepository uploadedFileRepositoryMock;

    private UploadedFileService uploadedFileService;

    private Path fileStorageLocation;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        fileStorageLocation = Files.createTempDirectory("test-uploads");
        uploadedFileService = new UploadedFileService(uploadedFileRepositoryMock);
    }

    @Test
    void FindByUserProfileId_ShouldReturnFilesForUser() {
        // Arrange
        Long userId = 1L;
        UploadedFile uploadedFile1 = new UploadedFile();
        UploadedFile uploadedFile2 = new UploadedFile();
        when(uploadedFileRepositoryMock.findByUserProfile_Id(userId)).thenReturn(Arrays.asList(uploadedFile1, uploadedFile2));

        // Act
        List<UploadedFile> result = uploadedFileService.findByUserProfileId(userId);

        // Assert

        assertEquals(2, result.size());
    }

    @Test
    void StoreFile_ShouldSaveFileToStorage() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.jpeg", "image/jpeg", "test_data".getBytes());
        Advert advert = new Advert();
        UserProfile userProfile = new UserProfile();
        UploadedFile savedFile = new UploadedFile();
        when(uploadedFileRepositoryMock.save(any(UploadedFile.class))).thenReturn(savedFile);

        // Act
        UploadedFile result = uploadedFileService.storeFile(file, advert, userProfile);

        // Assert
       assertNotNull(result);
       verify(uploadedFileRepositoryMock).save(any(UploadedFile.class));
    }

    @Test
    void StoreFile_ShouldThrowExceptionForInvalidFile() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test_data".getBytes());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> uploadedFileService.storeFile(file));

    }

    @Test
    void StoreFile_ShouldThrowExceptionForLargeFile() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.jpeg", "image/jpeg", new byte[10 * 1024 * 1024]);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> uploadedFileService.storeFile(file));
    }

    @Test
    void StoreFile_ShouldThrowExceptionForInvalidFileType() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test_data".getBytes());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> uploadedFileService.storeFile(file));
    }


    @Test
    void deleteFile_ShouldDeleteFile() throws IOException {
        // Arrange
        Long fileId = 1L;
        UploadedFile file = new UploadedFile();
        file.setFilePath(fileStorageLocation.resolve("test.jpg").toString());
        when(uploadedFileRepositoryMock.findById(fileId)).thenReturn(Optional.of(file));

        // Act
        uploadedFileService.deleteFile(fileId);

        // Assert
        verify(uploadedFileRepositoryMock).delete(file);
        assertFalse(Files.exists(Paths.get(file.getFilePath())));
    }


    @Test
    void deleteFile_WithNonExistentFile_ShouldThrowException() {
        // Arrange
        Long fileId = 1L;
        when(uploadedFileRepositoryMock.findById(fileId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> uploadedFileService.deleteFile(fileId));
    }
}