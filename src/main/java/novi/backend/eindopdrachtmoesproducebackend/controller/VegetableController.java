package novi.backend.eindopdrachtmoesproducebackend.controller;

import jakarta.validation.Valid;
import novi.backend.eindopdrachtmoesproducebackend.dtos.VegetableDto;
import novi.backend.eindopdrachtmoesproducebackend.models.Vegetable;
import novi.backend.eindopdrachtmoesproducebackend.service.VegetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vegetables")
public class VegetableController {

    @Autowired
    private VegetableService vegetableService;

    @GetMapping
    public List<VegetableDto> getAllVegetables() {
        return vegetableService.getAllVegetables()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public VegetableDto addVegetable(@Valid @RequestBody VegetableDto vegetableDto) {
        Vegetable vegetable = convertToEntity(vegetableDto);
        Vegetable savedVegetable = vegetableService.saveVegetable(vegetable);
        return convertToDto(savedVegetable);
    }

    // DTO conversion methods
    private VegetableDto convertToDto(Vegetable vegetable) {
        return new VegetableDto(vegetable.getName(), vegetable.getCategory());
    }

    private Vegetable convertToEntity(VegetableDto vegetableDto) {
        Vegetable vegetable = new Vegetable();
        vegetable.setName(vegetableDto.getName());
        vegetable.setCategory(vegetableDto.getCategory());
        return vegetable;
    }

}
