package pe.com.amsac.tramite.api.request.body.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.dozer.Mapping;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcuseReciboObservacionPideRequest {

	private String tipoDocumento;
	private String fechaGeneracion;
	private String fechaHoraIngreso;
	private String estado;
	private String emisorRazonSocial;
	private String emisorRuc;
	private String asunto;
	private String cuo;
	private String observacion;
	private String pinFirma;
	
}
