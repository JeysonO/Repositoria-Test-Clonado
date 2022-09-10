package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class TipoDocumentoResponse {

	private String id;
	private String tipoDocumento;
	private String descripcion;
	private String estado;

}
