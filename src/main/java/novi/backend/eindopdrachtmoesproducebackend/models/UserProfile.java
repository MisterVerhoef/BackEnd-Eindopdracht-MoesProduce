package novi.backend.eindopdrachtmoesproducebackend.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate doB;

    private String address;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_image_id", referencedColumnName = "id")
    private UploadedFile profileImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Advert> adverts;

    @ManyToMany
    @JoinTable(
            name = "saved_adverts",
            joinColumns = @JoinColumn(name = "user_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "advert_id")
    )
    private List<Advert> savedAdverts = new ArrayList<>();

    public UserProfile() {
    }

    public UserProfile(String name, LocalDate doB, String address) {
        this.name = name;
        this.doB = doB;
        this.address = address;
    }

    public UserProfile(String name, LocalDate doB, User user, String address) {
        this.name = name;
        this.doB = doB;
        this.user = user;
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Advert> getAdverts() {
        return adverts;
    }

    public void setAdverts(List<Advert> adverts) {
        this.adverts = adverts;
    }

    public List<Advert> getSavedAdverts() {
        return savedAdverts;
    }

    public void setSavedAdverts(List<Advert> savedAdverts) {
        this.savedAdverts = savedAdverts;
    }

    public String getUsername() {
        return user.getUsername();
    }

    public UploadedFile getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(UploadedFile profileImage) {
        this.profileImage = profileImage;
    }
}
