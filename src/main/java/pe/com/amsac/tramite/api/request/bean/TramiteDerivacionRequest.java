package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;

@Data
public class TramiteDerivacionRequest{

	private String usuarioInicio;
	private String usuarioFin;
	private String estado;
	private String tramiteId;

}
