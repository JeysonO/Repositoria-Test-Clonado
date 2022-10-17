package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.request.body.bean.SubsanacionTramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.request.body.bean.AtencionTramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.DerivarTramiteBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.TramiteDerivacionResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.service.TramiteDerivacionService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
	public ResponseEntity<CommonResponse> obtenerTramiteDerivacionPendientes() throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			List<TramiteDerivacion> listaTramiteDerivacionPendiente = tramiteDerivacionService.obtenerTramiteDerivacionPendientes();
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

	@GetMapping("/buscar-By-Params")
	public ResponseEntity<CommonResponse> buscarTramiteDerivacionParams(@Valid TramiteDerivacionRequest tramiteDerivacionRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			List<TramiteDerivacion> listaTramiteDerivacion = tramiteDerivacionService.buscarTramiteDerivacionParams(tramiteDerivacionRequest);
			List<TramiteDerivacionResponse> obtenerTramiteDerivacionList =  new ArrayList<>();
			TramiteDerivacionResponse tramiteDerivacionResponse = null;
			for (TramiteDerivacion temp : listaTramiteDerivacion) {
				tramiteDerivacionResponse = mapper.map(temp, TramiteDerivacionResponse.class);
				obtenerTramiteDerivacionList.add(tramiteDerivacionResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramiteDerivacionList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PostMapping
	public ResponseEntity<CommonResponse> registrarTramitesDerivacion(@Valid @RequestBody TramiteDerivacionBodyRequest tramiteDerivacionBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			String usuarioInicioId = securityHelper.obtenerUserIdSession();
			tramiteDerivacionBodyrequest.setUsuarioInicio(usuarioInicioId);

			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarTramiteDerivacion(tramiteDerivacionBodyrequest);

			//LocalDate localDate = tramiteDerivacion.getFechaMaximaAtencion().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			//tramiteDerivacion.setFechaMaximaAtencion(null);

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);
			//tramiteDerivacionResponse.setFechaMaximaAtencion(localDate);

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

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PostMapping("/derivacion-tramite")
	public ResponseEntity<CommonResponse> derivarTramiteDerivacion(@Valid @RequestBody DerivarTramiteBodyRequest derivartramiteBodyrequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarDerivacionTramite(derivartramiteBodyrequest);

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

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

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

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

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

}
