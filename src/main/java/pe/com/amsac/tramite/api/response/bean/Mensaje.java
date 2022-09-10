package pe.com.amsac.tramite.api.response.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Mensaje {

    private String codigo;
    private String tipo;
    private String mensaje;

}
