package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class UsuarioFirmaCreateResponse {


    private String id;
    private UsuarioUniqueResponse usuario;
    private String usernameServicioFirma;
    private String passwordServicioFirma;
    private String siglaFirma;
    private String estado;


}
