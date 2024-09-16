package novi.backend.eindopdrachtmoesproducebackend.models;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String username;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile userProfile;

    @Column(nullable = false)
    private boolean termsAccepted;

    public enum Role {
        USER, SELLER, ADMIN
    }

    public User() {

    }

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public User(String email, String username, String password, Role initialRole) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.roles.add(initialRole);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {

        this.roles = roles;
    }

    public void addRole(Role role) {

        this.roles.add(role);
    }

    public void removeRole(Role role) {

        this.roles.remove(role);
    }

    public UserProfile getUserProfile() {

        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {

        this.userProfile = userProfile;
    }

    public boolean isTermsAccepted() {

        return termsAccepted;
    }
    public boolean getTermsAccepted() {

        return termsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {

        this.termsAccepted = termsAccepted;
    }

}
