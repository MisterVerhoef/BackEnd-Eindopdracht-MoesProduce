package novi.backend.eindopdrachtmoesproducebackend.dtos;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDto extends UserDto {
    @NotBlank(message = "Een wachtwoord is vereist")
    @Size(min = 6, max = 100, message = "Wachtwoord moet tussen 6 en 100 characters zijn")
    private String password;

    @NotNull(message = "De algemene voorwaarden moeten aanvaard worden om te kunnen registreren")
    private Boolean termsAccepted;



    public UserRegistrationDto() {}

    public UserRegistrationDto(String email, String username, String password, Boolean termsAccepted) {
        super(email, username);
        this.password = password;
        this.termsAccepted = termsAccepted;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(Boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }
}
