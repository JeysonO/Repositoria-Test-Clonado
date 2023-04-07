package pe.com.amsac.tramite.api.config.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

public class ExceptionResponse {

	//@JsonFormat(shape = JsonFormat.Shape.STRING,timezone = "America/Lima",pattern = "dd-MM-yyyy HH:mm:ss a")
	//@JsonFormat(shape = JsonFormat.Shape.STRING,timezone = "America/Lima")
	@JsonFormat(shape = JsonFormat.Shape.STRING,timezone = "America/Lima",pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date timestamp;
	private HttpStatus error;
	private Integer status;
	private String message;
	private List<String> details;
	private String path;
	
	public ExceptionResponse(HttpStatus httpStatus, Integer codeStatus, String errorMessage, List<String> details, String path) {
        super();
        this.timestamp = new Date();
        this.error = httpStatus;
        this.status = codeStatus;
        this.message = errorMessage;
        this.details = details;
        this.path = path;
        
    }

	public HttpStatus getError() {
		return error;
	}

	public void setError(HttpStatus error) {
		this.error = error;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getDetails() {
		return details;
	}

	public void setDetails(List<String> details) {
		this.details = details;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}
