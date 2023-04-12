package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

@Data
public class DocumentoAdjuntoExternoBodyRequest {

	private String id;
	private String nombreArchivo;
	private String descripcion;
	private String extension;
	private String documentoExternoId;
	private String tipoAdjunto;
	
}
