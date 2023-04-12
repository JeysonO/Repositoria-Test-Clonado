package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class PersonaResponse {

	private String id;
	private String numeroDocumento;
	private String razonSocialNombre;
	private String tipoPersona;
	private String tipoDocumentoId;
	private String estado;

	@Mapping("tipoDocumento.id")
	public String getTipoDocumentoId(){return tipoDocumentoId;}

}
