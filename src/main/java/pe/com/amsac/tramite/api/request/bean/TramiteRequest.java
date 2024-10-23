package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import org.dozer.Mapping;
import org.springframework.format.annotation.DateTimeFormat;
import pe.com.amsac.tramite.api.request.BaseRequest;

import java.util.Date;

@Data
public class TramiteRequest extends BaseRequest {

	private String createdByUser;
	private String estado;
	private String id;

	private int numeroTramite;
	//private Date fechaDocumentoDesde;
	//private Date fechaDocumentoHasta;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaCreacionDesde;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaCreaciontoHasta;
	private String misTramite;
	private String asunto;
	private String razonSocial;
	private String soloOriginal;
	private String cargoUsuarioCreacion;
	private String origen;
	private String cuo;
	private String tipoTramite;
	private String tipoTramiteId;
	private String tramiteDependencia;

}
