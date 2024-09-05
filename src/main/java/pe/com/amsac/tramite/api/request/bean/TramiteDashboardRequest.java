package pe.com.amsac.tramite.api.request.bean;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import pe.com.amsac.tramite.api.request.BaseRequest;

import java.util.Date;

@Data
public class TramiteDashboardRequest extends BaseRequest {

	private String tipoTramiteId;
	private String tipoTramite;
	private String estado;
	private String dependenciaId;
	private String usuarioId;
	private int numeroTramite;
	private String categoriaEntidadPideId;
	private String entidadPideId;
	private String asunto;

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaCreacionDesde;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date fechaCreacionHasta;
	private String razonSocial;
	private String todoAnio;

}
