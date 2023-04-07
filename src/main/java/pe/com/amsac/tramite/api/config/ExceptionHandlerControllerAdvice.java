package pe.com.amsac.tramite.api.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pe.com.amsac.tramite.api.config.exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked","rawtypes"})
//@ControllerAdvice
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler{
	
	private static final Logger LOGGER = LogManager.getLogger(ExceptionHandlerControllerAdvice.class);
	
	@Autowired
	private Environment env;
	
	@ExceptionHandler(BadRequestException.class)
	public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
		String badErrorMessage = ex.getMessage()==null?env.getProperty("app.api.error.bad_request"):ex.getMessage();
		ExceptionResponse exceptionResponse = createExceptionResponse(ex, HttpStatus.BAD_REQUEST, request, badErrorMessage);
		LOGGER.error("Error handleBadRequestException ", ex);
		return new ResponseEntity<Object>(
				exceptionResponse, new HttpHeaders(), exceptionResponse.getError());
    }
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		// TODO Auto-generated method stub
		LOGGER.error("Error handleExceptionInternal ", ex);
		String mensaje = env.getProperty("app.api.error.internal_server_error");
		if(status == HttpStatus.BAD_REQUEST) {
			mensaje = env.getProperty("app.api.error.bad_request");
		}
			
		ExceptionResponse exceptionResponse = createExceptionResponse(ex, status, request, mensaje);
		
		return new ResponseEntity<Object>(
				exceptionResponse, headers, exceptionResponse.getError());
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
		ExceptionResponse exceptionResponse = createExceptionResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request, env.getProperty("app.api.error.internal_server_error"));
		LOGGER.error("Error handleAllExceptions ", ex);
		return new ResponseEntity<Object>(
				exceptionResponse, new HttpHeaders(), exceptionResponse.getError());
    }
	
	@ExceptionHandler(AppException.class)
	public final ResponseEntity<Object> handleApiExceptions(Exception ex, WebRequest request) {
		ExceptionResponse exceptionResponse = createExceptionResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request, ex.getMessage());
		LOGGER.error("Error handleApiExceptions ", ex);
		return new ResponseEntity<Object>(
				exceptionResponse, new HttpHeaders(), exceptionResponse.getError());
    }
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public final ResponseEntity<Object> handleResourceNotFoundExceptions(ResourceNotFoundException ex, WebRequest request) {
		ExceptionResponse exceptionResponse = createExceptionResponse(ex, HttpStatus.NOT_FOUND, request, ex.getMessage());
		LOGGER.error("Error handleResourceNotFoundExceptions ", ex);
		return new ResponseEntity<Object>(
				exceptionResponse, new HttpHeaders(), exceptionResponse.getError());
    }
	/*
	@ExceptionHandler(AuthenticationException.class)
	public final ResponseEntity<Object> handleUnauthorizedExceptions(Exception ex, WebRequest request) {
		ExceptionResponse exceptionResponse = createExceptionResponse(ex, HttpStatus.UNAUTHORIZED, request, ex.getMessage());
		LOGGER.error("Error handleUnauthorizedExceptions ", ex);
		return new ResponseEntity<Object>(
				exceptionResponse, new HttpHeaders(), exceptionResponse.getError());
    }
    */
    
	@ExceptionHandler(ServiceException.class)
	public final ResponseEntity<Object> handleServiceException(ServiceException ex, WebRequest request) {
		ExceptionResponse exceptionResponse = createExceptionResponse(ex, HttpStatus.CONFLICT, request, ex.getMessage());
		LOGGER.error("Error handleServiceException ", ex);
		return new ResponseEntity<Object>(
				exceptionResponse, new HttpHeaders(), exceptionResponse.getError());
    }

	@ExceptionHandler(BusinessServiceException.class)
	public final ResponseEntity<Object> handleBusinessServiceException(ServiceException ex, WebRequest request) {
		ExceptionResponse exceptionResponse = createExceptionResponse(ex, HttpStatus.CONFLICT, request, ex.getMessage());
		LOGGER.error("Error handleBusinessServiceException ", ex);
		return new ResponseEntity<Object>(
				exceptionResponse, new HttpHeaders(), exceptionResponse.getError());
	}
	
	@ExceptionHandler(FileStorageException.class)
	public final ResponseEntity<Object> handleFileStorageException(FileStorageException ex, WebRequest request) {
		ExceptionResponse exceptionResponse = createExceptionResponse(ex, HttpStatus.CONFLICT, request, ex.getMessage());
		LOGGER.error("Error handleFileStorageException ", ex);
		return new ResponseEntity<Object>(
				exceptionResponse, new HttpHeaders(), exceptionResponse.getError());
    }
	
	private ExceptionResponse createExceptionResponse(Exception ex, HttpStatus httpStatus, WebRequest request, String errorMessage){
		List<String> details = new ArrayList<>();
		if(ex instanceof MethodArgumentNotValidException) {
			details = ((MethodArgumentNotValidException)ex).getBindingResult()
	                .getFieldErrors()
	                .stream()
	                .map(x -> x.getField().concat(" ").concat(x.getDefaultMessage()))
	                .collect(Collectors.toList());
		}
        ExceptionResponse error = new ExceptionResponse(httpStatus,Integer.valueOf(httpStatus.value()), errorMessage,details,((ServletWebRequest)request).getRequest().getRequestURI().toString());
        return error;
	}
	
}
