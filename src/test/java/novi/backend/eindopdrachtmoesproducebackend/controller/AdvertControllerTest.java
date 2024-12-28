package novi.backend.eindopdrachtmoesproducebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
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

import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AdvertController.class)
class AdvertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvertService advertServiceMock;

    @MockBean
    private UploadedFileService uploadedFileServiceMock;

    @MockBean
    private ObjectMapper objectMapper;

    @MockBean
    private Authentication authenticationMock;

    @MockBean
    private UserProfileRepository userProfileRepositoryMock;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(authenticationMock);
    }

//    @Test
//    @WithMockUser
//    void getAllAdverts_ShouldReturnListOfAdverts() throws Exception {
//        // Arrange
//        List<AdvertDto> adverts = Arrays.asList(new AdvertDto(), new AdvertDto());
//        when(advertServiceMock.getAllAdverts()).thenReturn(adverts);
//
//        // Act & Assert
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts"))
//                .andDo(print())
//                .andExpect(MockMvcResultMatchers.status().isOk());
//        verify(advertServiceMock).getAllAdverts();
//    }

    @Test
    @WithMockUser
    void getAdvertById_ShouldIncrementViewCountAndReturnAdvert() throws Exception {
        // Arrange
        Long id = 1L;
        AdvertDto advertDto = new AdvertDto();
        when(advertServiceMock.getAdvertById(id)).thenReturn(advertDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/adverts/{id}", id))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        verify(advertServiceMock).incrementViewCount(id);
        verify(advertServiceMock).getAdvertById(id);
    }

}





