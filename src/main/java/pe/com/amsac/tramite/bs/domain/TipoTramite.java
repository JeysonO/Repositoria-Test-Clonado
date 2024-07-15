package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tipo_tramite")
public class TipoTramite extends BaseEntity {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_tipo_tramite")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "tipo_tramite")
	private String tipoTramite;

	@Column(name = "descripcion")
	private String descripcion;

	@Column(name = "estado")
	private String estado;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
