package novi.backend.eindopdrachtmoesproducebackend.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "adverts")
public class Advert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private int viewCount = 0;

    @Column(nullable = false)
    private int saveCount = 0;

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
        this.createdDate = LocalDate.now();
        this.vegetables = vegetables;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {

        this.viewCount = viewCount;
    }

    public int getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(int saveCount) {
        this.saveCount = saveCount;
    }

    public void incrementSaveCount() {
        saveCount++;
    }

    public void decrementSaveCount() {
        if (saveCount > 0) {
            saveCount--;
        }
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

        this.createdDate = LocalDate.now();
    }

    public List<UploadedFile> getPhotos() {
        return photos;
    }


    public void setPhotos(List<UploadedFile> photos) {
        this.photos = photos;
    }
}
