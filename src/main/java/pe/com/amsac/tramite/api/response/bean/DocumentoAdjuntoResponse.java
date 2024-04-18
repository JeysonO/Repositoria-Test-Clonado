package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;
import pe.com.amsac.tramite.api.file.bean.UploadFileResponse;

@Data
public class DocumentoAdjuntoResponse {

	private String id;
	private String nombreArchivo;
	private String descripcion;
	private String extension;
	private String tipoAdjunto;
	private String tramiteId;
	private String estado;
	private String seccionAdjunto;
	//private String usuarioCreacionAdjuntoNombre;
	private String createdByUser;
	private UsuarioDTOResponse usuarioCreacionAdjunto;

	private UploadFileResponse uploadFileResponse;

	@Mapping("tramite.id")
	public String getTramiteId(){return tramiteId;}

	public UploadFileResponse getUploadFileResponse() {
		return uploadFileResponse;
	}

	public void setUploadFileResponse(UploadFileResponse uploadFileResponse) {
		this.uploadFileResponse = uploadFileResponse;
	}
	/*
	@Mapping("usuarioCreacionAdjunto.nombreCompleto")
	public String getUsuarioCreacionAdjuntoNombre(){return usuarioCreacionAdjuntoNombre;}
	*/
}
