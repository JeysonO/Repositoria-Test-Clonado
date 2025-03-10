package pe.com.amsac.tramite.api.request.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentoAdjuntoRequest {

	private String id;
	private String tramiteId;
	private String nombreArchivo;
	private String tipoAdjunto;
	private String tramiteDerivacionId;
	private String estado;
	private String seccionAdjunto;
	private String descripcion;

}
