package pe.com.amsac.tramite.api.controller;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.bean.TramiteDashboardRequest;
import pe.com.amsac.tramite.api.request.body.bean.*;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.TramiteDerivacionResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.dto.DetalleDashboardDTO;
import pe.com.amsac.tramite.bs.service.TramiteDerivacionService;
import pe.com.amsac.tramite.bs.util.FormaDerivacionConstant;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/tramites-derivaciones")
@Slf4j
public class TramiteDerivacionController {
	private static final Logger LOGGER = LogManager.getLogger(TramiteDerivacionController.class);

	@Autowired
	private TramiteDerivacionService tramiteDerivacionService;

	@Autowired
	private Mapper mapper;

	@Autowired
	private SecurityHelper securityHelper;

	@GetMapping("/{id}")
	public ResponseEntity<CommonResponse> obtenerTramiteDerivacionById(@PathVariable String id) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.obtenerTramiteDerivacionById(id);

			LocalDate fechaMaxima = null;
			if (tramiteDerivacion.getFechaMaximaAtencion()!=null){
				fechaMaxima =tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				tramiteDerivacion.setFechaMaximaAtencion(null);
			}

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

			if(fechaMaxima!=null)
				tramiteDerivacionResponse.setFechaMaximaAtencion(fechaMaxima);

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
		} catch (Exception ex){
			log.error("ERROR pendientes",ex);
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
			boolean marcadoUltimoDerivado = false;
			for (TramiteDerivacion temp : listaTramiteDerivacion) {
				if (temp.getFechaMaximaAtencion()!=null){
					fechaMaxima =temp.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					temp.setFechaMaximaAtencion(null);
				}
				tramiteDerivacionResponse = mapper.map(temp, TramiteDerivacionResponse.class);
				if(fechaMaxima!=null)
					tramiteDerivacionResponse.setFechaMaximaAtencion(fechaMaxima);

				//Marcamos como ultimo derivado
				if(!marcadoUltimoDerivado && tramiteDerivacionResponse.getForma().equals(FormaDerivacionConstant.ORIGINAL)){
					tramiteDerivacionResponse.setUltimoOriginalDerivado("S");
					marcadoUltimoDerivado = true;
				}

				obtenerTramiteDerivacionList.add(tramiteDerivacionResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramiteDerivacionList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
			log.error("ERROR",se);
		}	catch (Exception ex) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, null)).build();
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			log.error("ERROR",ex);
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

			String dependenciaIdUserSession = securityHelper.obtenerDependenciaIdUserSession();
			if(!StringUtils.isBlank(dependenciaIdUserSession)){
				tramiteDerivacionBodyrequest.setDependenciaIdUsuarioInicio(dependenciaIdUserSession);
			}

			String cargoIdUserSession = securityHelper.obtenerCargoIdUserSession();
			if(!StringUtils.isBlank(cargoIdUserSession)){
				tramiteDerivacionBodyrequest.setCargoIdUsuarioInicio(cargoIdUserSession);
			}

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

			TramiteDerivacionResponse tramiteDerivacionResponse = new TramiteDerivacionResponse(); //  mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);
			tramiteDerivacionResponse.setId(tramiteDerivacion.getId());

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

	@GetMapping("/tramites-pendientes/record-count")
	public ResponseEntity<CommonResponse> cantidadRegistrosObtenerTramiteDerivacionPendientes(@Valid TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {

			String idUser =  securityHelper.obtenerUserIdSession();
			tramiteDerivacionRequest.setUsuarioFin(idUser);

			String dependenciaId =  securityHelper.obtenerDependenciaIdUserSession();
			tramiteDerivacionRequest.setDependenciaIdUsuarioFin(dependenciaId);

			int cantidadRegistros = tramiteDerivacionService.totalRegistros(tramiteDerivacionRequest);

			Map<String, Object> param = new HashMap<>();
			param.put("cantidadRegistros", cantidadRegistros);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(param).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@GetMapping("/buscar-By-Params/record-count")
	public ResponseEntity<CommonResponse> cantidadRegistrosObtenerTramiteByParams(@Valid TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {

			int cantidadRegistros = tramiteDerivacionService.totalRegistros(tramiteDerivacionRequest);

			Map<String, Object> param = new HashMap<>();
			param.put("cantidadRegistros", cantidadRegistros);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(param).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}


	@GetMapping("/buscar-By-Params-dashboard")
	public ResponseEntity<CommonResponse> buscarTramiteDerivacionParamsDashboard(@Valid TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;

		Map mapaResult = tramiteDerivacionService.buscarTramiteDerivacionParamsDashboard(tramiteDerivacionRequest);

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(mapaResult).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PostMapping("/notificar")
	public ResponseEntity<CommonResponse> notificarTramite(
			@RequestParam(value = "tramiteDerivacionId", required = true) String tramiteDerivacionId,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "mensaje", required = false) String mensaje,
			@RequestParam(value = "file", required = false) MultipartFile file,
			@RequestParam(value = "esRechazo", required = false) boolean esRechazo) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try{

			TramiteDerivacionNotificacionBodyRequest tramiteDerivacionNotificacionBodyRequest = TramiteDerivacionNotificacionBodyRequest.builder()
					.tramiteDerivacionId(tramiteDerivacionId)
					.email(email)
					.mensaje(mensaje)
					.file(file)
					.esRechazo(esRechazo).build();

			tramiteDerivacionService.notificar(tramiteDerivacionNotificacionBodyRequest);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

	}

	@PostMapping("/cancelar-tramite")
	public ResponseEntity<CommonResponse> cancelarTramite(
			@RequestParam(value = "tramiteDerivacionId", required = true) String tramiteDerivacionId,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "mensaje", required = false) String mensaje,
			@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try{

			TramiteDerivacionNotificacionBodyRequest tramiteDerivacionNotificacionBodyRequest = TramiteDerivacionNotificacionBodyRequest.builder()
					.tramiteDerivacionId(tramiteDerivacionId)
					.email(email)
					.mensaje(mensaje)
					.file(file).build();

			tramiteDerivacionService.cancelar(tramiteDerivacionNotificacionBodyRequest);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK,null)).build();

		}catch(ServiceException se){
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR,se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse,httpStatus);

	}

	@PostMapping("/registrar-tramite-derivacion-batch-lista")
	public ResponseEntity<CommonResponse> registrarTramitesDerivacionMigracionBatchLista(@Valid @RequestBody WrapperTramiteDerivacionBodyRequest tramiteDerivacionBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			//String usuarioInicioId = securityHelper.obtenerUserIdSession();
			//tramiteDerivacionBodyrequest.setUsuarioInicio(usuarioInicioId);
			//int secuencia = 2;
			for(TramiteDerivacionBodyRequest tramiteDerivacionMigracionBodyRequest : tramiteDerivacionBodyrequest.getTramiteDerivacion()){
				//tramiteDerivacionService.registrarTramiteDerivacionMigracion(tramiteDerivacionMigracionBodyRequest);
				//tramiteDerivacionService.registrarTramiteDerivacionMigracionBatch(tramiteDerivacionMigracionBodyRequest);
				//tramiteDerivacionMigracionBodyRequest.setSecuencia(secuencia++);
				tramiteDerivacionService.registrarTramiteDerivacionBatch(tramiteDerivacionMigracionBodyRequest, null);
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

	@GetMapping("/exportar-historial")
	//public ResponseEntity<Resource> downloadFileEscala(@Valid TramiteRequest tramiteRequest, HttpServletResponse response) throws Exception {
	public ResponseEntity<Resource> downloadHistorial(@Valid TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {

		JasperPrint jasperPrint = tramiteDerivacionService.exportarHistorial(tramiteDerivacionRequest);

		byte[] reporte = JasperExportManager.exportReportToPdf(jasperPrint);
		Resource resource = new ByteArrayResource(reporte);

		String contentType = "application/pdf";

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tramite-seguimiento-reporte.pdf")
				.body(resource);

	}

	/* Esta api fue de forma temporal  */
	/*
	@PostMapping("/atender-derivacion-lote")
	public ResponseEntity<CommonResponse> atenderDerivacionLote(@Valid @RequestBody AtenderDerivacionLoteRequest atenderDerivacionLoteRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		Map mapa = tramiteDerivacionService.atenderDerivacionLote(atenderDerivacionLoteRequest);

		Map<String, Object> param = new HashMap<>();
		param.put("tramites", atenderDerivacionLoteRequest.getTramites());
		param.put("usuario", atenderDerivacionLoteRequest.getUsuario());
		param.put("tramitesCerrados", mapa.get("tramitesCerrados").toString());
		param.put("derivacionesIdCerrados", mapa.get("derivacionesIdCerrados").toString());

		commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(param).build();


		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}
	*/

	//A este api se invoca desde la pantalla de reporte para obtener los valores resumidos del dashboard
	@GetMapping("/dashboard")
	public ResponseEntity<CommonResponse> dashboard(@Valid TramiteDashboardRequest tramiteDashboardRequest) throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;

		Map mapaResult = tramiteDerivacionService.indicadoresReporteByParams(tramiteDashboardRequest);

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(mapaResult).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	//A este api se invoca desde la pantalla de reporte para obtener el listado detalle
	@GetMapping("/detalle-by-params")
	public ResponseEntity<CommonResponse> detalleByParams(@Valid TramiteDashboardRequest tramiteDashboardRequest) throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;

		List<DetalleDashboardDTO> detalleDashboardDTOList = tramiteDerivacionService.detalleReporteByParams(tramiteDashboardRequest);

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(detalleDashboardDTOList).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@GetMapping("/detalle-by-params/record-count")
	public ResponseEntity<CommonResponse> detalleRecordCountByParams(@Valid TramiteDashboardRequest tramiteDashboardRequest) throws Exception {

		HttpStatus httpStatus = HttpStatus.OK;

		Integer cantidadRegistros = tramiteDerivacionService.detalleRecordCountByParams(tramiteDashboardRequest);

		CommonResponse commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(Map.of("cantidadRegistros", cantidadRegistros)).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@GetMapping("/detalle-by-params/exportar")
	public ResponseEntity<Resource> downloadDetalleByParams(@Valid TramiteDashboardRequest tramiteDashboardRequest) throws Exception {

		JasperPrint jasperPrint = tramiteDerivacionService.exportarReporteHistorial(tramiteDashboardRequest);

		byte[] reporte = JasperExportManager.exportReportToPdf(jasperPrint);
		Resource resource = new ByteArrayResource(reporte);

		String contentType = "application/pdf";

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte-detalle.pdf")
				.body(resource);

	}
}
