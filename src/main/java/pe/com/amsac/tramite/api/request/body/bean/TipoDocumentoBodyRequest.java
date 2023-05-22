package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

@Data
public class TipoDocumentoBodyRequest {

	private String id;
	private String tipoDocumento;
	private String descripcion;
	private String tipoAmbito;
	
}
