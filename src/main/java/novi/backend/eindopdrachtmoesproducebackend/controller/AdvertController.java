package novi.backend.eindopdrachtmoesproducebackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UploadedFileResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.VegetableDto;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.service.AdvertService;
import novi.backend.eindopdrachtmoesproducebackend.service.UploadedFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/adverts")
public class AdvertController {

    private static final Logger logger = LoggerFactory.getLogger(AdvertController.class);

    private final AdvertService advertService;
    private final UploadedFileService uploadedFileService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs an AdvertController with the required services and object mapper.
     *
     * @param advertService service for advert-related business logic
     * @param uploadedFileService service for handling uploaded files
     * @param objectMapper object mapper for JSON processing
     */
    public AdvertController(AdvertService advertService, 
                           UploadedFileService uploadedFileService, 
                           ObjectMapper objectMapper) {
        this.advertService = advertService;
        this.uploadedFileService = uploadedFileService;
        this.objectMapper = objectMapper;
    }

    /****
     * Retrieves all adverts.
     *
     * @return a list of all adverts as AdvertDto objects
     */
    @GetMapping
    public List<AdvertDto> getAllAdverts() {
        return advertService.getAllAdverts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertDto> getAdvertById(@PathVariable Long id) {
        advertService.incrementViewCount(id);
        AdvertDto advertDto = advertService.getAdvertById(id);
        return ResponseEntity.ok(advertDto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AdvertDto>> searchAdverts(@RequestParam("query") String query) {
        List<AdvertDto> results = advertService.searchAdverts(query);
        return ResponseEntity.ok(results);
    }

    /****
     * Creates a new advert with the specified title, description, vegetables, and images for the authenticated user.
     *
     * @param title the title of the advert
     * @param description the description of the advert
     * @param vegetablesJson a JSON string representing a list of vegetables associated with the advert
     * @param images a list of image files to be uploaded with the advert
     * @return the created advert as an AdvertDto
     * @throws JsonProcessingException if the vegetablesJson cannot be parsed
     */
    @PostMapping
    public ResponseEntity<AdvertDto> createAdvert(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("vegetables") String vegetablesJson,
            @RequestPart("images") List<MultipartFile> images,
            Authentication authentication) throws JsonProcessingException {

        List<VegetableDto> vegetables = objectMapper.readValue(vegetablesJson, new TypeReference<>(){});
        logger.info("Received create advert request. Title: {}, Description: {}, Vegetables count: {}, Images count: {}",
                title, description, vegetables.size(), images.size());

        AdvertDto createdAdvert = advertService.createAdvert(title, description, vegetables, images, authentication);
        return ResponseEntity.ok(createdAdvert);
    }

    /**
     * Handles image upload for a specific advert and associates the uploaded image with the advert and authenticated user.
     *
     * @param advertId the ID of the advert to which the image will be added
     * @param file the image file to upload
     * @return a response containing the uploaded file's name and accessible URL
     */
    @PostMapping("/{advertId}/upload-image")
    public ResponseEntity<UploadedFileResponseDto> uploadAdvertImage(
            @PathVariable Long advertId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String username = authentication.getName();

        UploadedFile uploadedFile = uploadedFileService.storeFile(file);
        advertService.addImageToAdvert(advertId, uploadedFile.getFileName(), username);

        String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(uploadedFile.getFileName())
                .toUriString();

        UploadedFileResponseDto responseDto = new UploadedFileResponseDto(uploadedFile.getFileName(), imageUrl);

        return ResponseEntity.ok(responseDto);
    }

    /****
     * Retrieves all adverts created by the authenticated user.
     *
     * @param principal the security principal representing the authenticated user
     * @return a response containing a list of adverts belonging to the user
     */
    @GetMapping("/user")
    public ResponseEntity<List<AdvertDto>> getAdvertsByUser(Principal principal) {
        String username = principal.getName();
        List<AdvertDto> userAdverts = advertService.getAdvertsByUsername(username);
        return ResponseEntity.ok(userAdverts);
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<Void> saveAdvert(@PathVariable Long id, Principal principal) {
        advertService.saveAdvert(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unsave")
    public ResponseEntity<Void> unsaveAdvert(@PathVariable Long id, Principal principal) {
        advertService.unsaveAdvert(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/saved")
    public ResponseEntity<List<AdvertDto>> getSavedAdverts(Principal principal) {
        List<AdvertDto> savedAdverts = advertService.getSavedAdverts(principal.getName());
        return ResponseEntity.ok(savedAdverts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvert(@PathVariable Long id, Authentication authentication) {

        advertService.checkUserAuthorization(authentication, id);

        advertService.deleteAdvert(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
