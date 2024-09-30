package novi.backend.eindopdrachtmoesproducebackend.repositories;

import novi.backend.eindopdrachtmoesproducebackend.models.Vegetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VegetableRepository extends JpaRepository<Vegetable, Long> {
    Optional<Vegetable> findByName(String name);
}