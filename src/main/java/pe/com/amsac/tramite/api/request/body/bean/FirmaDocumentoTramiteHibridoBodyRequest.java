package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Data
public class FirmaDocumentoTramiteHibridoBodyRequest {

	private String tramiteId;
	private String textoFirma;
	private String pin;
	//private String imagenFirmaDigitalId;
	//private String positionId;
	private String position;
	private String orientacion;
	private String positionCustom;
	private String tramiteDerivacionId;
	private MultipartFile file;
	private String usuarioFirmaLogoId;
	private String tipoDocumentoFirma;
}
