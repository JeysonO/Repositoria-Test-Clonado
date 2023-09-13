package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;

@Data
public class DocumentoAdjuntoCargaFromDirectoryBodyRequest {

	private String tramiteIdAnterior;
	private String tramiteIdNuevo;

}
