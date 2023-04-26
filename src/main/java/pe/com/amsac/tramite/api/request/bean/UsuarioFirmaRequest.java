package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import pe.com.amsac.tramite.api.request.BaseRequest;

import java.util.Date;

@Data
public class UsuarioFirmaRequest extends BaseRequest {

	private String nombre;


}
