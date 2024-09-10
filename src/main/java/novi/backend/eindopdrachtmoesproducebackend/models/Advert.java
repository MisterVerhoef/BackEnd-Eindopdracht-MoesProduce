package novi.backend.eindopdrachtmoesproducebackend.models;

import jakarta.persistence.*;

import java.time.LocalDate;

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

    public Advert() {
        this.createdDate = LocalDate.now();
    }



    public Advert(String title, String description, UserProfile userProfile) {
        this.title = title;
        this.description = description;
        this.userProfile = userProfile;
        this.createdDate = LocalDate.now();  // Datum wordt automatisch ingesteld
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
}
