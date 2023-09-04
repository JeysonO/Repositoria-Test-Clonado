package pe.com.amsac.tramite.api.request.bean;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TareasComplementariasMigracionRequest {

    private String idTramite;
    private boolean incluirDerivaciones;
}
