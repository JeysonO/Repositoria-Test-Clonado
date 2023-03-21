package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class UsuarioDTOResponse {

    private String id;
    private String usuario;
    private String nombre;
    private String apePaterno;
    private String apeMaterno;
    private String email;
    private String dependenciaNombre;
    private String dependenciaId;
    private String cargoNombre;
    private String cargoId;
    private String dependenciaSigla;
    private String tipoUsuario;

}
