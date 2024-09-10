package novi.backend.eindopdrachtmoesproducebackend.controller;

import novi.backend.eindopdrachtmoesproducebackend.models.Advert;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.AdvertRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/adverts")  // API endpoint voor adverts: /api/adverts/
public class AdvertController {

    @Autowired
    private AdvertRepository advertRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @GetMapping
    public List<Advert> getAllAdverts() {
        return advertRepository.findAll();
    }

    @PostMapping
    public Advert createAdvert(@RequestBody Advert advert, Authentication authentication) {

        String username = authentication.getName();

        // Haal het UserProfile op via de username
        UserProfile userProfile = userProfileRepository.findByUser_Username(username);
        if (userProfile == null) {
            throw new RuntimeException("UserProfile not found for the given user.");
        }

        // Koppel de advert aan het UserProfile
        advert.setUserProfile(userProfile);

        // Sla de advert op in de database
        return advertRepository.save(advert);
    }

}
