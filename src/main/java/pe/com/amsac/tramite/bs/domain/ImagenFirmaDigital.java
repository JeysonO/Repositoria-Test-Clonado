package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "imagen_firma_digital")
public class ImagenFirmaDigital extends BaseEntity {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_imagen_firma_digital")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "descripcion")
	private String desripcion;

	@Column(name = "ruta")
	private String ruta;

	@Column(name = "estado")
	private String estado;

	@Column(name = "size")
	private String size;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
