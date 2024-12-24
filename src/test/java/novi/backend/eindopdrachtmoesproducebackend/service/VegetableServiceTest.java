package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.models.Vegetable;
import novi.backend.eindopdrachtmoesproducebackend.repositories.VegetableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VegetableServiceTest {

    @Mock
    private VegetableRepository vegetableRepositoryMock;

    @InjectMocks
    private VegetableService vegetableService;

    private Vegetable testVegetable;


    @BeforeEach
    void setUp() {
        testVegetable = new Vegetable("Prei", "Groente");
        testVegetable.setId(1L);
    }

    @Test
    void getAllVegetables_ShouldReturnListOfVegetables() {
        // Arrange
        List<Vegetable> vegList = new ArrayList<>();
        vegList.add(testVegetable);
        vegList.add(new Vegetable("Broccoli", "Groente"));

        when(vegetableRepositoryMock.findAll()).thenReturn(vegList);

        // Act
        List<Vegetable> result = vegetableService.getAllVegetables();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Prei", result.get(0).getName());
        assertEquals("Broccoli", result.get(1).getName());

        verify(vegetableRepositoryMock, times(1)).findAll();


    }

    @Test
    void saveVegetable_ShouldReturnSavedVegetable() {

        // Arrange
        when(vegetableRepositoryMock.save(any(Vegetable.class))).thenAnswer(invocation ->{

            Vegetable savedVegetable = invocation.getArgument(0);
            savedVegetable.setId(99L);
            return  savedVegetable;
        });

        // Act
        Vegetable result = vegetableService.saveVegetable(testVegetable);

        // Assert
        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("Prei", result.getName());
        assertEquals("Groente", result.getCategory());

        verify(vegetableRepositoryMock, times(1)).save(testVegetable);
        }

    }
