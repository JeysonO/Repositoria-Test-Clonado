package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
//@Document(collection = "calendario")
@Entity
@Table(name = "calendario")
public class Calendario extends BaseEntity {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_calendario")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "fecha")
	private String fechaDate; // dd/MM/yyyy

	@Column(name = "feriado")
	private String feriado; //S o N

	@Column(name = "semana")
	private String semana; //S o N

	@Column(name = "fecha_number")
	private Integer fechaNumber; //yyyyMMdd

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
