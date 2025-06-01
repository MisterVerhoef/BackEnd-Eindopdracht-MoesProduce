package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.VegetableDto;
import novi.backend.eindopdrachtmoesproducebackend.exceptions.AdvertNotFoundException;
import novi.backend.eindopdrachtmoesproducebackend.exceptions.UnauthorizedAccessException;
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

    private final AdvertRepository advertRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final VegetableRepository vegetableRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final UploadedFileService uploadedFileService;

    /****
     * Constructs an AdvertService with the required repositories and services for advert management.
     */
    public AdvertService(AdvertRepository advertRepository,
                         UserProfileRepository userProfileRepository,
                         UserRepository userRepository,
                         VegetableRepository vegetableRepository,
                         UploadedFileRepository uploadedFileRepository,
                         UploadedFileService uploadedFileService) {
        this.advertRepository = advertRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.vegetableRepository = vegetableRepository;
        this.uploadedFileRepository = uploadedFileRepository;
        this.uploadedFileService = uploadedFileService;
    }

    /**
     * Creates a new advert with the specified title, description, vegetables, and images for the authenticated user.
     *
     * Assigns the SELLER role to the user if not already present and stores uploaded images linked to the advert.
     *
     * @param title the title of the advert
     * @param description the description of the advert
     * @param vegetableDtos list of vegetables associated with the advert
     * @param images list of images to be uploaded for the advert
     * @param authentication the authentication object representing the current user
     * @return the created advert as a data transfer object
     *
     * @throws RuntimeException if the user profile or any specified vegetable is not found
     */
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

        // Rol toekennen indien nodig
        User user = userProfile.getUser();
        if (!user.getRoles().contains(User.Role.SELLER)) {
            user.addRole(User.Role.SELLER);
            userRepository.save(user);
        }

        // Afbeeldingen opslaan
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

    /****
     * Retrieves an advert entity by its ID.
     *
     * @param id the ID of the advert to retrieve
     * @return the Advert entity with the specified ID
     * @throws AdvertNotFoundException if no advert with the given ID exists
     */
    public Advert getAdvertEntityById(Long id) {
        return advertRepository.findById(id)
                .orElseThrow(() -> new AdvertNotFoundException(id));
    }

    /****
     * Retrieves an advert by its ID and returns it as a DTO.
     *
     * @param id the ID of the advert to retrieve
     * @return the advert represented as an AdvertDto
     * @throws AdvertNotFoundException if no advert with the given ID exists
     */
    public AdvertDto getAdvertById(Long id) {
        Advert advert = getAdvertEntityById(id);
        return mapToDto(advert);
    }


    public List<AdvertDto> getAdvertsByUsername(String username) {
        List<Advert> adverts = advertRepository.findByUserProfile_User_Username(username);
        return adverts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Saves an advert to the user's list of saved adverts.
     *
     * @param advertId the ID of the advert to save
     * @param username the username of the user saving the advert
     * @throws AdvertNotFoundException if the advert does not exist
     * @throws RuntimeException if the advert is already saved by the user
     */
    @Transactional
    public void saveAdvert(Long advertId, String username) {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new AdvertNotFoundException(advertId));
        UserProfile userProfile = userProfileRepository.findByUser_Username(username);

        if (userProfile.getSavedAdverts().contains(advert)) {
            throw new RuntimeException("Advert is already saved.");
        }

        userProfile.getSavedAdverts().add(advert);
        advert.incrementSaveCount();
        userProfileRepository.save(userProfile);
        advertRepository.save(advert);
    }

    /**
     * Removes an advert from the user's saved adverts list.
     *
     * @param advertId the ID of the advert to unsave
     * @param username the username of the user performing the operation
     * @throws AdvertNotFoundException if the advert does not exist
     * @throws RuntimeException if the advert is not saved by the user
     */
    @Transactional
    public void unsaveAdvert(Long advertId, String username) {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new AdvertNotFoundException(advertId));
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

    /**
     * Deletes an advert by its ID if the requesting user is the owner.
     *
     * Removes the advert from all users' saved adverts, deletes all associated photos, and then deletes the advert itself.
     *
     * @param id the ID of the advert to delete
     * @param username the username of the user requesting deletion
     * @throws AdvertNotFoundException if the advert does not exist
     * @throws UnauthorizedAccessException if the user is not the owner of the advert
     */
    @Transactional
    public void deleteAdvert(Long id, String username) {
        Advert advert = advertRepository.findById(id)
                .orElseThrow(() -> new AdvertNotFoundException(id));

        // Check of de gebruiker de eigenaar is
        if (!advert.getUserProfile().getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException(username, id);
        }

        List<UserProfile> allUsersWhoSaved = userProfileRepository.findAllBySavedAdvertsContains(advert);
        for (UserProfile userProfile : allUsersWhoSaved) {
            userProfile.getSavedAdverts().remove(advert);
            userProfileRepository.save(userProfile);
        }

        // Verwijder gekoppelde foto's
        List<UploadedFile> photos = advert.getPhotos();
        uploadedFileRepository.deleteAll(photos);

        // Verwijder de advertentie zelf
        advertRepository.delete(advert);
    }

    /****
     * Verifies that the authenticated user is authorized to access the specified advert.
     *
     * @param authentication the authentication object representing the current user
     * @param advertId the ID of the advert to check authorization for
     * @throws AdvertNotFoundException if the advert with the given ID does not exist
     * @throws UnauthorizedAccessException if the authenticated user is not the owner of the advert
     */
    public void checkUserAuthorization(Authentication authentication, Long advertId) {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new AdvertNotFoundException(advertId));
        UserProfile userProfile = advert.getUserProfile();
        if (!authentication.getName().equals(userProfile.getUser().getUsername())) {
            throw new UnauthorizedAccessException(authentication.getName(), advertId);
        }
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
        advertDto.setFormattedCreatedDate();

        return advertDto;
    }

    /**
     * Adds an image to an existing advert if the specified user is authorized.
     *
     * @param advertId the ID of the advert to which the image will be added
     * @param fileName the name of the image file to add
     * @param username the username of the user attempting to add the image
     *
     * @throws AdvertNotFoundException if the advert with the given ID does not exist
     * @throws UnauthorizedAccessException if the user is not the owner of the advert
     */
    @Transactional
    public void addImageToAdvert(Long advertId, String fileName, String username) {
        Advert advert = advertRepository.findById(advertId)
                .orElseThrow(() -> new AdvertNotFoundException(advertId));


        if (!advert.getUserProfile().getUser().getUsername().equals(username)) {
            throw new UnauthorizedAccessException(username, advertId);
        }

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(fileName);
        uploadedFile.setFilePath("uploads/" + fileName);
        uploadedFile.setAdvert(advert);

        uploadedFileRepository.save(uploadedFile);

        advert.getPhotos().add(uploadedFile);
        advertRepository.save(advert);
    }
}
