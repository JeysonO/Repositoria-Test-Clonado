package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class DocumentoAdjuntoExternoResponse {

	private String id;
	private String nombreArchivo;
	private String descripcion;
	private String extension;
	private String documentoExternoId;
	private String tipoAdjunto;
	private String estado;

}
