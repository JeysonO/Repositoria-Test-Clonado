package pe.com.amsac.tramite.bs.domain;

import lombok.Builder;
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

@Builder
@Data
@Document(collection = "firma_documento")
@AttributeOverrides(value = {
		@AttributeOverride(name = "createdDate", column = @Column(name = "createdDate", updatable = false)),
		@AttributeOverride(name = "createdByUser", column = @Column(name = "createdByUser", updatable = false)),
		@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "lastModifiedDate", nullable = false)),
		@AttributeOverride(name = "lastModifiedByUser", column = @Column(name = "lastModifiedByUser", nullable = false))})
public class FirmaDocumento extends BaseAuditableEntity<String> {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	private String id;

	private String idTramite;

	private String nombreOriginalDocumento;

	private String nombreTemporalDocumento;

	private String logFirmador;

	private String contentType;

	private String tipoDocumento; //DOCUMENTO_TRAMITE / DOCUMENTO_EXTERNO

	private String estado; //PENDIENTE_FIRMA: Pendiente, FIRMADO:Firmado, ERROR: Error

	private String idTransaccionFirma;

	private Date fechaFirmaDocumento;

	private Date fechaRegistro;

	private String email;

	private String idDocumentoAdjuntoOrigen;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
