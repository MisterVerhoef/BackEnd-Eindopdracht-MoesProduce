package novi.backend.eindopdrachtmoesproducebackend.controller;

import jakarta.validation.Valid;
import novi.backend.eindopdrachtmoesproducebackend.dtos.*;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        User user = userService.registerUser(userRegistrationDto.getEmail(), userRegistrationDto.getUsername(), userRegistrationDto.getPassword());
        UserResponseDto responseDto = new UserResponseDto(user.getId(), user.getEmail(), user.getUsername(), user.getRoles());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        String token = userService.loginUser(loginRequestDto.getUsernameOrEmail(), loginRequestDto.getPassword());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserDetails(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        UserResponseDto responseDto = new UserResponseDto(user.getId(), user.getEmail(), user.getUsername(), user.getRoles());
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        User updatedUser = userService.updateUser(userId, userUpdateDto.getEmail(), userUpdateDto.getUsername());
        UserResponseDto responseDto = new UserResponseDto(updatedUser.getId(), updatedUser.getEmail(), updatedUser.getUsername(), updatedUser.getRoles());
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{userId}/promote")
    public ResponseEntity<String> promoteToSeller(@PathVariable Long userId) {
        userService.promoteToSeller(userId);
        return ResponseEntity.ok("User promoted to seller successfully");
    }
}
