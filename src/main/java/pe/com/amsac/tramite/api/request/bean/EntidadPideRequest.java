package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import pe.com.amsac.tramite.api.request.BaseRequest;

import java.util.Date;

@Data
public class EntidadPideRequest extends BaseRequest {

	private String idCategoriaEntidad;

}
