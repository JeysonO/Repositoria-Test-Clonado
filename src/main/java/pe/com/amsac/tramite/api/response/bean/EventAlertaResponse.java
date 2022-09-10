package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class EventAlertaResponse {

	private String id;
	private String evento;
	private String estado;

	private String alertaId;

	@Mapping("alerta.id")
	public String getAlertaId(){return alertaId;}

}
