package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class EntidadPideResponse {

	private String entidadPideId;
	private String ruc;
	private String nombre;

	@Mapping("id")
	public String getEntidadPideId(){return entidadPideId;}

}
