package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Document(collection = "configuracion")
public class Configuracion {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	private String id;

	private String horaMaximaRecepcionTramite;

	private String horaEnvioCorreoAcuseTramite;

	private String billingUsername;

	private String billingPassword;

	private String urlFirmaBack;

	private String urlLogBack;

	private String firmaEnv;

	private String urlFirmador;

	private String formatoFirma;

	private String firmaParagraphFormat;

}
