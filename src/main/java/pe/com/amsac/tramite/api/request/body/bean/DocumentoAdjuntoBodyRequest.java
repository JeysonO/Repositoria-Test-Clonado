package pe.com.amsac.tramite.api.request.body.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.dozer.Mapping;
import org.springframework.web.multipart.MultipartFile;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentoAdjuntoBodyRequest {

	private String id;
	private String nombreArchivo;
	private String descripcion;
	private String extension;
	private String tipoAdjunto;
	private MultipartFile file;
	private String tramiteId;
	private String tramiteDerivacionId;
	private String seccionAdjunto;
	private boolean omitirValidacionAdjunto;

	@Mapping("tramite.id")
	public String getTramiteId(){return tramiteId;}
}
