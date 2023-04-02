package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import javax.validation.constraints.NotNull;

@Data
public class UsuarioFirmaBodyRequest {

	private String id;
	@NotNull
	private String usuarioId;
	@NotNull
	private String usernameServicioFirma;
	@NotNull
	private String passwordServicioFirma;
	@NotNull
	private String siglaFirma;
	@NotNull
	private String estado;

	@Mapping("usuario.id")
	public String getUsuarioId(){return usuarioId;}


}
