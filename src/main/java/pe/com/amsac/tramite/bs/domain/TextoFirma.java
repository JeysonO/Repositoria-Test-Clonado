package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Document(collection = "texto_firma")
public class TextoFirma extends BaseEntity {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	private String id;

	private String texto;

	private String estado;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
