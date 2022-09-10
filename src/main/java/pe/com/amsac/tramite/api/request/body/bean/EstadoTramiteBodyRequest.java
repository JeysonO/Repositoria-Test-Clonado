package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

@Data
public class EstadoTramiteBodyRequest {

	private String id;
	private String estadoTramite;
	private String descripcion;

}
