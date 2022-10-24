package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;

import java.util.List;

@Data
public class TramiteReporteResponse {

	private String id;

	//Datos de configuracion del tramite
	private int numeroTramite;

	//Datos del documento
	private String origenDocumento; //INTERNO, EXTERNO
	private String numeroDocumento;
	private String asunto;

	//Otros datos
	private String tipoDocumentoNombre;
	private String tipoPersona;
	private String estado;

	private List<TramiteDerivacion> tramiteDerivacion;

	@Mapping("tipoDocumento.tipoDocumento")
	public String getTipoDocumentoNombre(){return tipoDocumentoNombre;}

}
