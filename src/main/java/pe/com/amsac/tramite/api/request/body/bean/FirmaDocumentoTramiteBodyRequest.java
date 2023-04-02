package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.util.Date;

@Data
public class FirmaDocumentoTramiteBodyRequest {

	private String documentoAdjuntoId;
	private String textoFirma;
	private String pin;
	//private String imagenFirmaDigitalId;
	private String positionId;
	private String positionCustom;
	private String usuarioFirmaLogoId;
}
