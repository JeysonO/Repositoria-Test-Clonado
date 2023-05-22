package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import pe.com.amsac.tramite.api.request.BaseRequest;

import java.util.Date;

@Data
public class TipoDocumentoRequest extends BaseRequest {

	private String estado;
	private String tipoAmbito;

}
