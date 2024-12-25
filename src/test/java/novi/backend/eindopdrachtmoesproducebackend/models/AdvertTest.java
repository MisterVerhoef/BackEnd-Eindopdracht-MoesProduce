package novi.backend.eindopdrachtmoesproducebackend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvertTest {

    @Mock
    private UserProfile userProfileMock;

    @Mock
    private Vegetable vegetableMock;

    @InjectMocks
    private Advert advert;

 @BeforeEach
void setUp() {
 MockitoAnnotations.openMocks(this);
 List<Vegetable> vegetables = new ArrayList<>();
 vegetables.add(vegetableMock);
advert = new Advert("Test Advert", "This is a test advert", userProfileMock, vegetables);
 }

    @Test
    void testInitialValues() {

        assertEquals("Test Advert", advert.getTitle());
        assertEquals("This is a test advert", advert.getDescription());
        assertEquals(userProfileMock, advert.getUserProfile());
        assertEquals(1, advert.getVegetables().size());
        assertEquals(vegetableMock, advert.getVegetables().get(0));

        assertEquals(0, advert.getViewCount());
        assertEquals(0, advert.getSaveCount());

        assertNotNull(advert.getCreatedDate());

    }

    @Test
    void testConstructorInitialization() {
        assertEquals("Test Advert", advert.getTitle());
        assertEquals("This is a test advert", advert.getDescription());
        assertEquals(userProfileMock, advert.getUserProfile());
        assertEquals(LocalDate.now(), advert.getCreatedDate());
        assertNotNull(advert.getVegetables());
        assertEquals(1, advert.getVegetables().size());
        assertEquals(0, advert.getSaveCount());
        assertEquals(0, advert.getViewCount());
    }

    @Test
    void testSettersAndGetters() {
        advert.setTitle("NewTitle");
        advert.setDescription("NewDescription");
        advert.setViewCount(5);
        advert.setSaveCount(2);

        assertEquals("NewTitle", advert.getTitle());
        assertEquals("NewDescription", advert.getDescription());
        assertEquals(5, advert.getViewCount());
        assertEquals(2, advert.getSaveCount());
    }

    @Test
    void testIncrementAndDecrementSaveCount() {
        // Begin is 0
        assertEquals(0, advert.getSaveCount());

        advert.incrementSaveCount();
        advert.incrementSaveCount();
        assertEquals(2, advert.getSaveCount());

        advert.decrementSaveCount();
        assertEquals(1, advert.getSaveCount());

        // Niet onder nul
        advert.decrementSaveCount();
        assertEquals(0, advert.getSaveCount());
        advert.decrementSaveCount();
        assertEquals(0, advert.getSaveCount(), "SaveCount mag niet onder 0 komen");
    }

    @Test
    void testPrePersist() {
        advert.setCreatedDate(null);
        advert.prePersist();
        assertEquals(LocalDate.now(), advert.getCreatedDate());
    }
    @Test
    void setAndGetSaveCount() {
        Advert advert = new Advert();
        advert.setSaveCount(3);
        assertEquals(3, advert.getSaveCount());
    }

    @Test
    void setAndGetVegetables() {
        Advert advert = new Advert();
        List<Vegetable> vegetables = new ArrayList<>();
        advert.setVegetables(vegetables);
        assertEquals(vegetables, advert.getVegetables());
    }

    @Test
    void setAndGetPhotos() {
        Advert advert = new Advert();
        List<UploadedFile> photos = new ArrayList<>();
        advert.setPhotos(photos);
        assertEquals(photos, advert.getPhotos());
    }
@Test
void advertInitializationWithNullTitle() {
    Advert advert = new Advert(null, "Description", userProfileMock, new ArrayList<>());
    assertNull(advert.getTitle());
    assertEquals("Description", advert.getDescription());
}

@Test
void advertInitializationWithNullDescription() {
    Advert advert = new Advert("Title", null, userProfileMock, new ArrayList<>());
    assertEquals("Title", advert.getTitle());
    assertNull(advert.getDescription());
}

@Test
void advertInitializationWithNullUserProfile() {

    assertThrows(NullPointerException.class, () -> new Advert("Title", "Description", null, new ArrayList<>()));
}

@Test
void advertInitializationWithNullVegetables() {
    Advert advert = new Advert("Title", "Description", userProfileMock, null);
    assertEquals("Title", advert.getTitle());
    assertEquals("Description", advert.getDescription());
    assertEquals(userProfileMock, advert.getUserProfile());
    assertNull(advert.getVegetables());
}

@Test
void setNullTitle() {
    advert.setTitle(null);
    assertNull(advert.getTitle());
}

@Test
void setNullDescription() {
    advert.setDescription(null);
    assertNull(advert.getDescription());
}

@Test
void setNullVegetables() {
    advert.setVegetables(null);
    assertNull(advert.getVegetables());
}

@Test
void setNullPhotos() {
    advert.setPhotos(null);
    assertNull(advert.getPhotos());
}

@Test
void setNegativeViewCount() {
    advert.setViewCount(-1);
    assertEquals(-1, advert.getViewCount());
}

@Test
void setNegativeSaveCount() {
    advert.setSaveCount(-1);
    assertEquals(-1, advert.getSaveCount());
}
}