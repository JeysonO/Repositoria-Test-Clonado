package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;

//@Builder
@Data
@Entity
@Table(name = "documento_adjunto")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})
public class DocumentoAdjunto extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_documento_adjunto")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "nombre_archivo")
	private String nombreArchivo;

	@Column(name = "nombre_archivo_descarga")
	private String nombreArchivoDescarga;

	@Column(name = "nombre_archivo_server")
	private String nombreArchivoServer;

	@Column(name = "descripcion")
	private String descripcion;

	@Column(name = "extension")
	private String extension;

	@Column(name = "tipo_adjunto")
	private String tipoAdjunto;

	@Column(name = "estado")
	private String estado;

	@Column(name = "size")
	private Long size;

	@Column(name = "id_tramite_derivacion")
	private String tramiteDerivacionId;

	@Column(name = "seccion_adjunto")
	private String seccionAdjunto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tramite", referencedColumnName = "id_tramite")
	private Tramite tramite;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_creacion_adjunto", referencedColumnName = "id_usuario")
	private Usuario usuarioCreacionAdjunto;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
