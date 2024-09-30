package novi.backend.eindopdrachtmoesproducebackend.dtos;

import java.time.LocalDate;

public class UserProfileDto {
    private String name;
    private LocalDate doB;
    private String address;
    private String username;
    private String email;

    public UserProfileDto() {}

    public UserProfileDto(String name, LocalDate doB, String address, String username, String email) {
        this.name = name;
        this.doB = doB;
        this.address = address;
        this.username = username;
        this.email = email;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "UserProfileDto{" +
                "name='" + name + '\'' +
                ", doB=" + doB +
                ", address='" + address + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDoB() {
        return doB;
    }

    public void setDoB(LocalDate doB) {
        this.doB = doB;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
