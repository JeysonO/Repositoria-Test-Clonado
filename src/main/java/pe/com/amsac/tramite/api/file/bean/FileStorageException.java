package pe.com.amsac.tramite.api.file.bean;

public class FileStorageException extends RuntimeException {
    private static final long serialVersionUID = 6703109416416894018L;

    public FileStorageException(String message) {
        super(message);
    }
}
