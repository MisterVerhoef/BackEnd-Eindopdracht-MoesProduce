package novi.backend.eindopdrachtmoesproducebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.VegetableDto;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.securtiy.JwtUtil;
import novi.backend.eindopdrachtmoesproducebackend.service.AdvertService;
import novi.backend.eindopdrachtmoesproducebackend.service.UploadedFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @BeforeEach
    void setUp() {

    }
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

    @Test
    @WithMockUser
    @DisplayName("GET /api/adverts - should return a list of AdvertDto")
    void getAllAdverts_ShouldReturnList() throws Exception {
        // ARRANGE
        // We gebruiken de no-args constructor + setters:
        AdvertDto ad1 = new AdvertDto();
        ad1.setId(1L);
        ad1.setTitle("Title1");
        ad1.setDescription("Desc1");
        ad1.setCreatedDate(LocalDate.now());
        ad1.setUsername("user1");
        ad1.setViewCount(0);

        AdvertDto ad2 = new AdvertDto();
        ad2.setId(2L);
        ad2.setTitle("Title2");
        ad2.setDescription("Desc2");
        ad2.setCreatedDate(LocalDate.now());
        ad2.setUsername("user2");
        ad2.setViewCount(10);

        when(advertServiceMock.getAllAdverts()).thenReturn(List.of(ad1, ad2));

        // ACT & ASSERT
        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Title1")))
                .andExpect(jsonPath("$[1].viewCount", is(10)));

        verify(advertServiceMock).getAllAdverts();
    }



    @Test
    @WithMockUser
    @DisplayName("GET /api/adverts/{id} - should increment viewCount and return AdvertDto")
    void getAdvertById_ShouldIncrementViewAndReturnAdvert() throws Exception {
        Long advertId = 99L;

        AdvertDto adDto = new AdvertDto();
        adDto.setId(advertId);
        adDto.setTitle("Title99");
        adDto.setDescription("Desc99");
        adDto.setCreatedDate(LocalDate.now());
        adDto.setUsername("testUser");
        adDto.setViewCount(5);

        doNothing().when(advertServiceMock).incrementViewCount(advertId);
        when(advertServiceMock.getAdvertById(advertId)).thenReturn(adDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts/{id}", advertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(99)))
                .andExpect(jsonPath("$.viewCount", is(5)));

        verify(advertServiceMock).incrementViewCount(advertId);
        verify(advertServiceMock).getAdvertById(advertId);
    }



    @WithMockUser
    @Test
    void getAdvertById_ShouldReturnAdvert() throws Exception {
        Long id = 1L;
        AdvertDto advertDto = createTestAdvertDto(id, "Test Advert");
        when(advertServiceMock.getAdvertById(id)).thenReturn(advertDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Test Advert"));
    }
    @Test
    @WithMockUser
    @DisplayName("GET /api/adverts/search?query=xyz - returns a list of matching adverts")
    void searchAdverts_ShouldReturnMatches() throws Exception {
        String searchQuery = "xyz";

        AdvertDto ad1 = new AdvertDto();
        ad1.setId(1L);
        ad1.setTitle("Result1");
        ad1.setDescription("Desc1");

        AdvertDto ad2 = new AdvertDto();
        ad2.setId(2L);
        ad2.setTitle("Result2");
        ad2.setDescription("Desc2");

        when(advertServiceMock.searchAdverts(searchQuery)).thenReturn(Arrays.asList(ad1, ad2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts/search")
                        .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Result1")))
                .andExpect(jsonPath("$[1].title", is("Result2")));

        verify(advertServiceMock).searchAdverts(searchQuery);
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("User Advert 1"))
                .andExpect(jsonPath("$[1].title").value("User Advert 2"));
    }

    @Test
    @WithMockUser("testUser")
    @DisplayName("POST /api/adverts/{id}/save - should save the advert for the user")
    void saveAdvert_ShouldSave() throws Exception {
        Long advertId = 1L;
        doNothing().when(advertServiceMock).saveAdvert(advertId, "testUser");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/adverts/{id}/save", advertId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(advertServiceMock).saveAdvert(advertId, "testUser");
    }

    @Test
    @WithMockUser("testUser")
    @DisplayName("POST /api/adverts/{id}/unsave - should unsave the advert")
    void unsaveAdvert_ShouldUnsave() throws Exception {
        Long advertId = 2L;
        doNothing().when(advertServiceMock).unsaveAdvert(advertId, "testUser");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/adverts/{id}/unsave", advertId)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(advertServiceMock).unsaveAdvert(advertId, "testUser");
    }

    @Test
    @WithMockUser("testUser")
    @DisplayName("DELETE /api/adverts/{id} - should delete advert if authorized")
    void deleteAdvert_ShouldDelete() throws Exception {
        Long advertId = 50L;
        doNothing().when(advertServiceMock).checkUserAuthorization(any(), eq(advertId));
        doNothing().when(advertServiceMock).deleteAdvert(advertId, "testUser");

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/adverts/{id}", advertId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(advertServiceMock).checkUserAuthorization(any(), eq(advertId));
        verify(advertServiceMock).deleteAdvert(advertId, "testUser");
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/adverts - createAdvert success")
    void createAdvert_ShouldSucceed() throws Exception {
        // ARRANGE
        String title = "NewTitle";
        String description = "NewDesc";


        List<VegetableDto> vegetables = Arrays.asList(
                new VegetableDto("Komkommer", "Groente"),
                new VegetableDto("Tomaat", "Fruit")
        );
        String vegetablesJson = objectMapper.writeValueAsString(vegetables);

        MockMultipartFile imageFile = new MockMultipartFile("images", "image1.jpg", "image/jpeg", new byte[] {1,2,3});


        AdvertDto resultDto = new AdvertDto();
        resultDto.setId(100L);
        resultDto.setTitle(title);
        resultDto.setDescription(description);
        resultDto.setUsername("testUser");

        when(advertServiceMock.createAdvert(
                eq(title),
                eq(description),
                anyList(),
                anyList(),
                any()
        )).thenReturn(resultDto);

        // ACT & ASSERT
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/adverts")
                        .file(imageFile)
                        .param("title", title)
                        .param("description", description)
                        .param("vegetables", vegetablesJson)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.title", is("NewTitle")));

        verify(advertServiceMock).createAdvert(eq(title), eq(description), anyList(), anyList(), any());
    }

    @Test
    @WithMockUser("testUser")
    @DisplayName("GET /api/adverts/saved - should return saved adverts")
    void getSavedAdverts_ShouldReturnList() throws Exception {
        AdvertDto saved1 = new AdvertDto();
        saved1.setId(7L);
        saved1.setTitle("Saved Advert1");
        saved1.setUsername("testUser");

        AdvertDto saved2 = new AdvertDto();
        saved2.setId(8L);
        saved2.setTitle("Saved Advert2");
        saved2.setUsername("testUser");

        when(advertServiceMock.getSavedAdverts("testUser"))
                .thenReturn(Arrays.asList(saved1, saved2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts/saved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(7)))
                .andExpect(jsonPath("$[1].title", is("Saved Advert2")));

        verify(advertServiceMock).getSavedAdverts("testUser");
    }


    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("POST /api/adverts - createAdvert fails with invalid JSON")
    void createAdvert_InvalidJson_ShouldReturnBadRequest() throws Exception {
        // ARRANGE
        String title = "Title";
        String description = "Desc";

        String invalidVegetablesJson = "{ not valid JSON array }";

        MockMultipartFile imageFile = new MockMultipartFile("images", "image1.jpg", "image/jpeg", new byte[] {1,2,3});

        // ACT & ASSERT
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/adverts")
                        .file(imageFile)
                        .param("title", title)
                        .param("description", description)
                        .param("vegetables", invalidVegetablesJson)
                        .with(csrf())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        verify(advertServiceMock, never()).createAdvert(anyString(), anyString(), anyList(), anyList(), any());
    }

    @Test
    @WithMockUser("testUser")
    @DisplayName("POST /api/adverts/{advertId}/upload-image - success")
    void uploadAdvertImage_ShouldReturnOk() throws Exception {
        Long advertId = 123L;
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[] {1,2,3});

        UploadedFile upFile = new UploadedFile();
        upFile.setFileName("image.jpg");
        upFile.setFilePath("uploads/image.jpg");

        when(uploadedFileServiceMock.storeFile(file)).thenReturn(upFile);

        doNothing().when(advertServiceMock).addImageToAdvert(eq(advertId), eq("image.jpg"), eq("testUser"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/adverts/{advertId}/upload-image", advertId)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", is("image.jpg")))
                .andExpect(jsonPath("$.fileUrl", containsString("/uploads/image.jpg")));

        verify(uploadedFileServiceMock).storeFile(file);
        verify(advertServiceMock).addImageToAdvert(advertId, "image.jpg", "testUser");
    }

    @Test
    @WithMockUser("testUser")
    @DisplayName("POST /api/adverts/{advertId}/upload-image - fails with server error")
    void uploadAdvertImage_Fails_ShouldReturn500() throws Exception {
        Long advertId = 456L;
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", new byte[]{1,2,3});

        when(uploadedFileServiceMock.storeFile(file)).thenThrow(new RuntimeException("Storage error"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/adverts/{advertId}/upload-image", advertId)
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isInternalServerError());

        verify(advertServiceMock, never()).addImageToAdvert(anyLong(), anyString(), anyString());
    }

}

