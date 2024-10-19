package novi.backend.eindopdrachtmoesproducebackend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "file_uploads")
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String filePath;
    private String fileType;
    private long fileSize;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable= true)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "user_profile_id", nullable= true)
    private UserProfile userProfile;

    public UploadedFile(Advert advert, String fileName, String filePath, long fileSize, String fileType, Long id) {
        this.advert = advert;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.id = id;
    }

    public UploadedFile() {

    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
