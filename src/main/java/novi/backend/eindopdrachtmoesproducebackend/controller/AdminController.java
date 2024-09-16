package novi.backend.eindopdrachtmoesproducebackend.controller;


import novi.backend.eindopdrachtmoesproducebackend.dtos.UserDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    UserService userService;

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

}
