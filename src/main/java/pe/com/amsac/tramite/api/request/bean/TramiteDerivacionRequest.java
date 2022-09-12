package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import pe.com.amsac.tramite.api.util.AmsacRequestBean;


@Data
public class TramiteDerivacionRequest{

	private String usuarioInicio;
	private String estado;
	private String tramiteId;
	private String proveidoAtencion;

}
