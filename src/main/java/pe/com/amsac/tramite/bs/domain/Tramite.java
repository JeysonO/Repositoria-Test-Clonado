package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
//@Document(collection = "tramite")
//@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "tramite")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})
public class Tramite extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_tramite")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	//Datos de configuracion del tramite
	@Column(name = "numero_tramite")
	private int numeroTramite;

	@Column(name = "mensaje_tramite")
	private String mensajeTramite;

	@Column(name = "aviso_confidencial")
	private String avisoConfidencial;

	@Column(name = "codigo_etica")
	private String codigoEtica;

	//@DBRef(db = "amsac-seguridad")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dependencia_destino", referencedColumnName = "id_dependencia")
	private Dependencia dependenciaDestino;

	//Datos del documento
	@Column(name = "fecha_documento")
	private Date fechaDocumento;

	@Column(name = "origen_documento")
	private String origenDocumento; //INTERNO, EXTERNO

	@Column(name = "numero_documento")
	private String numeroDocumento;

	@Column(name = "siglas")
	private String siglas;

	@Column(name = "folio")
	private String folio;

	@Column(name = "asunto")
	private String asunto;

	@Column(name = "atencion")
	private String atencion;

	@Column(name = "desea_factura")
	private String deseaFactura;

	@Column(name = "nombre_proyecto")
	private String nombreProyecto;

	@Column(name = "contrato_orden")
	private String contratoOrden;

	//Origen del documento
	@Column(name = "origen")
	private String origen; //INTERNO, EXTERNO

	@Column(name = "tipo_origen")
	private String tipoOrigen; //si es interno entonces va DOCUMENTO_PERSONAL.

	@Column(name = "estado")
	private String estado;

	//@Column(name = "id_tramite_relacionado")
	//private String idTramiteRelacionado;

	@Column(name = "razon_social")
	private String razonSocial;

	@Column(name = "cuo")
	private String cuo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_documento", referencedColumnName = "id_tipo_documento")
	private TipoDocumentoTramite tipoDocumento;

	@Transient
	private EntidadInterna entidadInterna;

	@Transient
	private EntidadExterna entidadExterna;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_forma_recepcion", referencedColumnName = "id_forma_recepcion")
	private FormaRecepcion formaRecepcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tramite_prioridad", referencedColumnName = "id_tramite_prioridad")
	private TramitePrioridad tramitePrioridad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dependencia_creacion", referencedColumnName = "id_dependencia")
	private Dependencia dependenciaUsuarioCreacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cargo_creacion", referencedColumnName = "id_cargo")
	private Cargo cargoUsuarioCreacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tramite_relacionado", referencedColumnName = "id_tramite")
	private Tramite tramiteRelacionado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entidad_pide", referencedColumnName = "id_entidad_pide")
	private EntidadPide entidadPide;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dependencia_remitente", referencedColumnName = "id_dependencia")
	private Dependencia dependenciaRemitente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_remitente", referencedColumnName = "id_usuario")
	private Usuario usuarioRemitente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cargo_remitente", referencedColumnName = "id_cargo")
	private Cargo cargoRemitente;

	@Column(name = "intentos_envio")
	private Integer intentosEnvio;

	@Transient
	private Long cantidadMaximaIntentos;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
