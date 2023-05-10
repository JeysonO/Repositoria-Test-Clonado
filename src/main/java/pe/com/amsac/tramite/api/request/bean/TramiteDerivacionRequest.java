package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import pe.com.amsac.tramite.api.request.BaseRequest;

import java.util.Date;

@Data
public class TramiteDerivacionRequest extends BaseRequest {

	private String usuarioInicio;
	private String usuarioFin;
	private String estado;

	private int numeroTramite;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaDerivacionDesde;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaDerivacionHasta;

	private String forma;

	private String tramiteId;
	private String asunto;

	private String dependenciaIdUsuarioInicio;
	private String dependenciaIdUsuarioFin;

	private String cargoIdUsuarioInicio;
	private String cargoIdUsuarioFin;

	private String notEstadoFin;

	private String estadoFin;
	private String razonSocial;


}
