package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

@Data
public class DocumentoExternoBodyRequest {

	private String id;
	private String numeroDocumento;
	private String atencion;
	private String asunto;
	private String deseaFactura;
	private String nombreProyecto;
	private String contratoOrden;

}
