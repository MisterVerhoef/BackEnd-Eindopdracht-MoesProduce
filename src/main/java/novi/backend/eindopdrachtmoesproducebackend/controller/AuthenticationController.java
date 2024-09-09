package novi.backend.eindopdrachtmoesproducebackend.controller;


import novi.backend.eindopdrachtmoesproducebackend.dtos.LoginRequestDto;
import novi.backend.eindopdrachtmoesproducebackend.dtos.LoginResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto response = authenticationService.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }
}
