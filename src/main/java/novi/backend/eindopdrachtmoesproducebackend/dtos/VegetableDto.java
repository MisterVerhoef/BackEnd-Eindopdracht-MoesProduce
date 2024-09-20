package novi.backend.eindopdrachtmoesproducebackend.dtos;

public class VegetableDto {

    private String name;
    private String category;


    // Constructor
    public VegetableDto(String category, String name) {
        this.category = category;
        this.name = name;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
