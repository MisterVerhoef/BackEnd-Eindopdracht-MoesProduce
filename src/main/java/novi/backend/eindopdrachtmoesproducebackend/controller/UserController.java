package novi.backend.eindopdrachtmoesproducebackend.controller;

import jakarta.validation.Valid;
import novi.backend.eindopdrachtmoesproducebackend.dtos.*;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.security.CustomUserDetails;
import novi.backend.eindopdrachtmoesproducebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserController(UserService userService,
                          UserProfileRepository userProfileRepository) {
        this.userService = userService;
        this.userProfileRepository = userProfileRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {

        User user = userService.registerUser(
                userRegistrationDto.getEmail(),
                userRegistrationDto.getUsername(),
                userRegistrationDto.getPassword(),
                userRegistrationDto.getTermsAccepted()
        );

        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRoles(),
                user.getTermsAccepted());

        logger.info("New user registered: {}, Profile name: {}, Terms accepted: {}", user.getUsername(), user.getUserProfile().getName(), user.isTermsAccepted());

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        String token = userService.loginUser(loginRequestDto.getUsernameOrEmail(), loginRequestDto.getPassword());

        logger.info("User logged in: {}", loginRequestDto.getUsernameOrEmail());

        return ResponseEntity.ok(new LoginResponseDto(token));
    }


    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Long userId = ((CustomUserDetails) userDetails).getId();

            userService.changePassword(userId,
                    changePasswordDto.getCurrentPassword(),
                    changePasswordDto.getNewPassword());
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        UserResponseDto responseDto = new UserResponseDto(user.getId(), user.getEmail(), user.getUsername(), user.getRoles());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Updates the email and username of the specified user if the authenticated user matches the user ID.
     *
     * Returns a 403 Forbidden response if the authenticated user does not match the user being updated.
     * On success, returns the updated user information.
     *
     * @param userId the ID of the user to update
     * @param userUpdateDto the new email and username for the user
     * @return the updated user details, or 403 Forbidden if unauthorized
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateDto userUpdateDto,
            Authentication authentication) {


    Long authenticatedUserId = ((CustomUserDetails) authentication.getPrincipal()).getId();
    if (!authenticatedUserId.equals(userId)) {
        return ResponseEntity.status(403).build();
    }
        User updatedUser = userService.updateUser(userId, userUpdateDto.getEmail(), userUpdateDto.getUsername());

        UserResponseDto responseDto = new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getEmail(),
                updatedUser.getUsername(),
                updatedUser.getRoles(),
                updatedUser.getTermsAccepted()
        );

        return ResponseEntity.ok(responseDto);
    }

        /****
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     * @return a response entity with a success message upon successful deletion
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        logger.info("User with userId {} deleted successfully", userId);        return ResponseEntity.ok().body("user deleted successfully");
    }


    @PutMapping("/{userId}/promote")
    public ResponseEntity<String> promoteToSeller(@PathVariable Long userId) {
        userService.promoteToSeller(userId);
        return ResponseEntity.ok("User promoted to seller successfully");
    }
}