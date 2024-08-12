package novi.backend.eindopdrachtmoesproducebackend.controller;

import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userEmail}/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping("")
    public ResponseEntity<Void> createUserProfile(@PathVariable String userEmail, @RequestBody UserProfileDto userProfileDto) {
        userProfileService.createUserProfile(userEmail, userProfileDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String userEmail) {
        UserProfileDto userProfile = userProfileService.getUserProfile(userEmail);
        return ResponseEntity.ok().body(userProfile);
    }

    @PutMapping("")
    public ResponseEntity<Void> updateUserProfile(@PathVariable String userEmail, @RequestBody UserProfileDto userProfileDto) {
        userProfileService.updateUserProfile(userEmail, userProfileDto);
        return ResponseEntity.noContent().build();
    }
}