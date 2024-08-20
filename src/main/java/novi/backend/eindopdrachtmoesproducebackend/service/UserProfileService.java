package novi.backend.eindopdrachtmoesproducebackend.service;

import jakarta.transaction.Transactional;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.exceptions.RecordNotFoundException;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserProfileService {


    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public UserProfile createUserProfile(Long userId, String name, LocalDate doB, String address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserProfile() != null) {
            throw new RuntimeException("User profile already exists");
        }

        UserProfile userProfile = new UserProfile(name, doB, address);
        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        return userProfileRepository.save(userProfile);
    }

    @Transactional
    public UserProfile updateUserProfile(Long userId, String name, LocalDate doB, String address) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        userProfile.setName(name);
        userProfile.setDoB(doB);
        userProfile.setAddress(address);

        return userProfileRepository.save(userProfile);
    }

    public UserProfile getUserProfile(Long userId) {
        return userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
    }

    @Transactional
    public void deleteUserProfile(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));

        User user = userProfile.getUser();
        user.setUserProfile(null);
        userRepository.save(user);

        userProfileRepository.delete(userProfile);
    }
}

