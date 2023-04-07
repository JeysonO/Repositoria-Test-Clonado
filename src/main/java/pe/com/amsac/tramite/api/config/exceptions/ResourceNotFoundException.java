package pe.com.amsac.tramite.api.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ResourceNotFoundException extends Exception {
    
	private static final long serialVersionUID = -8438865855332759951L;
	
	public ResourceNotFoundException( String message) {
        super(message);
    }
    
}
