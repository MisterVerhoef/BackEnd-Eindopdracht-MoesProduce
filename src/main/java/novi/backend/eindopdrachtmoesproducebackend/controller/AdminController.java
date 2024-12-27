package novi.backend.eindopdrachtmoesproducebackend.controller;


import novi.backend.eindopdrachtmoesproducebackend.dtos.UserDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        System.out.println("Sending users list to the frontend: " + users);
        return ResponseEntity.ok(users);
    }

        @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> changeUserRole(@PathVariable Long userId, @RequestParam User.Role newRole) {
        userService.changeUserRole(userId, newRole);
        System.out.println("User role changed for userId "+ userId + " to " + newRole);
        return ResponseEntity.ok("User role updated successfully");
    }



}
