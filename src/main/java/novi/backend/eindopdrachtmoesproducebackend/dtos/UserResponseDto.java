package novi.backend.eindopdrachtmoesproducebackend.dtos;

import novi.backend.eindopdrachtmoesproducebackend.models.User;

import java.util.Set;

public class UserResponseDto extends UserDto {
    private Long id;
    private Set<User.Role> roles;


    public UserResponseDto() {}

    public UserResponseDto(Long id, String email, String username, Set<User.Role> roles) {
        super(email, username);
        this.id = id;
        this.roles = roles;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<User.Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<User.Role> roles) {
        this.roles = roles;
    }
}
