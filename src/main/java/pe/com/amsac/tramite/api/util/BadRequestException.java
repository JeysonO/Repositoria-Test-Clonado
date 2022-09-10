package pe.com.amsac.tramite.api.util;

public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = 6703109416416894018L;

    public BadRequestException(String message) {
        super(message);
    }

}
