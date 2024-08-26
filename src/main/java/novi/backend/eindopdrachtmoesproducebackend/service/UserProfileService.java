package novi.backend.eindopdrachtmoesproducebackend.service;

import jakarta.transaction.Transactional;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.exceptions.RecordNotFoundException;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserProfileService {


    private final UserRepository userRepository;

    @Autowired
    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserProfileDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            throw new RuntimeException("Profile not found");
        }

        return convertToDto(profile, user);
    }

    @Transactional
    public UserProfileDto updateUserProfile(String username, UserProfileDto userProfileDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            user.setUserProfile(profile);
        }

        profile.setName(userProfileDto.getName());
        profile.setDoB(userProfileDto.getDoB());
        profile.setAddress(userProfileDto.getAddress());

        userRepository.save(user);
        return convertToDto(profile, user);
    }

    private UserProfileDto convertToDto(UserProfile profile, User user) {
        return new UserProfileDto(
                profile.getName(),
                profile.getDoB(),
                profile.getAddress(),
                user.getUsername(),
                user.getEmail()
        );
    }
}


