package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
//@Document(collection = "tramite")
//@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "tramite_envio_pide")
/*
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})

 */
public class TramiteEnvioPide extends BaseEntity {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_tramite_envio_pide")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tramite", referencedColumnName = "id_tramite")
	private Tramite tramite;

	@Column(name = "secuencia")
	private Integer secuencia;

	@Column(name = "estado")
	private String estado;

	@Column(name = "request")
	private String request;

	@Column(name = "response")
	private String response;

	@Column(name = "fecha_envio")
	private Date fechaEnvio;

	@Column(name = "fecha_respuesta")
	private Date fechaRespuesta;

	@Column(name = "created_by_user")
	private String createdByUser;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
