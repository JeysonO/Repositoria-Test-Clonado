package pe.com.amsac.tramite.api.controller;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.request.body.bean.*;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.TramiteDerivacionResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.service.TramiteDerivacionService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/tramites-derivaciones")
public class TramiteDerivacionController {
	private static final Logger LOGGER = LogManager.getLogger(TramiteDerivacionController.class);

	@Autowired
	private TramiteDerivacionService tramiteDerivacionService;

	@Autowired
	private Mapper mapper;

	@Autowired
	private SecurityHelper securityHelper;

	@GetMapping("{id}")
	public ResponseEntity<CommonResponse> obtenerTramiteDerivacionById(@PathVariable String id) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.obtenerTramiteDerivacionById(id);

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@GetMapping
	public ResponseEntity<CommonResponse> obtenerTramiteDerivacionPendientes(@Valid TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			/*
			En este request se aplican los siguientes filtros
			estado = P o A, de acuerdo a lo que necesita
			numeroTramite para un tramite especifico
			asunto: un like, basicamente esos filtros
			 */
			List<TramiteDerivacion> listaTramiteDerivacionPendiente = tramiteDerivacionService.obtenerTramiteDerivacionPendientes(tramiteDerivacionRequest);
			List<TramiteDerivacionResponse> obtenerTramiteDerivacionPendienteList =  new ArrayList<>();
			TramiteDerivacionResponse tramiteDerivacionResponse = null;
			LocalDate fechaMaxima = null;
			for (TramiteDerivacion temp : listaTramiteDerivacionPendiente) {
				if (temp.getFechaMaximaAtencion()!=null){
					fechaMaxima =temp.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					temp.setFechaMaximaAtencion(null);
				}
				tramiteDerivacionResponse = mapper.map(temp, TramiteDerivacionResponse.class);
				if(fechaMaxima!=null)
					tramiteDerivacionResponse.setFechaMaximaAtencion(fechaMaxima);
				obtenerTramiteDerivacionPendienteList.add(tramiteDerivacionResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramiteDerivacionPendienteList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@GetMapping("/buscar-By-Params")
	public ResponseEntity<CommonResponse> buscarTramiteDerivacionParams(@Valid TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			List<TramiteDerivacion> listaTramiteDerivacion = tramiteDerivacionService.buscarTramiteDerivacionParams(tramiteDerivacionRequest);
			List<TramiteDerivacionResponse> obtenerTramiteDerivacionList =  new ArrayList<>();
			TramiteDerivacionResponse tramiteDerivacionResponse = null;
			LocalDate fechaMaxima = null;
			for (TramiteDerivacion temp : listaTramiteDerivacion) {
				if (temp.getFechaMaximaAtencion()!=null){
					fechaMaxima =temp.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					temp.setFechaMaximaAtencion(null);
				}
				tramiteDerivacionResponse = mapper.map(temp, TramiteDerivacionResponse.class);
				if(fechaMaxima!=null)
					tramiteDerivacionResponse.setFechaMaximaAtencion(fechaMaxima);
				obtenerTramiteDerivacionList.add(tramiteDerivacionResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramiteDerivacionList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	//Este servicio se invoca cuando registro la derivacion de mi tramite al momento de registrar un tramite interno
	@PostMapping
	public ResponseEntity<CommonResponse> registrarTramitesDerivacion(@Valid @RequestBody TramiteDerivacionBodyRequest tramiteDerivacionBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			String usuarioInicioId = securityHelper.obtenerUserIdSession();
			tramiteDerivacionBodyrequest.setUsuarioInicio(usuarioInicioId);

			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrar(tramiteDerivacionBodyrequest);

			LocalDate localDate = null;
			if(tramiteDerivacion.getFechaMaximaAtencion()!=null){
				localDate = tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				tramiteDerivacion.setFechaMaximaAtencion(null);
			}

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);
			if(localDate!=null)
				tramiteDerivacionResponse.setFechaMaximaAtencion(localDate);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}


	@PutMapping("/enviar-a-subsanacion")
	public ResponseEntity<CommonResponse> subsanarTramiteDerivacion(@Valid @RequestBody SubsanacionTramiteDerivacionBodyRequest subsanartramiteDerivacionBodyrequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.subsanarTramiteDerivacion(subsanartramiteDerivacionBodyrequest);

			LocalDate localDate = null;
			if(tramiteDerivacion.getFechaMaximaAtencion()!=null){
				localDate = tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				tramiteDerivacion.setFechaMaximaAtencion(null);
			}
			
			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);
			if(localDate!=null)
				tramiteDerivacionResponse.setFechaMaximaAtencion(localDate);
			
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	//Cuando la persona que tiene le tramite lo deriva a otra persona
	@PostMapping("/derivacion-tramite")
	public ResponseEntity<CommonResponse> derivarTramiteDerivacion(@Valid @RequestBody DerivarTramiteBodyRequest derivartramiteBodyrequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarDerivacionTramite(derivartramiteBodyrequest);

			LocalDate localDate = null;
			if(tramiteDerivacion.getFechaMaximaAtencion()!=null){
				localDate = tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				tramiteDerivacion.setFechaMaximaAtencion(null);
			}

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);
			if(localDate!=null)
				tramiteDerivacionResponse.setFechaMaximaAtencion(localDate);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PostMapping("/recepcionar-tramite-derivacion/{idTramiteDerivacion}")
	public ResponseEntity<CommonResponse> recepcionarTramiteDerivacion(@PathVariable String idTramiteDerivacion) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarRecepcionTramiteDerivacion(idTramiteDerivacion);

			LocalDate localDate = null;
			if(tramiteDerivacion.getFechaMaximaAtencion()!=null){
				localDate = tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				tramiteDerivacion.setFechaMaximaAtencion(null);
			}

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);
			if(localDate!=null)
				tramiteDerivacionResponse.setFechaMaximaAtencion(localDate);
			
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PutMapping("/atender-tramite-derivacion")
	public ResponseEntity<CommonResponse> atenderTramiteDerivacion(@Valid @RequestBody AtencionTramiteDerivacionBodyRequest atenciontramiteDerivacionBodyrequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarAtencionTramiteDerivacion(atenciontramiteDerivacionBodyrequest);

			LocalDate localDate = null;
			if(tramiteDerivacion.getFechaMaximaAtencion()!=null){
				localDate = tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				tramiteDerivacion.setFechaMaximaAtencion(null);
			}
			
			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);
			if(localDate!=null)
				tramiteDerivacionResponse.setFechaMaximaAtencion(localDate);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PutMapping("/rechazar-tramite")
	public ResponseEntity<CommonResponse> rechazarTramiteDerivacion(@Valid @RequestBody RechazarTramiteDerivacionBodyRequest rechazarTramiteDerivacionBodyRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.rechazarTramiteDerivacion(rechazarTramiteDerivacionBodyRequest);

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@GetMapping("/obtener-derivacion-by-tramite-id/{tramiteId}")
	public ResponseEntity<CommonResponse> obtenerTramiteDerivacionByTramiteId(@PathVariable String tramiteId) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			List<TramiteDerivacion> listaTramiteDerivacionPendiente = tramiteDerivacionService.obtenerTramiteDerivacionByTramiteId(tramiteId);
			List<TramiteDerivacionResponse> obtenerTramiteDerivacionPendienteList =  new ArrayList<>();
			TramiteDerivacionResponse tramiteDerivacionResponse = null;
			for (TramiteDerivacion temp : listaTramiteDerivacionPendiente) {
				tramiteDerivacionResponse = mapper.map(temp, TramiteDerivacionResponse.class);
				obtenerTramiteDerivacionPendienteList.add(tramiteDerivacionResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramiteDerivacionPendienteList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PostMapping("/registrar-tramite-derivacion-migracion")
	public ResponseEntity<CommonResponse> registrarTramitesDerivacionMigracion(@Valid @RequestBody TramiteDerivacionMigracionBodyRequest tramiteDerivacionBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			//String usuarioInicioId = securityHelper.obtenerUserIdSession();
			//tramiteDerivacionBodyrequest.setUsuarioInicio(usuarioInicioId);

			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarTramiteDerivacionMigracion(tramiteDerivacionBodyrequest);
			/*
			LocalDate localDate = null;
			if(tramiteDerivacion.getFechaMaximaAtencion()!=null){
				localDate = tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				tramiteDerivacion.setFechaMaximaAtencion(null);
			}
			*/

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	@PostMapping("/registrar-tramite-derivacion-migracion-lista")
	public ResponseEntity<CommonResponse> registrarTramitesDerivacionMigracionLista(@Valid @RequestBody WrapperTramiteDerivacionMigracionBodyRequest tramiteDerivacionBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			//String usuarioInicioId = securityHelper.obtenerUserIdSession();
			//tramiteDerivacionBodyrequest.setUsuarioInicio(usuarioInicioId);

			for(TramiteDerivacionMigracionBodyRequest tramiteDerivacionMigracionBodyRequest : tramiteDerivacionBodyrequest.getTramiteDerivacion()){
				tramiteDerivacionService.registrarTramiteDerivacionMigracion(tramiteDerivacionMigracionBodyRequest);
			}

			//TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarTramiteDerivacionMigracion(tramiteDerivacionBodyrequest);
			/*
			LocalDate localDate = null;
			if(tramiteDerivacion.getFechaMaximaAtencion()!=null){
				localDate = tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				tramiteDerivacion.setFechaMaximaAtencion(null);
			}
			*/

			//TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
