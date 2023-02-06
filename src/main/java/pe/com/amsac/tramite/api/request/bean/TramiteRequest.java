package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.util.Date;

@Data
public class TramiteRequest {

	private String createdByUser;
	private String estado;
	//private String id;

	private int numeroTramite;
	//private Date fechaDocumentoDesde;
	//private Date fechaDocumentoHasta;
	private Date fechaCreacionDesde;
	private Date fechaCreaciontoHasta;
	private String misTramite;
	private String asunto;
	private String soloOriginal;
}
