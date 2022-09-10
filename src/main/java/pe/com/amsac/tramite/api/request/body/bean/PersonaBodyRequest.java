package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class PersonaBodyRequest {

	private String id;
	private String numeroDocumento;
	private String razonSocialNombre;
	private String tipoPersona;
	private String estado;
	private String tipoDocumentoId;

	@Mapping("tipoDocumento.id")
	public String getTipoDocumentoId(){return tipoDocumentoId;}
	
}
