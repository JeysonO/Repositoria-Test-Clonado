package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class UsuarioCreateResponse {


    private String id;
    private String usuario;
    private String nombre;
    private String apePaterno;
    private String apeMaterno;
    private String email;

    //@Mapping("empresa.razonSocial")

}
