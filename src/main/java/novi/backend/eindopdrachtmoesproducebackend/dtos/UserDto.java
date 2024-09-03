package novi.backend.eindopdrachtmoesproducebackend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {

    @NotBlank(message = "Een emailadres is vereist")
    @Email(message = "Ongeldig emailadres")
    private String email;

    @Size(min = 3, max = 50, message = "Gebruikersnaam moet tussen 3 en 50 karakters lang zijn")
    @NotBlank(message = "Een gebruikersnaam is vereist")
    private String username;


    public UserDto() {}

    public UserDto(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
