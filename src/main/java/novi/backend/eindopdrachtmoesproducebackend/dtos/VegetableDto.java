package novi.backend.eindopdrachtmoesproducebackend.dtos;

public class VegetableDto {

    private String name;
    private String category;


    public VegetableDto(String name, String category) {
        this.name = name;
        this.category = category;
    }

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
