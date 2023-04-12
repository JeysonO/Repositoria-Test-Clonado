package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class AuthenticationKeycloakResponse {
    private String access_token;
    private String refresh_token;
    private String expires_in;
    private String refresh_expires_in;
    private String token_type;
}
