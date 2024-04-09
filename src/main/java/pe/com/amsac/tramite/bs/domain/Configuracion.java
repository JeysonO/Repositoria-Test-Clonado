package pe.com.amsac.tramite.bs.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import pe.com.amsac.tramite.api.util.BaseEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "configuracion")
public class Configuracion extends BaseEntity {

	private static final long serialVersionUID = 7857201376677339392L;

	@Id
	@Column(name = "id_configuracion")
	@GeneratedValue(generator = "uuid-hibernate-generator")
	@GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "hora_maxima_recepcion_tramite")
	private String horaMaximaRecepcionTramite;

	@Column(name = "hora_envio_correo_acuse")
	private String horaEnvioCorreoAcuseTramite;

	@Column(name = "billling_username")
	private String billingUsername;

	@Column(name = "billing_password")
	private String billingPassword;

	@Column(name = "url_firma_back")
	private String urlFirmaBack;

	@Column(name = "url_log_back")
	private String urlLogBack;

	@Column(name = "firma_env")
	private String firmaEnv;

	@Column(name = "url_firmador")
	private String urlFirmador;

	@Column(name = "formato_firma")
	private String formatoFirma;

	@Column(name = "firma_paragraph_format")
	private String firmaParagraphFormat;

	@Column(name = "margen_firma")
	private int margenFirma;

	@Column(name = "tamano_alto_hoja")
	private int tamanoAltoHoja;

	@Column(name = "tamano_ancho_hoja")
	private int tamanoAnchoHoja;

	@Column(name = "ancho_firma")
	private int anchoFirma;

	@Column(name = "alto_firma")
	private int altoFirma;

	@Column(name = "cantidad_firmas_vertical")
	private int cantidadFirmasVertical;

	@Column(name = "cantidad_firmas_horizontal")
	private int cantidadFirmasHorizontal;

	@Override
	public Serializable getEntityId() {
		return getId();
	}

}
