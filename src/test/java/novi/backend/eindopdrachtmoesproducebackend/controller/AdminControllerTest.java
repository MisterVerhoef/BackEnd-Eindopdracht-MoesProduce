package novi.backend.eindopdrachtmoesproducebackend.controller;

import novi.backend.eindopdrachtmoesproducebackend.dtos.UserResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {


    @Mock
    private UserService userServiceMock;

    private AdminController adminController;

    @BeforeEach
    void setUp() {

        adminController = new AdminController(userServiceMock);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        UserResponseDto user1 = new UserResponseDto();
        UserResponseDto user2 = new UserResponseDto();
        List<UserResponseDto> userList = List.of(user1, user2);
        when(userServiceMock.getAllUsers()).thenReturn(userList);

        // Act
        ResponseEntity<List<UserResponseDto>> response = adminController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userList, response.getBody());
        verify(userServiceMock).getAllUsers();
    }

    @Test
    void changeUserRole_ShouldUpdateUserRole() {
        // Arrange
        Long userId = 1L;
        User.Role newRole = User.Role.ADMIN;

        // Act
        ResponseEntity<String> response = adminController.changeUserRole(userId, newRole);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User role updated successfully", response.getBody());
        verify(userServiceMock).changeUserRole(userId, newRole);
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsersExist() {
        // Arrange
        List<UserResponseDto> emptyList = List.of();
        when(userServiceMock.getAllUsers()).thenReturn(emptyList);

        // Act
        ResponseEntity<List<UserResponseDto>> response = adminController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(emptyList, response.getBody());
        verify(userServiceMock).getAllUsers();
    }

}

