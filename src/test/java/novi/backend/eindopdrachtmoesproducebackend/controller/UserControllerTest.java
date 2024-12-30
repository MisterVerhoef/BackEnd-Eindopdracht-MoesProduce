package novi.backend.eindopdrachtmoesproducebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.*;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.security.CustomUserDetails;
import novi.backend.eindopdrachtmoesproducebackend.security.JwtUtil;

import novi.backend.eindopdrachtmoesproducebackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.util.Set;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.mockito.Mockito.*;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserProfileRepository userProfileRepository;

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
    }

    @Test
    @WithMockUser
    void registerUser_ShouldReturnUserResponseDto() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto("test@example.com", "newUser", "password", true);
        when(userService.registerUser(anyString(), anyString(), anyString(), anyBoolean())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testUser"));
    }


    @Test
    @WithMockUser
    void loginUser_ShouldReturnToken() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto("testUser", "password");
        when(userService.loginUser(anyString(), anyString())).thenReturn("test-jwt-token");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));
    }

    @Test
    @WithMockUser
    void getUserDetails_ShouldReturnUserData() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto("new@email.com", "newUsername");

        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userService.updateUser(1L, "new@email.com", "newUsername")).thenReturn(testUser);

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }


    @Test
    @WithMockUser
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void promoteToSeller_ShouldReturnOk() throws Exception {
        doNothing().when(userService).promoteToSeller(1L);

        mockMvc.perform(put("/api/users/1/promote")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUser")
    void changePassword_ShouldReturnOk() throws Exception {
        // Arrange
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("encodedPassword");

        ChangePasswordDto passwordDto = new ChangePasswordDto();
        passwordDto.setCurrentPassword("oldPassword");
        passwordDto.setNewPassword("newPassword");

        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        doNothing().when(userService).changePassword(eq(1L), eq("oldPassword"), eq("newPassword"));

        // Act & Assert
        mockMvc.perform(put("/api/users/change-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk());


        verify(userService).changePassword(1L, "oldPassword", "newPassword");
    }


}
