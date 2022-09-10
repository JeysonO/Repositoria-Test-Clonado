package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class EventAlertaBodyRequest {

	private String id;
	private String evento;

	private String alertaId;

	@Mapping("alerta.id")
	public String getAlertaId(){return alertaId;}
	
}
