package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class UsuarioFirmaLogoCreateResponse {


    private String id;
    private String nombreArchivo;
    private String size;
    private String estado;
    private String descripcion;
    private boolean esFavorito;


}
