package novi.backend.eindopdrachtmoesproducebackend.repositories;

import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
}
