package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;

import java.util.Date;

@Data
public class TramiteDerivacionRequest{

	private String usuarioInicio;
	private String usuarioFin;
	private String estado;

	private int numeroTramite;
	private Date fechaDerivacionDesde;
	private Date fechaDerivacionHasta;

	private String tramiteId;

}
