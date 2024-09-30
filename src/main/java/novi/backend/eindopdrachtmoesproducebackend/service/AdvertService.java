package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.dtos.AdvertDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.VegetableDto;
import novi.backend.eindopdrachtmoesproducebackend.exceptions.AdvertNotFoundException;
import novi.backend.eindopdrachtmoesproducebackend.models.Advert;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.models.Vegetable;
import novi.backend.eindopdrachtmoesproducebackend.repositories.AdvertRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.VegetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdvertService {

    @Autowired
    private AdvertRepository advertRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VegetableRepository vegetableRepository;

    @Transactional
    public AdvertDto createAdvert(String title, String description, List<VegetableDto> vegetableDtos, Authentication authentication) {
        String username = authentication.getName();
        UserProfile userProfile = userProfileRepository.findByUser_Username(username);

        if (userProfile == null) {
            throw new RuntimeException("UserProfile not found for user: " + username);
        }

        List<Vegetable> vegetables = vegetableDtos.stream()
                .map(vegetableDto -> vegetableRepository.findByName(vegetableDto.getName())
                        .orElseThrow(() -> new RuntimeException("Vegetable not found: " + vegetableDto.getName())))
                .collect(Collectors.toList());

        Advert advert = new Advert(title, description,userProfile, vegetables);
        Advert savedAdvert = advertRepository.save(advert);

        User user = userProfile.getUser();
        if (!user.getRoles().contains(User.Role.SELLER)) {
            user.addRole(User.Role.SELLER);
            userRepository.save(user);
        }

        return mapToDto(savedAdvert);

    }

    public List<AdvertDto> getAllAdverts() {
        List<Advert> adverts = advertRepository.findAll();
        return adverts.stream().map(this::mapToDto).collect(Collectors.toList());
    }

//    public AdvertDto getAdvertById(Long id) {
//        Optional<Advert> optionalAdvert = advertRepository.findById(id);
//        if (optionalAdvert.isPresent()) {
//            return mapToDto(optionalAdvert.get());
//        } else {
//            throw new RuntimeException("Advert not found with id: " + id);
//        }
//    }

    public AdvertDto getAdvertById(Long id) {
        return advertRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new AdvertNotFoundException(id));
    }

    private AdvertDto mapToDto(Advert advert) {

        List<VegetableDto> vegetableDtos = advert.getVegetables().stream()
                .map(veg -> new VegetableDto(veg.getCategory(), veg.getName()))
                .collect(Collectors.toList());

        return new AdvertDto(
                advert.getId(),
                advert.getTitle(),
                advert.getDescription(),
                advert.getCreatedDate(),
                advert.getUserProfile().getUsername(),
                vegetableDtos
        );
    }

}
