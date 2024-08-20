package novi.backend.eindopdrachtmoesproducebackend.controller;

import jakarta.validation.Valid;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public ResponseEntity<UserProfileDto> createUserProfile(@RequestParam Long userId, @Valid @RequestBody UserProfileDto userProfileDto) {
        UserProfile createdProfile = userProfileService.createUserProfile(userId, userProfileDto.getName(), userProfileDto.getDoB(), userProfileDto.getAddress());
        UserProfileDto responseDto = new UserProfileDto(createdProfile.getName(), createdProfile.getDoB(), createdProfile.getAddress());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable Long userId) {
        UserProfile userProfile = userProfileService.getUserProfile(userId);
        UserProfileDto profileDto = new UserProfileDto(userProfile.getName(), userProfile.getDoB(), userProfile.getAddress());
        return ResponseEntity.ok(profileDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDto> updateUserProfile(@PathVariable Long userId, @Valid @RequestBody UserProfileDto userProfileDto) {
        UserProfile updatedProfile = userProfileService.updateUserProfile(userId, userProfileDto.getName(), userProfileDto.getDoB(), userProfileDto.getAddress());
        UserProfileDto responseDto = new UserProfileDto(updatedProfile.getName(), updatedProfile.getDoB(), updatedProfile.getAddress());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long userId) {
        userProfileService.deleteUserProfile(userId);
        return ResponseEntity.noContent().build();
    }
}