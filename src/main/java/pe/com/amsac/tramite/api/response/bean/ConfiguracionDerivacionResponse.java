package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class ConfiguracionDerivacionResponse {

    private String id;
    private CargoResponse cargoOrigen;
    private CargoResponse cargoDestino;
    private String estado;

}
