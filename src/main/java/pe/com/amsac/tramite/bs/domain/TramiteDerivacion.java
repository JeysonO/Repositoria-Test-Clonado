package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
//@Document(collection = "tramite_derivacion")
@Entity
@Table(name = "tramite_derivacion")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})
public class TramiteDerivacion extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_tramite_derivacion")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "secuencia")
	private int secuencia;

	//private String usuarioInicio;
	//@DBRef(db = "amsac-seguridad")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_inicio", referencedColumnName = "id_usuario")
	private Usuario usuarioInicio;

	//@DBRef(db = "amsac-seguridad")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dependencia_usuario_inicio", referencedColumnName = "id_dependencia")
	private Dependencia dependenciaUsuarioInicio;

	//private String usuarioFin;
	//@DBRef(db = "amsac-seguridad")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_fin", referencedColumnName = "id_usuario")
	private Usuario usuarioFin;

	//@DBRef(db = "amsac-seguridad")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dependencia_usuario_fin", referencedColumnName = "id_dependencia")
	private Dependencia dependenciaUsuarioFin;

	@Column(name = "estado_inicio")
	private String estadoInicio;

	@Column(name = "estado_fin")
	private String estadoFin;

	@Column(name = "comentario_inicio")
	private String comentarioInicio;

	@Column(name = "comentario_fin")
	private String comentarioFin;

	@Column(name = "fecha_inicio")
	private Date fechaInicio;

	@Column(name = "fecha_fin")
	private Date fechaFin;

	@Column(name = "fecha_maximo_atencion")
	private Date fechaMaximaAtencion;

	@Column(name = "proveido_atencion")
	private String proveidoAtencion;

	@Column(name = "estado")
	private String estado;

	@Column(name = "foma")
	private String forma;

	//@DBRef
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tramite", referencedColumnName = "id_tramite")
	private Tramite tramite;

	//@DBRef(db = "amsac-seguridad")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cargo_usuario_inicio", referencedColumnName = "id_cargo")
	private Cargo cargoUsuarioInicio;

	//@DBRef(db = "amsac-seguridad")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cargo_usuario_fin", referencedColumnName = "id_cargo")
	private Cargo cargoUsuarioFin;

	//@Transient
	@Transient
	private String dependenciaNombreUsuarioInicio;

	@Transient
	private String cargoNombreUsuarioInicio;

	//@Transient
	@Transient
	private String dependenciaNombreUsuarioFin;

	@Transient
	private String cargoNombreUsuarioFin;

	//@Transient
	@Transient
	private String usuarioInicioNombreCompleto;

	//@Transient
	@Transient
	private String usuarioFinNombreCompleto;

	//@Transient
	@Transient
	private String usuarioCreacion;

	//@Transient
	@Transient
	private String dependenciaEmpresa;

	//@Transient
	@Transient
	private String emailUsuarioCreacion;

	@Column(name = "numero_tramite_relacionado")
	private int numeroTramiteRelacionado;

	@Column(name = "con_adjunto")
	private boolean conAdjunto;

	@Transient
	private int diasFueraPlazo;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

	/*
	public String getUsuarioInicioNombreCompleto(){
		if(usuarioInicio!=null)
			return usuarioInicio.getNombreCompleto();
		else
			return "";
	}

	public String getUsuarioFinNombreCompleto(){
		if(usuarioFin!=null)
			return usuarioFin.getNombreCompleto();
		else
			return "";
	}
	*/

}
