package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class UsuarioCargoResponse {

    private String id;
    private String estado;
    private CargoResponse cargo;
    private UsuarioResponse usuario;


}
