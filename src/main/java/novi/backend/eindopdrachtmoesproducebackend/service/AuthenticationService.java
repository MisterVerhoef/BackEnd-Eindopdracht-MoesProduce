package novi.backend.eindopdrachtmoesproducebackend.service;


import novi.backend.eindopdrachtmoesproducebackend.dtos.LoginResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UserRepository;
import novi.backend.eindopdrachtmoesproducebackend.security.CustomUserDetails;
import novi.backend.eindopdrachtmoesproducebackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager,
                                 UserDetailsService userDetailsService,
                                 JwtUtil jwtUtil,
                                 UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public LoginResponseDto login(String usernameOrEmail, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usernameOrEmail, password));

        var user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("Invalid username/email or password"));

        var userDetails = new CustomUserDetails(user);

        String token = jwtUtil.generateToken(userDetails);

        return new LoginResponseDto(token);
    }


}
