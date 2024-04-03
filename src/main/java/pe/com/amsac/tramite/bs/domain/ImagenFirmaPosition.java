package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "imagen_firma_position")
public class ImagenFirmaPosition extends BaseEntity {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_imagen_firma_position")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "position")
	private String position;

	@Column(name = "positionpxl")
	private String positionpxl;

	@Column(name = "estado")
	private String estado;

	@Column(name = "orientacion")
	private String orientacion;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
