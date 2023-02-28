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
@Document(collection = "tramite")
public class TramiteMigracion {

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

	private Date createdDate;
	private Date lastModifiedDate;
	private String createdByUser;
	private String lastModifiedByUser;

}
