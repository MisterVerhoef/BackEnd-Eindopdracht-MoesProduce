package novi.backend.eindopdrachtmoesproducebackend.service;

import novi.backend.eindopdrachtmoesproducebackend.dtos.UploadedFileResponseDto;
import novi.backend.eindopdrachtmoesproducebackend.models.Advert;
import novi.backend.eindopdrachtmoesproducebackend.models.UploadedFile;
import novi.backend.eindopdrachtmoesproducebackend.models.UserProfile;
import novi.backend.eindopdrachtmoesproducebackend.repositories.UploadedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class UploadedFileService {

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
    private final List<String> allowedFileTypes = List.of("image/jpeg", "image/png", "image/gif");
    private final long maxFileSize = 5 * 1024 * 1024;  // 5 MB



    public UploadedFileService(UploadedFileRepository uploadedFileRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    public List<UploadedFile> findByUserProfileId(Long userProfileId) {
        return uploadedFileRepository.findByUserProfile_Id(userProfileId);
    }

    public UploadedFile storeFile(MultipartFile file) {
        return storeFile(file, null, null);
    }

    public UploadedFile storeFile(MultipartFile file, Advert advert) {
        return storeFile(file, advert, null);
    }

    public UploadedFile storeFile(MultipartFile file, UserProfile userProfile) {
        return storeFile(file, null, userProfile);
    }

    public UploadedFile storeFile(MultipartFile file, Advert advert, UserProfile userProfile) {
        validateFile(file);
        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UploadedFile uploadedFile = new UploadedFile();
            uploadedFile.setFileName(fileName);
            uploadedFile.setFilePath(targetLocation.toString());
            uploadedFile.setFileType(file.getContentType());
            uploadedFile.setFileSize(file.getSize());
            uploadedFile.setAdvert(advert);
            uploadedFile.setUserProfile(userProfile);

            uploadedFileRepository.save(uploadedFile);

            return uploadedFileRepository.save(uploadedFile);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!", ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (!allowedFileTypes.contains(file.getContentType())) {
            throw new RuntimeException("Invalid file type. Only JPEG, PNG, and GIF files are allowed." + allowedFileTypes);
        }

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds the maximum allowed size of 5 MB.");
        }
    }

    public void deleteFile(Long fileId) {
        UploadedFile uploadedFile = uploadedFileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        try {
            Path filePath = Paths.get(uploadedFile.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file. Please try again!", e);
        }
        uploadedFileRepository.delete(uploadedFile);
    }

    private String generateUniqueFileName(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + fileExtension;
    }
}