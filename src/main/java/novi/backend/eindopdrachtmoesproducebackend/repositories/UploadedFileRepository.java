package novi.backend.eindopdrachtmoesproducebackend.repositories;


import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    List<UploadedFile> findByUserProfile_Id(Long userProfileId);

    List<UploadedFile> findByAdvertId(Long advertId);

}
