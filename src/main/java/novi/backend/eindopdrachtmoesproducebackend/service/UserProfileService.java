package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.exceptions.RecordNotFoundException;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final UserRepository userRepository;

    public UserProfileService(UserProfileRepository userProfileRepository, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
    }

    public void createUserProfile(String userEmail, UserProfileDto userProfileDto) {
        User user = userRepository.findById(userEmail)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));

        UserProfile userProfile = new UserProfile();
        updateUserProfileFromDto(userProfile, userProfileDto);
        user.setUserProfile(userProfile);

        userRepository.save(user);
        userProfileRepository.save(userProfile);
    }

    public UserProfileDto getUserProfile(String userEmail) {
        User user = userRepository.findById(userEmail)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));

        UserProfile userProfile = user.getUserProfile();
        if (userProfile == null) {
            throw new RecordNotFoundException("User profile not found");
        }

        return fromUserProfile(userProfile);
    }

    public void updateUserProfile(String userEmail, UserProfileDto userProfileDto) {
        User user = userRepository.findById(userEmail)
                .orElseThrow(() -> new RecordNotFoundException("User not found"));

        UserProfile userProfile = user.getUserProfile();
        if (userProfile == null) {
            throw new RecordNotFoundException("User profile not found");
        }

        updateUserProfileFromDto(userProfile, userProfileDto);
        userProfileRepository.save(userProfile);
    }

    private UserProfileDto fromUserProfile(UserProfile userProfile) {
        UserProfileDto dto = new UserProfileDto();
        dto.setFirstName(userProfile.getFirstName());
        dto.setLastName(userProfile.getLastName());
        dto.setDoB(userProfile.getDoB());
        dto.setAddress(userProfile.getAddress());
        return dto;
    }

    private void updateUserProfileFromDto(UserProfile userProfile, UserProfileDto userProfileDto) {
        userProfile.setFirstName(userProfileDto.getFirstName());
        userProfile.setLastName(userProfileDto.getLastName());
        userProfile.setDoB(userProfileDto.getDoB());
        userProfile.setAddress(userProfileDto.getAddress());
    }
}

