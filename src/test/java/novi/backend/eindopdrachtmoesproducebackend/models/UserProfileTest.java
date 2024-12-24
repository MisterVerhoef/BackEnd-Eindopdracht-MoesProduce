package novi.backend.eindopdrachtmoesproducebackend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserProfileTest {

    @Mock
    private User userMock;

    @Mock
    private UploadedFile uploadedFileMock;

    private UserProfile userProfile;

    @BeforeEach
    void setUp() {
        userProfile = new UserProfile();
    }

    @Test
    void testNoArgsConstructor() {
        assertNull(userProfile.getId());
        assertNull(userProfile.getName());
        assertNull(userProfile.getDoB());
        assertNull(userProfile.getAddress());
        assertNull(userProfile.getProfileImage());
        assertNull(userProfile.getUser());
        assertNull(userProfile.getAdverts());
        assertNotNull(userProfile.getSavedAdverts());
        assertTrue(userProfile.getSavedAdverts().isEmpty());
    }

    @Test
    void testConstructorWithNameDobUserAddress() {
        LocalDate dob = LocalDate.of(1995, 5, 15);
        User user = new User();
        UserProfile userProfile3 = new UserProfile("Jane", dob, user, "AnotherAddress");

        assertEquals("Jane", userProfile3.getName());
        assertEquals(dob, userProfile3.getDoB());
        assertEquals("AnotherAddress", userProfile3.getAddress());
        assertEquals(user, userProfile3.getUser());
        assertNotNull(userProfile3.getSavedAdverts());
        assertTrue(userProfile3.getSavedAdverts().isEmpty());
    }


    @Test
    void testSettersAndGetters() {
        // ARRANGE
        long expectedId = 100L;
        String expectedName = "Alice";
        LocalDate expectedDateOfBirth = LocalDate.of(2000, 1, 1);
        String expectedAddress = "TestAddress";
        List<Advert> advertsList = new ArrayList<>();
        advertsList.add(new Advert());
        advertsList.add(new Advert());
        List<Advert> savedAdverts = new ArrayList<>();
        savedAdverts.add(new Advert());

        // ACT
        userProfile.setId(expectedId);
        userProfile.setName(expectedName);
        userProfile.setDoB(expectedDateOfBirth);
        userProfile.setAddress(expectedAddress);
        userProfile.setUser(userMock);
        userProfile.setProfileImage(uploadedFileMock);
        userProfile.setAdverts(advertsList);
        userProfile.setSavedAdverts(savedAdverts);

        // ASSERT
        assertEquals(expectedId, userProfile.getId());
        assertEquals(expectedName, userProfile.getName());
        assertEquals(expectedDateOfBirth, userProfile.getDoB());
        assertEquals(expectedAddress, userProfile.getAddress());
        assertEquals(userMock, userProfile.getUser());
        assertEquals(uploadedFileMock, userProfile.getProfileImage());
        assertEquals(2, userProfile.getAdverts().size());
        assertEquals(1, userProfile.getSavedAdverts().size());
    }

    @Test
    void testGetUsernameCallsUserMock() {
        // ARRANGE
        when(userMock.getUsername()).thenReturn("MockUserName");
        userProfile.setUser(userMock);

        // ACT
        String username = userProfile.getUsername();

        // ASSERT
        assertEquals("MockUserName", username);
        verify(userMock).getUsername();
    }


    @Test
    void testSavedAdvertsManagement() {
        Advert advert = new Advert();
        userProfile.getSavedAdverts().add(advert);

        assertFalse(userProfile.getSavedAdverts().isEmpty());
        assertEquals(1, userProfile.getSavedAdverts().size());
        assertTrue(userProfile.getSavedAdverts().contains(advert));
    }

    @Test
    void testEqualsAndHashCode() {
        UserProfile profile1 = new UserProfile();
        UserProfile profile2 = new UserProfile();


        assertEquals(profile1, profile1);


        profile1.setId(1L);
        profile2.setId(1L);
        assertEquals(profile1.getId(), profile2.getId());


        profile2.setId(2L);
        assertNotEquals(profile1, profile2);


        assertNotEquals(null, profile1);


        assertNotEquals(new Object(), profile1);
    }

    }
