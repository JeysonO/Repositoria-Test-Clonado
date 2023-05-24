package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.time.LocalDate;
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
	private LocalDate fechaMaximaAtencion;
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

	private int numeroTramite;

	private String numeroDocumento;

	private String asunto;

	private Date fechaInicio;

	private Date fechaFin;

	private Date fechaDocumento;

	private String origenDocumento;

	private String usuarioCreacion;

	private String dependenciaEmpresa;

	private String dependenciaDestinoNombre;

	private String emailUsuarioCreacion;

	private Date tramiteCreatedDate;

	private String numeroTramiteRelacionado;

	private int diasFueraPlazo;

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

	@Mapping("tramite.numeroTramite")
	public int getNumeroTramite(){return numeroTramite;}

	@Mapping("tramite.numeroDocumento")
	public String getNumeroDocumento(){return numeroDocumento;}

	@Mapping("tramite.asunto")
	public String getAsunto(){return asunto;}

	@Mapping("tramite.fechaDocumento")
	public Date getFechaDocumento(){return fechaDocumento;}

	@Mapping("tramite.origenDocumento")
	public String getOrigenDocumento(){return origenDocumento;}

	@Mapping("tramite.dependenciaDestino.nombre")
	public String getDependenciaDestinoNombre(){return dependenciaDestinoNombre;}

	@Mapping("tramite.createdDate")
	public Date getTramiteCreatedDate(){return tramiteCreatedDate;}

}
