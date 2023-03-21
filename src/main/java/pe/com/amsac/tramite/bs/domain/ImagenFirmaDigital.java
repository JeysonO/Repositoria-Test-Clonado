package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@Document(collection = "imagen_firma_digital")
public class ImagenFirmaDigital extends BaseEntity {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	private String id;

	private String desripcion;

	private String ruta;

	private String estado;

	private String size;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
