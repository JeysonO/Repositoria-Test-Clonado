package pe.com.amsac.tramite.api.response.bean;

import lombok.Data;
import org.dozer.Mapping;

import javax.persistence.Column;

@Data
public class CategoriaEntidadResponse {

	private String id;
	private String categoriaEntidad;
	private String nombre;
	private String estado;

}
