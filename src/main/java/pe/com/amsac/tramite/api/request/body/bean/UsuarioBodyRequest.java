package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import javax.validation.constraints.NotNull;

@Data
public class UsuarioBodyRequest {


	private String id;
	@NotNull
	private String usuario;
	@NotNull
	private String nombre;
	@NotNull
	private String apePaterno;
	@NotNull
	private String apeMaterno;
	@NotNull
	private String personaId;
	@NotNull
	private String email;

	@Mapping("persona.id")
	public String getPersonaId(){return personaId;}
}
