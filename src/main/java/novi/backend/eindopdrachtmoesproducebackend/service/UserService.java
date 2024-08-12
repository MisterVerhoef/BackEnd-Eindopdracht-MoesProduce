package novi.backend.eindopdrachtmoesproducebackend.service;


import novi.backend.eindopdrachtmoesproducebackend.dtos.UserDto;
import novi.backend.eindopdrachtmoesproducebackend.exceptions.RecordNotFoundException;
import novi.backend.eindopdrachtmoesproducebackend.models.User;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getUsers() {
        List <UserDto> collection = new ArrayList<>();
        List <User> list = userRepository.findAll();
        for (User user : list) {
            collection.add(fromUser(user));
        }
        return collection;
    }

    public UserDto getUser(String email){
        User user = userRepository.findById(email)
                .orElseThrow(() -> new RecordNotFoundException("User not found with email : " + email));
        return fromUser(user);
    }

    public String createUser(UserDto userDto) {
        User user = toUser(userDto);
        User savedUser = userRepository.save(user);
        return savedUser.getEmail();
    }

    public void deleteUser(String email) {
        userRepository.deleteById(email);
    }

    public void updateUser(String email, UserDto newUser) {
        if (!userRepository.existsById(email)) throw new RecordNotFoundException();
        User user = userRepository.findById(email).get();
        updateUserFromDto(user, newUser);
        userRepository.save(user);
    }
public static UserDto fromUser(User user) {
    UserDto dto = new UserDto();
    dto.setEmail(user.getEmail());
    dto.setUsername(user.getUsername());
    dto.setPassword(user.getPassword());
    return dto;
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        return user;
    }

    private void updateUserFromDto(User user, UserDto userDto) {
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getPassword() != null) {
            user.setPassword(userDto.getPassword());
        }
    }

    }

