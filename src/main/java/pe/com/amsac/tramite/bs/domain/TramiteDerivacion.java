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
@Document(collection = "tramite_derivacion")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "createdDate", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "createdByUser", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "lastModifiedDate", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "lastModifiedByUser", nullable = false))})

public class TramiteDerivacion extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	private String id;

	private String secuencia;

	private String usuarioInicio;

	private String usuarioFin;

	private String estadoInicio;

	private String estadoFin;

	private String comentario;

	private Date fechaInicio;

	private Date fechaFin;

	private Date fechaMaximaAtencion;

	private String proveidoAtencion;

	private String estado;

	private String forma;

	@DBRef
	private Tramite tramite;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
