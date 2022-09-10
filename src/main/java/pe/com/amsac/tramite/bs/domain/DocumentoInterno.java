package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "documento_interno")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "createdDate", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "createdByUser", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "lastModifiedDate", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "lastModifiedByUser", nullable = false))})

public class DocumentoInterno extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
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

	private EntidadInterna entidadInterna;
	private EntidadExterna entidadExterna;

	@DBRef
	private TipoDocumento tipoDocumento;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
