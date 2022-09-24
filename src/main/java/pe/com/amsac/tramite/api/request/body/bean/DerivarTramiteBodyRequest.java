package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class DerivarTramiteBodyRequest {

	@NotNull
	private String id;
	@NotNull
	private String usuarioFin;
	private String comentarioFin;
	private String proveidoAtencion;
	private Date fechaMaximaAtencion;
	
}
