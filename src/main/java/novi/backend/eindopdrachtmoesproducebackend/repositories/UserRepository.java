package novi.backend.eindopdrachtmoesproducebackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import novi.backend.eindopdrachtmoesproducebackend.models.User;

public interface UserRepository extends JpaRepository<User, String> {
}
