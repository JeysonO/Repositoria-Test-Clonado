package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
	
	@NotNull
	private String username;

	@NotNull
	private String password;

	@NotNull
	private String clientId;

	@NotNull
	private String clientSecret;

}
