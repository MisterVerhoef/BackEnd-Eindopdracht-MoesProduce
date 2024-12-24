package novi.backend.eindopdrachtmoesproducebackend.models;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VegetableTest {

    @Test
    void testNoArgsConstructor() {
        // ARRANGE
       // Geen setup nodig

        // ACT
        Vegetable veg = new Vegetable();

        // ASSERT
        // Standaardwaarden checken (ofwel null)
        assertNotNull(veg);
        assertNull(veg.getId());
        assertNull(veg.getName());
        assertNull(veg.getCategory());
    }

    @Test
    void testAllArgsConstructor() {
        // ARRANGE
        String expectedName = "Tomaat";
        String expectedCategory = "Fruit";

        // ACT
        Vegetable veg = new Vegetable(expectedName, expectedCategory);

        // ASSERT
        // Id is niet gezet, dus blijft null
        assertNull(veg.getId());
        assertEquals(expectedName, veg.getName());
        assertEquals(expectedCategory, veg.getCategory());
    }

    @Test
    void testGetAndSetId() {
        // ARRANGE
        Vegetable veg = new Vegetable();
        Long expectedId = 123L;

        // ACT
        veg.setId(expectedId);

        // ASSERT
        assertEquals(expectedId, veg.getId());
    }

    @Test
    void testGetAndSetName() {
        // ARRANGE
        Vegetable veg = new Vegetable();
        String expectedName = "Paprika";

        // ACT
        veg.setName(expectedName);

        // ASSERT
        assertEquals(expectedName, veg.getName());
    }

    @Test
    void testGetAndSetCategory() {
        // ARRANGE
        Vegetable veg = new Vegetable();
        String expectedCategory = "Groente";

        // ACT
        veg.setCategory(expectedCategory);

        // ASSERT
        assertEquals(expectedCategory, veg.getCategory());
    }

    @Test
    void testToString() {
        // ARRANGE
        Vegetable veg = new Vegetable("Wortel", "Groente");
        veg.setId(10L);

        // ACT
        String actualString = veg.toString();

        // ASSERT
        String expected = "Vegetable{id=10, name='Wortel', category='Groente'}";
        assertEquals(expected, actualString);
    }
}