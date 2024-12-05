package novi.backend.eindopdrachtmoesproducebackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UploadedFileResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.VegetableDto;
import novi.backend.eindopdrachtmoesproducebackend.exceptions.AdvertNotFoundException;
import novi.backend.eindopdrachtmoesproducebackend.models.*;
import novi.backend.eindopdrachtmoesproducebackend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertService {

    @Autowired
    private AdvertRepository advertRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VegetableRepository vegetableRepository;

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private UploadedFileService uploadedFileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public AdvertDto createAdvert(
            String title,
            String description,
            List<VegetableDto> vegetableDtos,
            List<MultipartFile> images,
            Authentication authentication) {

        String username = authentication.getName();
        UserProfile userProfile = userProfileRepository.findByUser_Username(username);

        if (userProfile == null) {
            throw new RuntimeException("UserProfile not found for user: " + username);
        }


        List<Vegetable> vegetables = vegetableDtos.stream()
                .map(dto -> vegetableRepository.findByName(dto.getName())
                        .orElseThrow(() -> new RuntimeException("Vegetable not found: " + dto.getName())))
                .collect(Collectors.toList());

        Advert advert = new Advert(title, description, userProfile, vegetables);
        Advert savedAdvert = advertRepository.save(advert);

        User user = userProfile.getUser();
        if (!user.getRoles().contains(User.Role.SELLER)) {
            user.addRole(User.Role.SELLER);
            userRepository.save(user);
        }

        for (MultipartFile image : images) {
            uploadedFileService.storeFile(image, savedAdvert);
        }

        return mapToDto(savedAdvert);

    }

    public List<AdvertDto> getAllAdverts() {
        List<Advert> adverts = advertRepository.findAll();
        return adverts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<AdvertDto> searchAdverts(String query) {
        List<Advert> adverts = advertRepository.searchByTitle(query);
        return adverts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private AdvertDto mapToDto(Advert advert) {

        List<VegetableDto> vegetableDtos = advert.getVegetables().stream()
                .map(veg -> new VegetableDto(veg.getCategory(), veg.getName()))
                .collect(Collectors.toList());

        List<String> imageUrls = advert.getPhotos().stream()
                .map(photo -> ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(photo.getFileName())
                        .toUriString())
                .collect(Collectors.toList());

        AdvertDto advertDto = new AdvertDto(
                advert.getId(),
                advert.getTitle(),
                advert.getDescription(),
                advert.getCreatedDate(),
                advert.getUserProfile().getUser().getUsername(),
                vegetableDtos,
                advert.getViewCount()
        );
        advertDto.setImageUrls(imageUrls);
        advertDto.setSaveCount(advert.getSaveCount());


        return advertDto;
    }

    public Advert mapToAdvert(AdvertDto advertDto) {
        Advert advert = new Advert();
        advert.setId(advertDto.getId());
        advert.setTitle(advertDto.getTitle());
        advert.setDescription(advertDto.getDescription());
        advert.setCreatedDate(advertDto.getCreatedDate());

        return advert;
    }

    @Transactional
    public void addImageToAdvert(Long advertId, String fileName, String username) {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new RuntimeException("Advert not found with id: " + advertId));


        if (!advert.getUserProfile().getUser().getUsername().equals(username)) {
            throw new RuntimeException("User is not authorized to modify this advert");
        }

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(fileName);
        uploadedFile.setFilePath("uploads/" + fileName);
        uploadedFile.setAdvert(advert);

        uploadedFileRepository.save(uploadedFile);

        advert.getPhotos().add(uploadedFile);
        advertRepository.save(advert);
    }

    public void checkUserAuthorization(Authentication authentication, Long advertId) {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new AdvertNotFoundException(advertId));
        UserProfile userProfile = advert.getUserProfile();
        if (!authentication.getName().equals(userProfile.getUser().getUsername())) {
            throw new RuntimeException("User is not authorized to modify this advert");
        }
    }

    public Advert getAdvertEntityById(Long id) {
        return advertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advert not found with id: " + id));
    }

    public AdvertDto getAdvertById(Long id) {
        Advert advert = getAdvertEntityById(id);
        return mapToDto(advert);
    }

    public List<AdvertDto> getAdvertsByUsername(String username) {
        List<Advert> adverts = advertRepository.findByUserProfile_User_Username(username);
        return adverts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void saveAdvert(Long advertId, String username) {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new RuntimeException("Advert not found with id: " + advertId));
        UserProfile userProfile = userProfileRepository.findByUser_Username(username);

        if (userProfile.getSavedAdverts().contains(advert)) {
            throw new RuntimeException("Advert is already saved.");
        }

        userProfile.getSavedAdverts().add(advert);
        advert.incrementSaveCount();
        userProfileRepository.save(userProfile);
        advertRepository.save(advert);
    }

    @Transactional
    public void unsaveAdvert(Long advertId, String username) {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new RuntimeException("Advert not found with id: " + advertId));
        UserProfile userProfile = userProfileRepository.findByUser_Username(username);

        if (!userProfile.getSavedAdverts().contains(advert)) {
            throw new RuntimeException("Advert is not saved.");
        }

        userProfile.getSavedAdverts().remove(advert);
        advert.decrementSaveCount();
        userProfileRepository.save(userProfile);
        advertRepository.save(advert);
    }

    public List<AdvertDto> getSavedAdverts(String username) {
        UserProfile userProfile = userProfileRepository.findByUser_Username(username);
        return userProfile.getSavedAdverts().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void incrementViewCount(Long advertId) {
        advertRepository.incrementViewCount(advertId);
    }

    @Transactional
    public void deleteAdvert(Long id) {
        Advert advert = advertRepository.findById(id)
                .orElseThrow(() -> new AdvertNotFoundException(id));

    }
}
