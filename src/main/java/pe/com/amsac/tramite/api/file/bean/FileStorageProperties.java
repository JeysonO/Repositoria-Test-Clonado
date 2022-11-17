package pe.com.amsac.tramite.api.file.bean;

public class FileStorageProperties {
    protected String baseUploadDir;

    public FileStorageProperties() {
    }

    public String getBaseUploadDir() {
        return this.baseUploadDir;
    }

    public void setBaseUploadDir(String baseUploadDir) {
        this.baseUploadDir = baseUploadDir;
    }
}
