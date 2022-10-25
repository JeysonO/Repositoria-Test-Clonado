package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.time.LocalDate;

@Data
public class TramiteDerivacionReporteResponse {

	private String id;
	private int secuencia;
	private String usuarioInicio;
	private String usuarioFin;
	private String estadoInicio;
	private String estadoFin;
	private String comentarioInicio;
	private String comentarioFin;
	private LocalDate fechaMaximaAtencion;
	private String proveidoAtencion;
	private String estado;
	private String forma;

	private String tramiteId;

	@Mapping("usuarioInicio.nombreCompleto")
	public String getUsuarioInicio(){return usuarioInicio;}
	@Mapping("usuarioFin.nombreCompleto")
	public String getUsuarioFin(){return usuarioFin;}
	@Mapping("tramite.id")
	public String getTramiteId(){return tramiteId;}

}
