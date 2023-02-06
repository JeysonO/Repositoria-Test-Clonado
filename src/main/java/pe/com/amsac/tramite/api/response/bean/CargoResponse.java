package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class CargoResponse {

    private String id;
    private String cargo;
    private String nombre;
    private String estado;
    private String dependenciaId;
    private DependenciaResponse dependencia;

    @Mapping("dependencia.id")
    public String getDependenciaId(){return dependenciaId;}

}
