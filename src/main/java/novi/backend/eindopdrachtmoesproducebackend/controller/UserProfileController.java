package novi.backend.eindopdrachtmoesproducebackend.controller;

import jakarta.validation.Valid;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/profile")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
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
}