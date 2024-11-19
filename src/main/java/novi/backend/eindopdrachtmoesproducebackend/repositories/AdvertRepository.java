package novi.backend.eindopdrachtmoesproducebackend.repositories;

import novi.backend.eindopdrachtmoesproducebackend.models.Advert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdvertRepository extends JpaRepository<Advert, Long>{


    @Modifying
    @Query("UPDATE Advert a SET a.viewCount = a.viewCount + 1 WHERE a.id = :advertId")
void incrementViewCount(@Param("advertId") Long id);

    @Query("SELECT a FROM Advert a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Advert> searchByTitle(@Param("query") String query);

    List<Advert> findByUserProfile_User_Username(String username);
}
