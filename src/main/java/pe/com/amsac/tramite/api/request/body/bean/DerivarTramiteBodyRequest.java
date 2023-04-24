package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

@Data
public class DerivarTramiteBodyRequest {

	@NotNull
	private String id;
	@NotNull
	private String usuarioFin;
	@NotNull
	private String dependenciaIdUsuarioFin;
	@NotNull
	private String cargoIdUsuarioFin;
	private String comentarioFin;
	private String proveidoAtencion;
	private String forma; //COPIA, ORIGINAL
	private LocalDate fechaMaximaAtencion;

	private String enConocimientoAtendido;

	@Mapping("usuarioFin.id")
	public String getUsuarioFin(){return usuarioFin;}
	
}
