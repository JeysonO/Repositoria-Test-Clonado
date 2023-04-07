package pe.com.amsac.tramite.api.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AppException extends RuntimeException {

    private static final long serialVersionUID = 1373967952838547884L;

    public AppException(String message) {
        super(message);
    }

}
