package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
//@Table(name = "persona")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})
public class Persona extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_persona")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "numero_documento")
	private String numeroDocumento;

	@Column(name = "razon_social_nombre")
	private String razonSocialNombre;

	@Column(name = "tipo_persona")
	private String tipoPersona;

	@Column(name = "estado")
	private String estado;

	//@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name = "id_tipo_documento", referencedColumnName = "id_tipo_documento")
	private TipoDocumento tipoDocumento;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
