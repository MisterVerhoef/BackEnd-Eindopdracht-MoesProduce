package novi.backend.eindopdrachtmoesproducebackend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Create test user
        User user = new User();
        user.setId(22L);
        user.setUsername("user8");
        user.setEmail("user8@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setTermsAccepted(true);
        user.addRole(User.Role.USER);

        UserProfile userProfile = new UserProfile();
        userProfile.setName("TestProfile");
        userProfile.setDoB(LocalDate.of(2000, 1, 1));
        userProfile.setAddress("TestAddress");
        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        userRepository.save(user);
    }
    @Test
    void testUserFlow() throws Exception {
        // Test login with existing user
        String loginResponse = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"usernameOrEmail\":\"user8\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String initialToken = new ObjectMapper().readTree(loginResponse).get("token").asText();

        // Test getting initial user profile with token
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + initialToken)
                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestProfile"))
                .andExpect(jsonPath("$.address").value("TestAddress"))
                .andExpect(jsonPath("$.doB").value("2000-01-01"));

        // Update user profile
        mockMvc.perform(put("/api/users/profile")
                .header("Authorization", "Bearer " + initialToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"UpdatedProfile\",\"address\":\"NewAddress\",\"doB\":\"2001-02-02\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedProfile"))
                .andExpect(jsonPath("$.address").value("NewAddress"))
                .andExpect(jsonPath("$.doB").value("2001-02-02"));

        // Update user credentials
        mockMvc.perform(put("/api/users/8")
                .header("Authorization", "Bearer " + initialToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"newemail@example.com\",\"username\":\"newUsername123\"}"))
                .andExpect(status().isOk());

        // Login with new username to get fresh token
        String newLoginResponse = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"usernameOrEmail\":\"newUsername123\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

       String newToken = new ObjectMapper().readTree(newLoginResponse).get("token").asText();

        // Verify updated profile with new token
        mockMvc.perform(get("/api/users/profile")
                .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedProfile"))
                .andExpect(jsonPath("$.address").value("NewAddress"))
                .andExpect(jsonPath("$.doB").value("2001-02-02"));

        // Verify user details with new token
        mockMvc.perform(get("/api/users/8")
                .header("Authorization", "Bearer " + newToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUsername123"));
    }
}


