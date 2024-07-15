package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class TipoTramiteResponse {

	private String id;
	private String tipoTramite;
	private String descripcion;
	private String estado;

}
