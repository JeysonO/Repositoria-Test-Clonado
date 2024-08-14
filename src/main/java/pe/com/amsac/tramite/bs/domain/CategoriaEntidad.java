package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "categoria_entidad")
public class CategoriaEntidad {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_categoria_entidad")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "categoria_entidad")
	private String categoriaEntidad;

	@Column(name = "nombre")
	private String nombre;

	@Column(name = "estado")
	private String estado;

}
