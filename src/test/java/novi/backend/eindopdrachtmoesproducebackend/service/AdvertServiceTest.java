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
    void createAdvert_UserProfileNotFound() {
        //Arrange
        when(authentication.getName()).thenReturn("WrongUser");
        when(userProfileRepository.findByUser_Username("WrongUser")).thenReturn(null);

        //Act & Assert

        assertThrows(RuntimeException.class, () ->
                advertService.createAdvert(
                        "Test Advert",
                        "Test description",
                        List.of(),
                        List.of(),
                        authentication
                )
        );
        verify(advertRepository, never()).save(any(Advert.class));
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

    @Test
    void getAllAdverts(){

        //Arrange
        Advert advert2 = new Advert();
        advert2.setId(2L);
        advert2.setTitle("Test Advert 2");
        advert2.setDescription("Test description 2");
        advert2.setUserProfile(userProfile);
        advert2.setVegetables(new ArrayList<>());

        when(advertRepository.findAll()).thenReturn(List.of(advert, advert2));
        //Act
        List<AdvertDto> adverts = advertService.getAllAdverts();

        //Assert
        assertEquals(2, adverts.size());
        assertEquals("Test Advert", adverts.get(0).getTitle());
        assertEquals("Test Advert 2", adverts.get(1).getTitle());
    }

    @Test
    void SearchAdverts_Success() {

        //Arrange

        when(advertRepository.searchByTitle("Test")).thenReturn(List.of(advert));

        //Act

        List<AdvertDto> results = advertService.searchAdverts("Test");

        //Assert

        assertEquals(1, results.size());
        assertEquals("Test Advert", results.get(0).getTitle());
        verify(advertRepository).searchByTitle("Test");
    }

    @Test
    void getAdvertById_Success() {
        //Arrange
        when(advertRepository.findById(1L)).thenReturn(Optional.of(advert));
        //Act
        AdvertDto result = advertService.getAdvertById(1L);
        //Assert
        assertNotNull(result);
        assertEquals("Test Advert", result.getTitle());
        verify(advertRepository).findById(1L);
    }

    @Test
    void getAdvertById_AdvertNotFound() {
        //Arrange
        when(advertRepository.findById(999L)).thenReturn(Optional.empty());
        //Act & Assert
        assertThrows(RuntimeException.class, () ->
                advertService.getAdvertById(999L));

        verify(advertRepository, times(1)).findById(999L);
    }

    @Test
    void getAdvertsByUsername_Success() {
        //Arrange
        when(advertRepository.findByUserProfile_User_Username("Frank")).thenReturn(List.of(advert));
        //Act
        List<AdvertDto> result = advertService.getAdvertsByUsername("Frank");
        //Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Advert", result.get(0).getTitle());
        verify(advertRepository).findByUserProfile_User_Username("Frank");
    }

    @Test
    void saveAdvert_Success() {
        //Arrange

        when(advertRepository.findById(1L)).thenReturn(Optional.of(advert));
        UserProfile up = new UserProfile();
        up.setUser(user);
       when(userProfileRepository.findByUser_Username("Frank")).thenReturn(up);

       //Act
        advertService.saveAdvert(1L, "Frank");
        //Assert
        verify(userProfileRepository).save(up);
        verify(advertRepository).save(advert);
        assertEquals(1,advert.getSaveCount());
    }



}