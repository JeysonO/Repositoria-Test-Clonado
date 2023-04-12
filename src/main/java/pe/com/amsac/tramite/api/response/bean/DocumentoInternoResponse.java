package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;
import pe.com.amsac.tramite.bs.domain.EntidadExterna;
import pe.com.amsac.tramite.bs.domain.EntidadInterna;

import java.util.Date;

@Data
public class DocumentoInternoResponse {

	private String id;
	private String mensajeTramite;
	private String avisoConfidencial;
	private String codigoEtica;
	private String origen;
	private Date fechaDocumento;
	private String tipoOrigen;
	private String numeroDocumento;
	private String siglas;
	private String asunto;
	private String estado;

	private String tipoDocumentoId;
	private EntidadInterna entidadInterna;
	private EntidadExterna entidadExterna;

	@Mapping("tipoDocumento.id")
	public String getTipoDocumentoId(){return tipoDocumentoId;}

}
