package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class TramiteResponse {

	private String id;
	private String numero;
	private String estado;

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
