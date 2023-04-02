package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import pe.com.amsac.tramite.bs.domain.Usuario;

import javax.persistence.Id;

@Data
public class UsuarioFirmaCreateResponse {


    private String id;
    private UsuarioResponse usuario;
    private String usernameServicioFirma;
    private String passwordServicioFirma;
    private String siglaFirma;
    private String estado;


}
