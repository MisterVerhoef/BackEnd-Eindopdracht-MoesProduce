package novi.backend.eindopdrachtmoesproducebackend.controller;

import jakarta.validation.Valid;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UploadedFileResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.service.UploadedFileService;
import novi.backend.eindopdrachtmoesproducebackend.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/api/users/profile")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UploadedFileService uploadedFileService;


    @Autowired
    public UserProfileController(UserProfileService userProfileService, UploadedFileService uploadedFileService) {
        this.userProfileService = userProfileService;
        this.uploadedFileService = uploadedFileService;
    }

    @GetMapping
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            logger.error("Authentication object is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            UserProfileDto profileDto = userProfileService.getUserProfile(authentication.getName());
            logger.info("Successfully retrieved profile for user: {}", authentication.getName());
            return ResponseEntity.ok(profileDto);
        } catch (Exception e) {
            logger.error("Error retrieving profile for user: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<UserProfileDto> updateUserProfile(@Valid @RequestBody UserProfileDto userProfileDto, Authentication authentication) {
        if (authentication == null) {
            logger.error("Authentication object is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            UserProfileDto updatedProfileDto = userProfileService.updateUserProfile(authentication.getName(), userProfileDto);
            logger.info("User profile updated successfully for user: {}", authentication.getName());
            System.out.println("User profile updated successfully for user: " + authentication.getName());
            return ResponseEntity.ok(updatedProfileDto);
        } catch (Exception e) {
            logger.error("Error updating profile for user: {}", authentication.getName(), e);
            System.out.println("Error updating profile for user: " + authentication.getName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/upload-profile-image")
    public ResponseEntity<UploadedFileResponseDto> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication)
        {
        try {
            String username = authentication.getName();
            UserProfile userProfile = userProfileService.getUserProfileEntity(username);

            UploadedFile uploadedFile = uploadedFileService.storeFile(file);

            userProfile.setProfileImage(uploadedFile);
            userProfileService.saveUserProfile(userProfile);

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(uploadedFile.getFileName())
                    .toUriString();


            UploadedFileResponseDto responseDto = new UploadedFileResponseDto(uploadedFile.getFileName(), imageUrl);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            logger.error("Error uploading profile image for user: {}", authentication.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

