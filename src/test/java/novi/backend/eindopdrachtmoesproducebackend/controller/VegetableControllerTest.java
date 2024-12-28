package novi.backend.eindopdrachtmoesproducebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import novi.backend.eindopdrachtmoesproducebackend.dtos.VegetableDto;
import novi.backend.eindopdrachtmoesproducebackend.models.Vegetable;
import novi.backend.eindopdrachtmoesproducebackend.securtiy.JwtUtil;
import novi.backend.eindopdrachtmoesproducebackend.service.VegetableService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(VegetableController.class)
class VegetableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private VegetableService vegetableService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "TestUser")
    @DisplayName("GET /api/vegetables - should return a list of VegetableDto")
    void getAllVegetables_ShouldReturnList() throws Exception {
        // ARRANGE
        Vegetable veg1 = new Vegetable();
        veg1.setId(1L);
        veg1.setName("Tomaat");
        veg1.setCategory("Fruit");

        Vegetable veg2 = new Vegetable();
        veg2.setId(2L);
        veg2.setName("Paprika");
        veg2.setCategory("Groente");

        when(vegetableService.getAllVegetables()).thenReturn(List.of(veg1, veg2));

        // ACT & ASSERT
        mockMvc.perform(get("/api/vegetables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Tomaat")))
                .andExpect(jsonPath("$[0].category", is("Fruit")))
                .andExpect(jsonPath("$[1].name", is("Paprika")))
                .andExpect(jsonPath("$[1].category", is("Groente")));

        verify(vegetableService, times(1)).getAllVegetables();
    }

    @Test
    @WithMockUser(username = "TestUser")
    @DisplayName("POST /api/vegetables - should save a new vegetable (with CSRF)")
    void addVegetable_ShouldSaveAndReturn() throws Exception {
        // ARRANGE
        // We willen dat 'name' "Komkommer" is (zoals de test straks controleert) en 'category' "Groente"
        VegetableDto inputDto = new VegetableDto("Groente", "Komkommer");

        // De service zal een Vegetable teruggeven met deze velden
        Vegetable savedVeg = new Vegetable();
        savedVeg.setId(10L);
        savedVeg.setName("Komkommer");
        savedVeg.setCategory("Groente");

        when(vegetableService.saveVegetable(ArgumentMatchers.any(Vegetable.class)))
                .thenReturn(savedVeg);

        // Maak een JSON payload van inputDto
        String jsonPayload = objectMapper.writeValueAsString(inputDto);

        // ACT & ASSERT
        mockMvc.perform(post("/api/vegetables")
                        .with(csrf()) // Voeg CSRF-token toe
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Komkommer")))  // komt nu overeen
                .andExpect(jsonPath("$.category", is("Groente")));

        // Verifieer dat de service is aangeroepen
        verify(vegetableService, times(1))
                .saveVegetable(ArgumentMatchers.any(Vegetable.class));
    }

    @Test
    @WithMockUser(username = "TestUser")
    @DisplayName("POST /api/vegetables - should return 403 Forbidden without CSRF")
    void addVegetable_NoCsrf_ShouldFailWith403() throws Exception {
        // ARRANGE
        VegetableDto inputDto = new VegetableDto("Komkommer", "Groente");
        String jsonPayload = objectMapper.writeValueAsString(inputDto);

        // ACT & ASSERT (zonder .with(csrf()))
        mockMvc.perform(post("/api/vegetables")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isForbidden());

        verify(vegetableService, never()).saveVegetable(any(Vegetable.class));
    }

    @Test
    @DisplayName("POST /api/vegetables - without user authentication should return 403")
    void addVegetable_NoUser_ShouldFailWith403() throws Exception {
        // ARRANGE
        VegetableDto inputDto = new VegetableDto("Groente", "Komkommer");
        String jsonPayload = objectMapper.writeValueAsString(inputDto);

        // ACT & ASSERT (geen @WithMockUser)
        mockMvc.perform(post("/api/vegetables")
                        .with(csrf()) // Wel CSRF, maar geen ingelogde user
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isUnauthorized());

        verify(vegetableService, never()).saveVegetable(any(Vegetable.class));
    }
}
