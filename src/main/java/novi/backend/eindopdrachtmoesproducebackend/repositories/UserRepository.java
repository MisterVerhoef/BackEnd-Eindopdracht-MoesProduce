package novi.backend.eindopdrachtmoesproducebackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import novi.backend.eindopdrachtmoesproducebackend.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

}
