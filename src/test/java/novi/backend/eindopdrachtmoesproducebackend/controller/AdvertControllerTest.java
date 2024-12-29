package novi.backend.eindopdrachtmoesproducebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.securtiy.JwtUtil;
import novi.backend.eindopdrachtmoesproducebackend.service.AdvertService;
import novi.backend.eindopdrachtmoesproducebackend.service.UploadedFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AdvertController.class)
class AdvertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AdvertService advertServiceMock;

    @MockBean
    private UploadedFileService uploadedFileServiceMock;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserProfileRepository userProfileRepositoryMock;

    private AdvertDto createTestAdvertDto(Long id, String title) {
        AdvertDto dto = new AdvertDto();
        dto.setId(id);
        dto.setTitle(title);
        dto.setDescription("Test Description");
        dto.setCreatedDate(LocalDate.now());
        dto.setUsername("testUser");
        dto.setVegetables(new ArrayList<>());


        return dto;
    }

    @WithMockUser
    @Test
    void getAdvertById_ShouldReturnAdvert() throws Exception {
        Long id = 1L;
        AdvertDto advertDto = createTestAdvertDto(id, "Test Advert");
        when(advertServiceMock.getAdvertById(id)).thenReturn(advertDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts/{id}", id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Advert"));
    }
    @WithMockUser
    @Test
    void searchAdverts_ShouldReturnMatchingAdverts() throws Exception {
        // Arrange
        String searchQuery = "tomato";
        List<AdvertDto> searchResults = Arrays.asList(
                createTestAdvertDto(1L, "Fresh Tomatoes"),
                createTestAdvertDto(2L, "Tomato Plants")
        );
        when(advertServiceMock.searchAdverts(searchQuery)).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts/search")
                        .param("query", searchQuery))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Fresh Tomatoes"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("Tomato Plants"));
    }

    @Test
    @WithMockUser("testUser")
    void getAdvertsByUser_ShouldReturnUserAdverts() throws Exception {
        // Arrange
        List<AdvertDto> userAdverts = Arrays.asList(
                createTestAdvertDto(1L, "User Advert 1"),
                createTestAdvertDto(2L, "User Advert 2")
        );
        when(advertServiceMock.getAdvertsByUsername("testUser")).thenReturn(userAdverts);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts/user"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("User Advert 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].title").value("User Advert 2"));
    }

    @Test
    @WithMockUser("testUser")
    void saveAdvert_ShouldReturnOk() throws Exception {
        // Arrange
        Long advertId = 1L;
        doNothing().when(advertServiceMock).saveAdvert(eq(advertId), eq("testUser"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/adverts/{id}/save", advertId)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(advertServiceMock).saveAdvert(advertId, "testUser");
    }

    @Test
    @WithMockUser("testUser")
    void deleteAdvert_ShouldReturnNoContent() throws Exception {
        // Arrange
        Long advertId = 1L;
        doNothing().when(advertServiceMock).checkUserAuthorization(any(), eq(advertId));
        doNothing().when(advertServiceMock).deleteAdvert(eq(advertId), eq("testUser"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/adverts/{id}", advertId)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(advertServiceMock).checkUserAuthorization(any(), eq(advertId));
        verify(advertServiceMock).deleteAdvert(advertId, "testUser");
    }


}

