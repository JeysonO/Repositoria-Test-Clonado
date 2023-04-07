package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

import static javax.persistence.TemporalType.TIMESTAMP;

@Data
@Document(collection = "tramite")
@EntityListeners(AuditingEntityListener.class)
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "createdDate", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "createdByUser", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "lastModifiedDate", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "lastModifiedByUser", nullable = false))})
public class Tramite extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	private String id;

	//Datos de configuracion del tramite
	private int numeroTramite;
	private String mensajeTramite;
	private String avisoConfidencial;
	private String codigoEtica;

	@DBRef(db = "amsac-seguridad")
	private Dependencia dependenciaDestino;

	//Datos del documento
	private Date fechaDocumento;
	private String origenDocumento; //INTERNO, EXTERNO
	private String numeroDocumento;
	private String siglas;
	private String folio;
	private String asunto;
	private String atencion;
	private String deseaFactura;
	private String nombreProyecto;
	private String contratoOrden;

	//Origen del documento
	private String origen; //INTERNO, EXTERNO
	private String tipoOrigen; //si es interno entonces va DOCUMENTO_PERSONAL.

	private String estado;

	private String idTramiteRelacionado;

	@DBRef
	private TipoDocumento tipoDocumento;

	private EntidadInterna entidadInterna;

	private EntidadExterna entidadExterna;

	@DBRef
	private FormaRecepcion formaRecepcion;

	@DBRef
	private TramitePrioridad tramitePrioridad;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
