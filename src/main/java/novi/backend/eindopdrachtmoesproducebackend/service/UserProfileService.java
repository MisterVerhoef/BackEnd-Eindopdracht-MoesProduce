package novi.backend.eindopdrachtmoesproducebackend.service;

import jakarta.transaction.Transactional;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public UserProfileDto updateUserProfile(String currentUsername, UserProfileDto userProfileDto) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update username if changed and not null
        if (userProfileDto.getUsername() != null && !currentUsername.equals(userProfileDto.getUsername())) {
            if (userRepository.existsByUsername(userProfileDto.getUsername())) {
                throw new RuntimeException("Username already taken");
            }
            user.setUsername(userProfileDto.getUsername());
        }

        // Update email if changed and not null
        if (userProfileDto.getEmail() != null && !user.getEmail().equals(userProfileDto.getEmail())) {
            if (userRepository.existsByEmail(userProfileDto.getEmail())) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(userProfileDto.getEmail());
        }

        // Update other profile information
        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            user.setUserProfile(profile);
        }

        // Only update fields if they are not null
        if (userProfileDto.getName() != null) {
            profile.setName(userProfileDto.getName());
        }
        if (userProfileDto.getDoB() != null) {
            profile.setDoB(userProfileDto.getDoB());
        }
        if (userProfileDto.getAddress() != null) {
            profile.setAddress(userProfileDto.getAddress());
        }

        user = userRepository.save(user);
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