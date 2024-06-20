package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class TipoDocumentoPideResponse {

	private String tipoDocumentoPideId;
	private String tipoDocumentoPide;
	private String nombre;

	@Mapping("id")
	public String getTipoDocumentoPideId(){return tipoDocumentoPideId;}

}
