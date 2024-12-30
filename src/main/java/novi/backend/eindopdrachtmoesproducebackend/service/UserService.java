package novi.backend.eindopdrachtmoesproducebackend.service;


import jakarta.transaction.Transactional;
import novi.backend.eindopdrachtmoesproducebackend.dtos.UserResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import novi.backend.eindopdrachtmoesproducebackend.security.CustomUserDetails;
import novi.backend.eindopdrachtmoesproducebackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public User registerUser(String email, String username, String password, boolean termsAccepted) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("Email is al in gebruik");
        }
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new RuntimeException("Gebruikersnaam is al in gebruik");
        }
        if (!termsAccepted) {
            throw new RuntimeException("Voorwaarden moeten worden geaccepteerd");
        }

        User user = new User(email, passwordEncoder.encode(password), username);
        user.addRole(User.Role.USER);
        user.setTermsAccepted(termsAccepted);

        UserProfile userProfile = new UserProfile();
        userProfile.setName(username);
        userProfile.setUser(user);
        user.setUserProfile(userProfile);

        return userRepository.save(user);
    }


    public String loginUser(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || password == null) {
            throw new BadCredentialsException("Username/email and password are required");
        }

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid username/email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username/email or password");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        return jwtUtil.generateToken(userDetails);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateUser(Long userId, String email, String username) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEmail().equals(email) && userRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("Email already in use");
        }
        if (!user.getUsername().equals(username) && userRepository.existsByUsernameIgnoreCase(username)) {
            throw new RuntimeException("Username already in use");
        }

        user.setEmail(email);
        user.setUsername(username);
        return userRepository.save(user);
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(user);
        System.out.println("User deleted successfully: " + user.getUsername());
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void changeUserRole(Long userId, User.Role newRole) {
        User user = getUserById(userId);

        // Remove all existing roles
        user.getRoles().clear();

        // Add the new role
        user.addRole(newRole);

        userRepository.save(user);
    }

    @Transactional
    public void promoteToSeller(Long userId) {
        User user = getUserById(userId);
        user.addRole(User.Role.SELLER);
        userRepository.save(user);
    }

    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private UserResponseDto mapToResponseDto(User user){
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getRoles(),
                user.getTermsAccepted()
        );
    }


}

