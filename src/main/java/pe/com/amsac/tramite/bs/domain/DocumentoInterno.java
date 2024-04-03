package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
//@Table(name = "documento_interno")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})
public class DocumentoInterno extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_documento_interno")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	private String mensajeTramite;

	private String avisoConfidencial;

	private String codigoEtica;

	private String origen;

	private Date fechaDocumento;

	private String tipoOrigen;

	private String numeroDocumento;

	private String siglas;

	private String asunto;

	private String estado;

	/*
	private EntidadInterna entidadInterna;
	private EntidadExterna entidadExterna;

	@DBRef
	private TipoDocumento tipoDocumento;
	*/

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
