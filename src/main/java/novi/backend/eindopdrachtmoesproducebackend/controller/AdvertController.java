package novi.backend.eindopdrachtmoesproducebackend.controller;

import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.models.Advert;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.AdvertRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.service.AdvertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/adverts")  // API endpoint voor adverts: /api/adverts/
public class AdvertController {

    @Autowired
    private AdvertService advertService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @GetMapping
    public List<AdvertDto> getAllAdverts() {
        return advertService.getAllAdverts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdvertDto> getAdvertById(@PathVariable Long id) {
        AdvertDto advertDto = advertService.getAdvertById(id);
        return ResponseEntity.ok(advertDto);
    }

    @PostMapping
    public AdvertDto createAdvert(@RequestBody AdvertDto advertDto, Authentication authentication) {

        return advertService.createAdvert(advertDto.getTitle(), advertDto.getDescription(), authentication);
    }




}
