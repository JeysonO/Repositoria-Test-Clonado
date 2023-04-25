package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class CargoDTOResponse {

    private String id;
    private String cargo;
    private String nombre;
    private String estado;
    private DependenciaDTOResponse dependencia;


}
