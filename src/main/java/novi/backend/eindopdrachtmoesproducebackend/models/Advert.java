package novi.backend.eindopdrachtmoesproducebackend.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adverts")  // Naam van de database tabel)
public class Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Gebruikt een auto-incremente ID (Primary Key)
    private Long id;

    private String title;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    private LocalDate createdDate;

    @ManyToMany
    @JoinTable(
            name = "advert_vegetables",
            joinColumns = @JoinColumn(name = "advert_id"),
            inverseJoinColumns = @JoinColumn(name = "vegetable_id")
    )
    private List<Vegetable> vegetables;

    public Advert() {
        this.createdDate = LocalDate.now();
    }

    @OneToMany(mappedBy = "advert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadedFile> photos = new ArrayList<>();




    public Advert(String title, String description, UserProfile userProfile, List<Vegetable> vegetables) {
        this.title = title;
        this.description = description;
        this.userProfile = userProfile;
        this.createdDate = LocalDate.now();// Datum wordt automatisch ingesteld
        this.vegetables = vegetables; // Groenten toevoegen aan advert
    }


    public List<Vegetable> getVegetables() {
        return vegetables;
    }

    public void setVegetables(List<Vegetable> vegetables) {
        this.vegetables = vegetables;
    }
    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {


        this.title = title;
    }

    public LocalDate getCreatedDate() {

        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {

        this.createdDate = createdDate;
    }

    public UserProfile getUserProfile() {

        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {

        this.userProfile = userProfile;
    }

    @PrePersist
    public void prePersist() {

        this.createdDate = LocalDate.now(); // Stelt de datum in op het moment van plaatsing
    }

    public List<UploadedFile> getPhotos() {
        return photos;
    }


    public void setPhotos(List<UploadedFile> photos) {
        this.photos = photos;
    }
}
