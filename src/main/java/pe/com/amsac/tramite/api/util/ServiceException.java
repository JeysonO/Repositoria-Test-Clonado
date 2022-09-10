package pe.com.amsac.tramite.api.util;

import lombok.Data;
import pe.com.amsac.tramite.api.response.bean.Mensaje;

import java.util.List;

@Data
public class ServiceException extends Exception {

	private static final long serialVersionUID = -2900331782449121653L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(List<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}

	private List<Mensaje> mensajes;

}
