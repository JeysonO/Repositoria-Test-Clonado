package pe.com.amsac.tramite.api.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class FileStorageException extends RuntimeException {

	private static final long serialVersionUID = 6703109416416894018L;

	public FileStorageException(String message) {
        super(message);
    }
    
}
