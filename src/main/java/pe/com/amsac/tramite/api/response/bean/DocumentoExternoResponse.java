package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class DocumentoExternoResponse {

	private String id;
	private String numeroDocumento;
	private String atencion;
	private String asunto;
	private String deseaFactura;
	private String nombreProyecto;
	private String contratoOrden;
	private String estado;

}
