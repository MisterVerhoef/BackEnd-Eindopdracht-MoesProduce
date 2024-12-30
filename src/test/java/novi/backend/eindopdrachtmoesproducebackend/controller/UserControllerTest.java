package novi.backend.eindopdrachtmoesproducebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.*;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.security.JwtUtil;
import novi.backend.eindopdrachtmoesproducebackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserProfileRepository userProfileRepository;

    @MockBean
    private Authentication authentication;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setRoles(Set.of(User.Role.USER));
        testUser.setTermsAccepted(true);

        UserProfile profile = new UserProfile();
        profile.setName("TestProfile");
        testUser.setUserProfile(profile);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser
    void registerUser_ShouldReturnUserResponseDto() throws Exception {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto("test@example.com", "newUser", "password", true);
        UserResponseDto responseDto = new UserResponseDto(1L, "test@example.com", "newUser", Set.of(User.Role.USER), true);
        when(userService.registerUser(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf())) // Voeg de CSRF token toe met .with(csrf())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService).registerUser("test@example.com", "newUser", "password", true);
    }

    @Test
    @WithMockUser
    void loginUser_ShouldReturnLoginResponseDto() throws Exception {
        // Arrange
        LoginRequestDto dto = new LoginRequestDto("testUser", "password");
        LoginResponseDto responseDto = new LoginResponseDto("token");
        when(userService.loginUser("testUser", "password")).thenReturn("token");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService).loginUser("testUser", "password");
    }

//    @Test
//    @WithMockUser
//    void changePassword_ShouldReturnSuccessMessage() throws Exception {
//        // Arrange
//        ChangePasswordDto dto = new ChangePasswordDto("oldPassword", "newPassword");
//        CustomUserDetails userDetails = new CustomUserDetails(testUser);
//        when(userService.changePassword(anyLong(), anyString(), anyString())).thenThrow(new RuntimeException("Wrong password"));
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/change-password")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest());
//        verify(userService).changePassword(userDetails.getId(), "oldPassword", "newPassword");
//    }

    @Test
    @WithMockUser
    void getUserDetails_ShouldReturnUserResponseDto() throws Exception {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService).getUserById(1L);
    }

    @Test
    @WithMockUser(username = "testUser")
    void updateUser_ShouldReturnUpdatedUserResponseDto() throws Exception {
        // Arrange
        UserUpdateDto dto = new UserUpdateDto("newEmail@example.com", "newUsername");
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userService.updateUser(1L, "newEmail@example.com", "newUsername")).thenReturn(testUser);
        when(authentication.getName()).thenReturn("testUser");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService).updateUser(1L, "newEmail@example.com", "newUsername");
    }

    @Test
    @WithMockUser
    void deleteUser_ShouldDeleteUser() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser
    void promoteToSeller_ShouldPromoteUser() throws Exception {
        // Arrange
        doNothing().when(userService).promoteToSeller(1L);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/1/promote"))
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(userService).promoteToSeller(1L);
    }
}