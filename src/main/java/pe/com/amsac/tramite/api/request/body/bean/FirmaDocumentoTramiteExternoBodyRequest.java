package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
public class FirmaDocumentoTramiteExternoBodyRequest {

	private String textoFirma;
	private String pin;
	//private String imagenFirmaDigitalId;
	private String usuarioFirmaLogoId;
	//private String positionId;
	private String position;
	private String orientacion;
	private String positionCustom;
	private String email;
	private MultipartFile file;
}
