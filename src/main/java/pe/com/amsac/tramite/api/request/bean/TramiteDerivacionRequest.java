package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import pe.com.amsac.tramite.api.request.BaseRequest;

import java.util.Date;

@Data
public class TramiteDerivacionRequest extends BaseRequest {

	private String usuarioInicio;
	private String usuarioFin;
	private String estado;

	private int numeroTramite;
	private Date fechaDerivacionDesde;
	private Date fechaDerivacionHasta;

	private String forma;

	private String tramiteId;
	private String asunto;

}
