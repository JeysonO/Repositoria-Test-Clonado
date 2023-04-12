package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

@Data
public class DocumentoAdjuntoInternoBodyRequest {

	private String id;
	private String nombreArchivo;
	private String descripcion;
	private String extension;
	private String documentoInternoId;
	private String tipoAdjunto;
	
}
