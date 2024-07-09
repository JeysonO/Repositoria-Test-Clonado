package pe.com.amsac.tramite.api.request.body.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.dozer.Mapping;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosFirmaDocumentoRequest {

	//Se agregan atributos para la firma del documento
	private String textoFirma;
	private String position;
	private String orientacion;
	private String positionCustom;
	private String pin;
	private String usuarioFirmaLogoId;

}
