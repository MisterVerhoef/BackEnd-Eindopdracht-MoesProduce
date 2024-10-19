package novi.backend.eindopdrachtmoesproducebackend.controller;

import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UploadedFileResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.models.Advert;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import novi.backend.eindopdrachtmoesproducebackend.service.AdvertService;
import novi.backend.eindopdrachtmoesproducebackend.service.UploadedFileService;
import novi.backend.eindopdrachtmoesproducebackend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin
public class UploadedFileController {

    @Autowired
    private UploadedFileService uploadedFileService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private UserRepository userRepository;

//    public UploadedFileController(UploadedFileService uploadedFileService, UserProfileService userProfileService, AdvertService advertService) {
//        this.uploadedFileService = uploadedFileService;
//        this.userProfileService = userProfileService;
//        this.advertService = advertService;
//    }

    @PostMapping("/profile")
    public ResponseEntity<UploadedFileResponseDto> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            UserProfile userProfile = userProfileService.getUserProfileEntity(username);

            UploadedFile uploadedFile = uploadedFileService.storeFile(file, userProfile);

            userProfile.setProfileImage(uploadedFile);
            userProfileService.saveUserProfile(userProfile);

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(uploadedFile.getFileName())
                    .toUriString();

            UploadedFileResponseDto responseDto = new UploadedFileResponseDto(uploadedFile.getFileName(), imageUrl);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            e.printStackTrace(); // Add this line for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/advert/{advertId}")
    public ResponseEntity<UploadedFileResponseDto> uploadAdvertImage(
            @PathVariable Long advertId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            Advert advert = advertService.getAdvertEntityById(advertId);

            UploadedFile uploadedFile = uploadedFileService.storeFile(file, advert);

            advertService.addImageToAdvert(advertId, uploadedFile.getFileName(), username);

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(uploadedFile.getFileName())
                    .toUriString();

            UploadedFileResponseDto responseDto = new UploadedFileResponseDto(uploadedFile.getFileName(), imageUrl);

            return ResponseEntity.ok(responseDto);

        } catch (Exception e) {
            e.printStackTrace(); // Add this line for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

