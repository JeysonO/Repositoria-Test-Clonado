package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;

import java.util.Date;

@Data
public class TramiteDerivacionRequest{

	private String usuarioInicio;
	private String usuarioFin;
	private String estado;
	private String tramiteId;

	private int numeroTramite;
	private Date fechaDocumentoDesde;
	private Date fechaDocumentoHasta;
	private Date fechaCreacionDesde;
	private Date fechaCreaciontoHasta;
	private String emailEmisor;
	private String dependenciaId;
	private String tipoDocumentoId;

}
