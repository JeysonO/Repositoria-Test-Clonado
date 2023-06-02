package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import javax.validation.constraints.NotBlank;

@Data
public class ConfiguracionUsuarioBodyRequest {

	private String id;
	@NotBlank
	private String usuarioId;

	private boolean enviarAlertaTramitePendiente;

	private String estado;

	@Mapping("usuario.id")
	public String getUsuarioId(){return usuarioId;}

}
