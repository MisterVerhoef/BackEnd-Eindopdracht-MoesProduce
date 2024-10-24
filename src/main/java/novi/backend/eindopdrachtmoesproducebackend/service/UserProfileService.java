package novi.backend.eindopdrachtmoesproducebackend.service;

import jakarta.transaction.Transactional;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserProfileDto;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UploadedFileRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserProfileRepository;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class UserProfileService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private UploadedFileService uploadedFileService;


    public UserProfileService() {

    }

    public UserProfileDto getUserProfile(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            throw new RuntimeException("Profile not found" + username);
        }

        return convertToDto(profile, user);
    }

    public void saveUserProfile(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }

    @Transactional
    public UserProfileDto updateUserProfile(String currentUsername, UserProfileDto userProfileDto) {
        User user = userRepository.findByUsernameIgnoreCase(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));


        if (userProfileDto.getUsername() != null && !currentUsername.equals(userProfileDto.getUsername())) {
            if (userRepository.existsByUsernameIgnoreCase(userProfileDto.getUsername())) {
                throw new RuntimeException("Username already taken");
            }
            user.setUsername(userProfileDto.getUsername());
        }


        if (userProfileDto.getEmail() != null && !user.getEmail().equals(userProfileDto.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(userProfileDto.getEmail())) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(userProfileDto.getEmail());
        }


        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            user.setUserProfile(profile);
        }


        if (userProfileDto.getName() != null) {
            profile.setName(userProfileDto.getName());
        }
        if (userProfileDto.getDoB() != null) {
            profile.setDoB(userProfileDto.getDoB());
        }
        if (userProfileDto.getAddress() != null) {
            profile.setAddress(userProfileDto.getAddress());
        }

        userRepository.save(user);
        userProfileRepository.save(profile);

        return convertToDto(profile, user);
    }

    public UserProfile getUserProfileEntity(String username) {

        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            throw new RuntimeException("Profile not found" + username);
        }

        return profile;
    }

    private UserProfileDto convertToDto(UserProfile profile, User user) {
        UserProfileDto dto =  new UserProfileDto(
                profile.getName(),
                profile.getDoB(),
                profile.getAddress(),
                user.getUsername(),
                user.getEmail()
        );

        if (profile.getProfileImage()!= null) {
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                   .pathSegment(profile.getProfileImage().getFileName())
                    .toUriString();
            dto.setProfileImageUrl(imageUrl);
        }
        return dto;

    }

    public UserProfile convertDtoToUserProfile(UserProfileDto userProfileDto) {
        UserProfile userProfile = new UserProfile();

        if (userProfileDto.getName() != null) {
            userProfile.setName(userProfileDto.getName());
        }

        if (userProfileDto.getDoB() != null) {
            userProfile.setDoB(userProfileDto.getDoB());
        }

        if (userProfileDto.getAddress() != null) {
            userProfile.setAddress(userProfileDto.getAddress());
        }

        return userProfile;
    }

    @Transactional
    public void updateProfileImage(String username, String fileName) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = user.getUserProfile();
        if (profile == null) {
            profile = new UserProfile();
            user.setUserProfile(profile);
        }

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(fileName);
        uploadedFile.setFilePath("uploads/" + fileName);
        uploadedFile.setUserProfile(profile);

        uploadedFileRepository.save(uploadedFile);

        profile.setProfileImage(uploadedFile);

        userProfileRepository.save(profile);
    }

    @Transactional
    public void assignImageToUserProfile(Long userId, String fileName) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("UserProfile not found"));

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(fileName);
        uploadedFile.setFilePath("uploads/" + fileName);
        uploadedFile.setUserProfile(userProfile);

        uploadedFileRepository.save(uploadedFile);

        userProfile.setProfileImage(uploadedFile);

        userProfileRepository.save(userProfile);
    }
}