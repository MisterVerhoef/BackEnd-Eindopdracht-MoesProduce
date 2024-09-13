package novi.backend.eindopdrachtmoesproducebackend.dtos;

import java.time.LocalDate;

public class AdvertDto {

    private Long id;
    private String title;
    private String description;
    private LocalDate createdDate;
    private String username;

    public AdvertDto(){

    }

    public AdvertDto(Long id, String title, String description, LocalDate createdDate, String username) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.username = username;
    }
        public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
