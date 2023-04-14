package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.time.LocalDate;
import java.util.Date;

@Data
public class TramiteDerivacionMigracionBodyRequest {

	private String usuarioInicio;
	private String usuarioFin;
	private String estadoInicio;
	private String estadoFin;
	private Date fechaInicio;
	private Date fechaFin;
	private String proveidoAtencion;
	private String forma;
	private String estado;

	private String tramiteId;

	private String dependenciaIdUsuarioInicio;
	private String dependenciaIdUsuarioFin;

	@Mapping("tramite.id")
	public String getTramiteId(){return tramiteId;}

	@Mapping("usuarioFin.id")
	public String getUsuarioFin(){return usuarioFin;}

	@Mapping("usuarioInicio.id")
	public String getUsuarioInicio(){return usuarioInicio;}

	@Mapping("dependenciaUsuarioInicio.id")
	public String getDependenciaIdUsuarioInicio(){return dependenciaIdUsuarioInicio;}

	@Mapping("dependenciaUsuarioFin.id")
	public String getDependenciaIdUsuarioFin(){return dependenciaIdUsuarioFin;}
	
}
