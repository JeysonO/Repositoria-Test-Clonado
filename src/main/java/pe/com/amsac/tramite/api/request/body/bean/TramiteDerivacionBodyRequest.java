package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.time.LocalDate;
import java.util.Date;

@Data
public class TramiteDerivacionBodyRequest {

	private String id;
	private int secuencia;
	private String usuarioInicio;
	private String usuarioFin;
	private String estadoInicio;
	private String estadoFin;
	private String comentarioInicio;
	private String comentarioFin;
	private Date fechaInicio;
	private Date fechaFin;
	private LocalDate fechaMaximaAtencion;
	//private Date fechaMaximaAtencion;
	private String proveidoAtencion;
	private String forma;

	private String tramiteId;

	private String dependenciaIdUsuarioInicio;
	private String dependenciaIdUsuarioFin;

	private String cargoIdUsuarioInicio;
	private String cargoIdUsuarioFin;

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

	@Mapping("cargoUsuarioInicio.id")
	public String getCargoIdUsuarioInicio(){return cargoIdUsuarioInicio;}

	@Mapping("cargoUsuarioFin.id")
	public String getCargoIdUsuarioFin(){return cargoIdUsuarioFin;}
	
}
