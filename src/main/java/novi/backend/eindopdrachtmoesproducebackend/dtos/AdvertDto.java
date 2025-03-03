package novi.backend.eindopdrachtmoesproducebackend.dtos;

import novi.backend.eindopdrachtmoesproducebackend.models.Vegetable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdvertDto {

    private Long id;
    private String title;
    private String description;
    private LocalDate createdDate;
    private String username; 
    private List<VegetableDto> vegetables;
    private List<String> imageUrls;
    private int viewCount;
    private int saveCount;


    private String formattedCreatedDate;




    public AdvertDto(){

    }

    public AdvertDto(Long id, String title, String description, LocalDate createdDate, String username, List<VegetableDto> vegetables, int viewCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdDate = createdDate;
        this.username = username;
        this.vegetables = vegetables;
        this.viewCount = viewCount;

        this.setFormattedCreatedDate();

    }

    public void setFormattedCreatedDate() {
        if (this.createdDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            this.formattedCreatedDate = this.createdDate.format(formatter);
        }
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<VegetableDto> getVegetables() {
        return vegetables;
    }

    public void setVegetables(List<VegetableDto> vegetables) {
        this.vegetables = vegetables;
    }
        public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
        this.setFormattedCreatedDate();
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


    public String getFormattedCreatedDate() {
        return formattedCreatedDate;

}





}

