package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "cargo")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})

public class Cargo extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_cargo")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "cargo")
	private String cargo;

	@Column(name = "nombre")
	private String nombre;

	@Column(name = "estado")
	private String estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dependencia", referencedColumnName = "id_dependencia")
	private Dependencia dependencia;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
