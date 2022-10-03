package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

import javax.persistence.Transient;
import java.util.Date;

@Data
public class TramiteDerivacionResponse {

	private String id;
	private int secuencia;
	private String usuarioInicioId;
	private String usuarioFinId;
	private String estadoInicio;
	private String estadoFin;
	private String comentarioInicio;
	private String comentarioFin;
	private Date fechaMaximaAtencion;
	private String proveidoAtencion;
	private String estado;
	private String forma;

	private String tramiteId;

	private String dependenciaNombreUsuarioInicio;

	private String cargoNombreUsuarioInicio;

	private String dependenciaNombreUsuarioFin;

	private String cargoNombreUsuarioFin;

	private String usuarioInicioNombreCompleto;

	private String usuarioFinNombreCompleto;

	private String formaRecepcionNombre;

	private String tipoDocumentoNombre;

	@Mapping("tramite.id")
	public String getTramiteId(){return tramiteId;}

	@Mapping("usuarioInicio.id")
	public String getUsuarioInicioId(){return usuarioInicioId;}

	@Mapping("usuarioFin.id")
	public String getUsuarioFinId(){return usuarioFinId;}

	@Mapping("tramite.formaRecepcion.formaRecepcion")
	public String getFormaRecepcionNombre(){return formaRecepcionNombre;}

	@Mapping("tramite.tipoDocumento.tipoDocumento")
	public String getTipoDocumentoNombre(){return tipoDocumentoNombre;}

}
