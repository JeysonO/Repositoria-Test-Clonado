package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class AlertaCorreoDestinatarioBodyRequest {

	private String id;
	private String correoDestinatario;
	private String tipoDestino;
	private String alertaId;

	@Mapping("alerta.id")
	public String getAlertaId(){return alertaId;}
	
}
