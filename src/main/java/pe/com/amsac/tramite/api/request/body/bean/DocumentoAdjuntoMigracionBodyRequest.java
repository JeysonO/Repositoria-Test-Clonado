package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentoAdjuntoMigracionBodyRequest {

	private String tramiteId; //id del tramite en mongo
	private String carpetaDocumento; //fa_archivo_old
	private String nombreArchivo; //fa_archivo_bytes
	private String nombreOriginalArchivo; //fa_nombre
	private String descripcion; //fa_descripcion

}
