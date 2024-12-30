package novi.backend.eindopdrachtmoesproducebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.LoginRequestDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.LoginResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.security.JwtAuthenticationFilter;
import novi.backend.eindopdrachtmoesproducebackend.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthenticationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationServiceMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /auth/login - returns 200 + token on success")
    void login_ShouldReturnToken() throws Exception {
        // ARRANGE
        LoginRequestDto loginRequest = new LoginRequestDto("TestUser", "Secret123");
        LoginResponseDto loginResponse = new LoginResponseDto("fake.jwt.token");

        when(authenticationServiceMock.login("TestUser", "Secret123"))
                .thenReturn(loginResponse);

        // ACT & ASSERT
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake.jwt.token"));

        verify(authenticationServiceMock).login("TestUser", "Secret123");
    }

    @Test
    @DisplayName("POST /auth/login - returns 400 if credentials invalid")
    void login_InvalidCredentials_ShouldReturn400() throws Exception {
        // ARRANGE
        LoginRequestDto loginRequest = new LoginRequestDto("wrongUser", "wrongPass");

        doThrow(new BadCredentialsException("Invalid username/email or password"))
                .when(authenticationServiceMock)
                .login("wrongUser", "wrongPass");

        // ACT & ASSERT
        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());

        verify(authenticationServiceMock).login("wrongUser", "wrongPass");
    }
}
