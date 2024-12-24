package novi.backend.eindopdrachtmoesproducebackend.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UploadedFileTest {

    @Test
    void testNoArgsConstructor() {
        // ARRANGE & ACT
        UploadedFile uploadedFile = new UploadedFile();

        // ASSERT
        assertNotNull(uploadedFile);
        assertNull(uploadedFile.getId());
        assertNull(uploadedFile.getAdvert());
        assertNull(uploadedFile.getFileName());
        assertNull(uploadedFile.getFilePath());
        assertNull(uploadedFile.getFileType());
        assertEquals(0, uploadedFile.getFileSize());
        assertNull(uploadedFile.getUserProfile());
    }

    @Test
    void testAllArgsConstructor() {
        // ARRANGE
        Advert advert = new Advert();
        Long expectedId = 10L;
        String expectedFileName = "test.jpg";
        String expectedFilePath = "uploads/test.jpg";
        long expectedFileSize = 1024;
        String expectedFileType = "image/jpeg";

        // ACT
       UploadedFile uploadedFile = new UploadedFile(advert, expectedFileName, expectedFilePath, expectedFileSize, expectedFileType, expectedId);

        // ASSERT
        assertEquals(advert, uploadedFile.getAdvert());
        assertEquals(expectedFileName, uploadedFile.getFileName());
        assertEquals(expectedFilePath, uploadedFile.getFilePath());
        assertEquals(expectedFileSize, uploadedFile.getFileSize());
        assertEquals(expectedFileType, uploadedFile.getFileType());
        assertEquals(expectedId, uploadedFile.getId());

        assertNotNull(uploadedFile);
    }

    @Test
    void testGetAndSetId() {
        // ARRANGE
        UploadedFile uploadedFile = new UploadedFile();
        Long expectedId = 123L;
        // ACT
        uploadedFile.setId(expectedId);
        // ASSERT
        assertEquals(expectedId, uploadedFile.getId());
    }

    @Test
    void testGetAndSetAdvert() {
        // ARRANGE
        UploadedFile uploadedFile = new UploadedFile();
        Advert expectedAdvert = new Advert();
        // ACT
        uploadedFile.setAdvert(expectedAdvert);
        // ASSERT
        assertEquals(expectedAdvert, uploadedFile.getAdvert());
    }

    @Test
    void testGetAndSetFileName() {
        // ARRANGE
        UploadedFile uploadedFile = new UploadedFile();
        String expectedFileName = "test.jpg";
        // ACT
        uploadedFile.setFileName(expectedFileName);
        // ASSERT
        assertEquals(expectedFileName, uploadedFile.getFileName());
    }
    @Test
    void testGetAndSetFilePath() {
        // ARRANGE
        UploadedFile uploadedFile = new UploadedFile();
        String expectedFilePath = "uploads/test.jpg";
        // ACT
        uploadedFile.setFilePath(expectedFilePath);
        // ASSERT
        assertEquals(expectedFilePath, uploadedFile.getFilePath());
    }

    @Test
    void testGetAndSetFileSize() {
        // ARRANGE
        UploadedFile uploadedFile = new UploadedFile();
        long expectedFileSize = 1024;
        // ACT
        uploadedFile.setFileSize(expectedFileSize);
        // ASSERT
        assertEquals(expectedFileSize, uploadedFile.getFileSize());
    }

    @Test
    void testGetAndSetFileType() {
        // ARRANGE
        UploadedFile uploadedFile = new UploadedFile();
        String expectedFileType = "image/jpeg";
        //Act
        uploadedFile.setFileType(expectedFileType);
        // ASSERT
        assertEquals(expectedFileType, uploadedFile.getFileType());
    }

    @Test
    void testGetAndSetUserProfile() {
        // ARRANGE
        UploadedFile uploadedFile = new UploadedFile();
        UserProfile expectedUserProfile = new UserProfile();
        // ACT
        uploadedFile.setUserProfile(expectedUserProfile);
        // ASSERT
        assertEquals(expectedUserProfile, uploadedFile.getUserProfile());
    }



}