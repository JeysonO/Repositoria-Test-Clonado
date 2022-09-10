package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;

@Data
public class DocumentoConfiguracionResponse {

	private String id;
	private String avisoConfidencialidad;
	private String codigoEtica;
	private String mensajeTramite;
	private String horarioRecepcion;
	private String tipoProcesoDocumento;
	private String estado;

}
