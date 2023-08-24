package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

import java.util.List;

@Data
public class WrapperTramiteDerivacionBodyRequest {

	private List<TramiteDerivacionBodyRequest> tramiteDerivacion;

}
