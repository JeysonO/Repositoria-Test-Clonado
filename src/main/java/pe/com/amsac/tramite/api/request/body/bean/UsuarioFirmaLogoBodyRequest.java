package pe.com.amsac.tramite.api.request.body.bean;

import lombok.Data;
import org.dozer.Mapping;

import java.util.Date;

@Data
public class UsuarioFirmaLogoBodyRequest {

	private String id;
	private boolean esFavorito;

	
}
