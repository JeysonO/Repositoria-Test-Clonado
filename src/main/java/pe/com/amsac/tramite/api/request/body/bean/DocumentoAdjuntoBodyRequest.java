package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

@Data
public class DocumentoAdjuntoBodyRequest {

	private String id;
	private String nombreArchivo;
	private String descripcion;
	private String extension;
	private String tramiteId;
	private String tipoAdjunto;
	
}
