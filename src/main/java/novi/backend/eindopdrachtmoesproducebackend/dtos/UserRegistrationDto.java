package novi.backend.eindopdrachtmoesproducebackend.dtos;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto extends UserDto {
    @NotBlank(message = "Een wachtwoord is vereist")
    @Size(min = 6, max = 100, message = "Wachtwoord moet tussen 6 en 100 characters zijn")
    private String password;


    public UserRegistrationDto() {}

    public UserRegistrationDto(String email, String username, String password) {
        super(email, username);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
