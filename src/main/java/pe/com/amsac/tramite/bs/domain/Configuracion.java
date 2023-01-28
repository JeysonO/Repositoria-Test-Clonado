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

}
