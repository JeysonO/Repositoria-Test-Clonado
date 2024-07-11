package pe.com.amsac.tramite.bs.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseAuditableEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "firma_documento")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "created_date", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "created_by_user", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "last_modified_date", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "last_modified_by_user", nullable = false))})
public class FirmaDocumento extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_firma_documento")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "id_tramite")
	private String idTramite;

	@Column(name = "nombre_original_documento")
	private String nombreOriginalDocumento;

	@Column(name = "nombre_temporal_documento")
	private String nombreTemporalDocumento;

	@Column(name = "log_firmador")
	private String logFirmador;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "tipo_documento")
	private String tipoDocumento; //DOCUMENTO_TRAMITE / DOCUMENTO_EXTERNO

	@Column(name = "estado")
	private String estado; //PENDIENTE_FIRMA: Pendiente, FIRMADO:Firmado, ERROR: Error

	@Column(name = "id_transaccion_firma")
	private String idTransaccionFirma;

	@Column(name = "fecha_firma_documento")
	private Date fechaFirmaDocumento;

	@Column(name = "fecha_registro")
	private Date fechaRegistro;

	@Column(name = "email")
	private String email;

	@Column(name = "id_documento_adjunto_origen")
	private String idDocumentoAdjuntoOrigen;

	@Column(name = "tramite_derivacion_id")
	private String tramiteDerivacionId;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
