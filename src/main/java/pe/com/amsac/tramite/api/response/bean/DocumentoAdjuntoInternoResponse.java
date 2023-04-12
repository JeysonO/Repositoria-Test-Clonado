package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class DocumentoAdjuntoInternoResponse {

	private String id;
	private String nombreArchivo;
	private String descripcion;
	private String extension;
	private String documentoInternoId;
	private String tipoAdjunto;
	private String estado;

}
