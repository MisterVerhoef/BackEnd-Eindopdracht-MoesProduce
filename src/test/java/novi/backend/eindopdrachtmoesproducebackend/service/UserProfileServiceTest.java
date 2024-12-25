package novi.backend.eindopdrachtmoesproducebackend.service;

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




}