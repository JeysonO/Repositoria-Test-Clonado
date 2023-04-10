package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class FirmaDocumentoTramiteBodyRequest {

	@NotBlank
	private String documentoAdjuntoId;
	@NotBlank
	private String textoFirma;
	@NotBlank
	private String pin;
	//private String imagenFirmaDigitalId;
	//private String positionId;
	private String position;
	private String orientacion;
	private String positionCustom;
	@NotBlank
	private String usuarioFirmaLogoId;
}
