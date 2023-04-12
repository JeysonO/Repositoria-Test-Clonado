package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

@Data
public class AlertaCorreoDestinatarioResponse {

	private String id;
	private String correoDestinatario;
	private String tipoDestino;
	private String estado;

	private String alertaId;

	@Mapping("alerta.id")
	public String getAlertaId(){return alertaId;}

}
