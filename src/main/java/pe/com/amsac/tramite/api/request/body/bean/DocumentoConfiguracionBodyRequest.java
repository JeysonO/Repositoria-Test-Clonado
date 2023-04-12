package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

@Data
public class DocumentoConfiguracionBodyRequest {

	private String id;
	private String avisoConfidencialidad;
	private String codigoEtica;
	private String mensajeTramite;
	private String horarioRecepcion;
	private String tipoProcesoDocumento;
	
}
