package pe.com.amsac.tramite.api.config.exceptions;

import java.util.List;

public class BusinessServiceException extends Exception {
	
	private static final long serialVersionUID = 4917466823521823849L;
	
	private String message;
	private List<String> errores;
	
	public BusinessServiceException(String message){
		super(message);
		this.message = message;
	}
	
	public BusinessServiceException(String message, List<String> errores){
		super(message);
		this.message = message;
		this.errores = errores;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getErrores() {
		return errores;
	}

	public void setErrores(List<String> errores) {
		this.errores = errores;
	}
	
	
}
