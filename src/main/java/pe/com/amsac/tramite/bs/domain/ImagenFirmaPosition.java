package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@Document(collection = "imagen_firma_position")
public class ImagenFirmaPosition extends BaseEntity {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	private String id;

	private String position;

	private String positionpxl;

	private String estado;

	private String orientacion;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
