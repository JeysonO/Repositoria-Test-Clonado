package pe.com.amsac.tramite.api.response.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioUniqueResponse {

    private String id;
    private String usuario;
    private String nombre;
    private String apePaterno;
    private String apeMaterno;
    private String email;
    private String tipoUsuario;

}
