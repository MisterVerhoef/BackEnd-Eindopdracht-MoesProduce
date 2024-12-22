package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.VegetableDto;
import novi.backend.eindopdrachtmoesproducebackend.exceptions.AdvertNotFoundException;
import novi.backend.eindopdrachtmoesproducebackend.models.Advert;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.models.Vegetable;
import novi.backend.eindopdrachtmoesproducebackend.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertServiceTest {
    @Mock
    private AdvertRepository advertRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UploadedFileRepository uploadedFileRepository;

    @Mock
    private UploadedFileService uploadedFileService;

    @Mock
    private VegetableRepository vegetableRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdvertService advertService;

    private Advert advert;
    private User user;
    private UserProfile userProfile;
    private Vegetable vegetable;
    private List<MultipartFile> images;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("Frank");

        userProfile = new UserProfile();
        userProfile.setUser(user);

        advert = new Advert();
        advert.setId(1L);
        advert.setTitle("Test Advert");
        advert.setDescription("Test description");
        advert.setUserProfile(userProfile);

        advert.setVegetables(new ArrayList<>());

        vegetable = new Vegetable("Koolsoorten", "Bloemkool");

        images = new ArrayList<>();
    }


    @Test
    void createAdvert_Success() {
        //Arrange
        when(authentication.getName()).thenReturn("Frank");
        when(userProfileRepository.findByUser_Username("Frank")).thenReturn(userProfile);
        when(vegetableRepository.findByName("Bloemkool")).thenReturn(Optional.of(vegetable));
        when(advertRepository.save(any(Advert.class))).thenReturn(advert);

        List<VegetableDto> vegetableDtos = List.of(new VegetableDto("Koolsoorten", "Bloemkool"));
        //Act
        AdvertDto result = advertService.createAdvert(
                "Test Advert",
                "Test description",
                vegetableDtos,
                images,
                authentication);

        //Assert
        assertNotNull(result);
        assertEquals("Test Advert", result.getTitle());
        verify(advertRepository, times(1)).save(any(Advert.class));
        verify(uploadedFileService, never()).storeFile(any(MultipartFile.class), any(Advert.class));
    }

    @Test
    void deleteAdvert_Success() {
        //Arrange

        when(advertRepository.findById(1L)).thenReturn(Optional.of(advert));

        //Act

        advertService.deleteAdvert(1L, "Frank");

        //Assert

        verify(advertRepository, times(1)).findById(1L);
        verify(advertRepository, times(1)).delete(advert);
    }

    @Test
    void deleteAdvert_AdvertNotFound() {

        //Arrange
        when(advertRepository.findById(99L)).thenReturn(Optional.empty());

        //Act & Assert

        assertThrows(AdvertNotFoundException.class, () ->
                advertService.deleteAdvert(99L, "Frank"));


        verify(advertRepository, never()).delete(any(Advert.class));
    }

    @Test
    void deleteAdvert_UserNotAuthorized() {

        //Arrange
        when(advertRepository.findById(1L)).thenReturn(Optional.of(advert));

        //Act & Assert

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                advertService.deleteAdvert(1L, "OtherUser"));

         assertTrue(thrown.getMessage().contains("User is not authorized to delete this advert"));
        verify(advertRepository, never()).delete(any(Advert.class));


    }

}