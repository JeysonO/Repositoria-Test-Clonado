package pe.com.amsac.tramite.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.keycloak")
@Data
public class KeycloakProperties {

    private String grantType;
    private String url;

}
