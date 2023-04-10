package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.util.Date;
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
	private String usuario;
	private String tipoDocumentoNombre;
	private String persona;
	private String tipoPersona;
	private String estado;
	private String forma;
	private Date createdDate;

	private List<TramiteDerivacionReporteResponse> tramiteDerivacion;

	@Mapping("tipoDocumento.tipoDocumento")
	public String getTipoDocumentoNombre(){return tipoDocumentoNombre;}

}
