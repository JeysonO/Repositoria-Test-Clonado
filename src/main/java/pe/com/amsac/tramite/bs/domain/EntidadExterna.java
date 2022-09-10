package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class EntidadExterna {

    private String razonSocial;
    private String cargo;
    private String nombre;
    private String telefono;
    private String anexo;
    private String celular;
    private String email;
    private String direccion;
}
