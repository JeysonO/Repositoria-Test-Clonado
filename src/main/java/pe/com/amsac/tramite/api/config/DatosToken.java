package pe.com.amsac.tramite.api.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosToken {
    @JsonProperty("sid")
    private String id;
    @JsonProperty("name")
    private String nameCompleto;
    @JsonProperty("preferred_username")
    private String userName;
    @JsonProperty("given_name")
    private String nombre;
    @JsonProperty("family_name")
    private String apellido;
    @JsonProperty("email")
    private String email;

}
