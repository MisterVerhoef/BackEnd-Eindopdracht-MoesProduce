package novi.backend.eindopdrachtmoesproducebackend.models;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private String email;

    @OneToOne
    @MapsId
    @JoinColumn(name = "email")
    private User user;

    private String firstName;
    private String lastName;
    private LocalDate DoB;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDoB() {
        return DoB;
    }

    public void setDoB(LocalDate doB) {
        DoB = doB;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
