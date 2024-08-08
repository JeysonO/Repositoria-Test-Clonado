package pe.com.amsac.tramite.api.config.exceptions;

import lombok.Data;
import pe.com.amsac.tramite.api.response.bean.Mensaje;

import java.util.List;
import java.util.Map;

@Data
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = -2900331782449121653L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(List<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}

	public ServiceException(List<Mensaje> mensajes, Map atributos) {
		this.mensajes = mensajes;
		this.atributos=atributos;
	}

	private List<Mensaje> mensajes;
	private Map atributos;
}
