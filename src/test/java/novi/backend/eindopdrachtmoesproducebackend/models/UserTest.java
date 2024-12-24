package novi.backend.eindopdrachtmoesproducebackend.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Mock
    private UserProfile userProfileMock;

    @InjectMocks
    private User user;

    @BeforeEach
    void setup() {
        user.setEmail("test@example.com");
        user.setUsername("testUser");
        user.setPassword("p@ssw0rd");
        user.setTermsAccepted(true);
        user.setUserProfile(userProfileMock);
    }

    @Test
    void testNoArgsConstructor() {
        User u = new User();
        assertNotNull(u);
        assertNull(u.getId());
        assertNull(u.getEmail());
        assertNull(u.getPassword());
        assertNull(u.getUsername());
        assertTrue(u.getRoles().isEmpty());
        assertFalse(u.isTermsAccepted());
        assertNull(u.getUserProfile());
    }

    @Test
    void testSettersAndGetters() {
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testUser", user.getUsername());
        assertEquals("p@ssw0rd", user.getPassword());
        assertTrue(user.isTermsAccepted());
    }

    @Test
    void addRoleSuccessfully() {
        user.addRole(User.Role.ADMIN);
        assertTrue(user.getRoles().contains(User.Role.ADMIN));
    }

    @Test
    void removeRoleSuccessfully() {
        user.addRole(User.Role.SELLER);
        user.removeRole(User.Role.SELLER);
        assertFalse(user.getRoles().contains(User.Role.SELLER));
    }

    @Test
    void addRoleToImmutableRolesSetThrowsException() {
        Set<User.Role> immutableRoles = Set.of(User.Role.USER);
        user.setRoles(immutableRoles);
        assertThrows(UnsupportedOperationException.class, () -> user.addRole(User.Role.ADMIN));
    }

    @Test
    void removeRoleFromImmutableRolesSetThrowsException() {
        Set<User.Role> immutableRoles = Set.of(User.Role.USER);
        user.setRoles(immutableRoles);
        assertThrows(UnsupportedOperationException.class, () -> user.removeRole(User.Role.USER));
    }

    @Test
    void testUserProfileRelationship() {
        when(userProfileMock.getId()).thenReturn(42L);
        when(userProfileMock.getUsername()).thenReturn("testProfile");

        assertEquals(42L, user.getUserProfile().getId());
        assertEquals("testProfile", user.getUserProfile().getUsername());
    }

    @Test
    void testTermsAcceptance() {
        user.setTermsAccepted(false);
        assertFalse(user.isTermsAccepted());

        user.setTermsAccepted(true);
        assertTrue(user.isTermsAccepted());
    }

    @Test
    void testEmailValidation() {
        user.setEmail("invalid");
        assertEquals("invalid", user.getEmail());

        user.setEmail("valid@email.com");
        assertEquals("valid@email.com", user.getEmail());
    }
}