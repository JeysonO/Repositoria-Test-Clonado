package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class TramiteBodyRequest {

	private String id;
	private String numero;

	private String estadoTramiteId;
	private String formaRecepcionId;
	private String tramitePrioridadId;

	@Mapping("estadoTramite.id")
	public String getEstadoTramiteId(){return estadoTramiteId;}

	@Mapping("formaRecepcion.id")
	public String getFormaRecepcionId(){return formaRecepcionId;}

	@Mapping("tramitePrioridad.id")
	public String getTramitePrioridadId(){return tramitePrioridadId;}
	
}
