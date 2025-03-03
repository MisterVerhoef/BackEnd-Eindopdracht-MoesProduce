package novi.backend.eindopdrachtmoesproducebackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UploadedFileResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.VegetableDto;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.service.AdvertService;
import novi.backend.eindopdrachtmoesproducebackend.service.UploadedFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AdvertService advertService;

    @Autowired
    private UploadedFileService uploadedFileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserProfileRepository userProfileRepository;

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

    @PostMapping
    public ResponseEntity<AdvertDto> createAdvert(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("vegetables") String vegetablesJson,
            @RequestPart("images") List<MultipartFile> images,
            Authentication authentication) {

        try {
            List<VegetableDto> vegetables = objectMapper.readValue(vegetablesJson, new TypeReference<>(){});
            logger.info("Received create advert request. Title: {}, Description: {}, Vegetables count: {}, Images count: {}",
                    title, description, vegetables.size(), images.size());

            AdvertDto createdAdvert = advertService.createAdvert(title, description, vegetables, images, authentication);
            return ResponseEntity.ok(createdAdvert);
        } catch (JsonProcessingException e) {
            logger.error("Error parsing vegetables JSON", e);
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Error creating advert", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{advertId}/upload-image")
    public ResponseEntity<UploadedFileResponseDto> uploadAdvertImage(
            @PathVariable Long advertId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String username = authentication.getName();

            UploadedFile uploadedFile = uploadedFileService.storeFile(file);
            advertService.addImageToAdvert(advertId, uploadedFile.getFileName(), username);

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(uploadedFile.getFileName())
                    .toUriString();

            UploadedFileResponseDto responseDto = new UploadedFileResponseDto(uploadedFile.getFileName(), imageUrl);

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            logger.error("Error uploading advert image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

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
