package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;

import java.util.Date;
import java.util.List;

@Data
public class WrapperTramiteDerivacionMigracionBodyRequest {

	private List<TramiteDerivacionMigracionBodyRequest> tramiteDerivacion;

}
