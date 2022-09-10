package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.util.Date;

@Data
public class TramiteDerivacionResponse {

	private String id;
	private String secuencia;
	private String usuarioInicio;
	private String usuarioFin;
	private String estadoFin;
	private String comentario;
	private Date fechaMaximaAtencion;
	private String proveidoAtencion;
	private String estado;
	private String forma;

	private String tramiteId;

	@Mapping("tramite.id")
	public String getTramiteId(){return tramiteId;}

}
