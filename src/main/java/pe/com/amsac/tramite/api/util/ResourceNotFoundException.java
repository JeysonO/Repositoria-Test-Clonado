package pe.com.amsac.tramite.api.util;

public class ResourceNotFoundException extends Exception{
    private static final long serialVersionUID = -8438865855332759951L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
