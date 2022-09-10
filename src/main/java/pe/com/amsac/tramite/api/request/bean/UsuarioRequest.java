package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import pe.com.amsac.tramite.api.util.AmsacRequestBean;


@Data
public class UsuarioRequest extends AmsacRequestBean {

	private String nombre;

	private String email;

}
