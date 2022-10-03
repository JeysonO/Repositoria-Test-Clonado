package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class DerivarTramiteBodyRequest {

	@NotNull
	private String id;
	@NotNull
	private String usuarioFin;
	private String comentarioFin;
	private String proveidoAtencion;
	private String forma; //COPIA, ORIGINAL
	private Date fechaMaximaAtencion;

	@Mapping("usuarioFin.id")
	public String getUsuarioFin(){return usuarioFin;}
	
}
