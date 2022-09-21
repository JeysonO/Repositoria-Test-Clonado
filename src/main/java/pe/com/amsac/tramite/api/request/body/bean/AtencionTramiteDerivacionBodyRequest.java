package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

@Data
public class AtencionTramiteDerivacionBodyRequest {

	private String id;
	private String estadoFin;
	private String comentario;
	
}
