package novi.backend.eindopdrachtmoesproducebackend.dtos;

import java.time.LocalDate;

public class UserProfileDto {

    private String name;
    private LocalDate doB;
    private String address;

    public UserProfileDto() {}

    public UserProfileDto(String name, LocalDate doB, String address) {
        this.name = name;
        this.doB = doB;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDoB() {
        return doB;
    }

    public void setDoB(LocalDate doB) {
        this.doB = doB;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }
}
