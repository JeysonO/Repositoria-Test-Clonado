package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.request.bean.TramiteDerivacionRequest;
import pe.com.amsac.tramite.api.request.body.bean.AtencionTramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteDerivacionBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.TramiteDerivacionResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.TramiteDerivacion;
import pe.com.amsac.tramite.bs.service.TramiteDerivacionService;

import javax.validation.Valid;
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

	@GetMapping
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
			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarTramiteDerivacion(tramiteDerivacionBodyrequest);

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

	@PutMapping("/recepcionar-tramite-derivacion")
	public ResponseEntity<CommonResponse> recepcionarTramiteDerivacion(@Valid @RequestBody AtencionTramiteDerivacionBodyRequest atenciontramiteDerivacionBodyrequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			/*
			1. Tomar el tramite derivacion actual y actualizarlo con los siguientes valores:
			estadoFin: RECEPCIONADO
			fechaFin: fecha actual
			estado: A
			comentario: TRAMITE RECEPCIONADO

			Va a crear otro registro, con la secuencia siguiente, pasa saber la siguiente secuencia, hacar una consulta
			a la tabla tramite_derivacion con el id del tramite, lo ordenaras de forma descendente para obtener la ultima secuencia, le suma 1
			y generas una copia del tramite _derivacion que estas atendiendo:
			actualizas los valores:
			estadoInicio: RECEPCIONADO
			fechaInicio: fecha actuak
			fechaFin: nulo
			estadoFin: nulo
			estado: P
			secuencia: secuencia anterior + 1.


			*/


			TramiteDerivacion tramiteDerivacion = tramiteDerivacionService.registrarAtencionTramiteDerivacion(atenciontramiteDerivacionBodyrequest);

			TramiteDerivacionResponse tramiteDerivacionResponse = mapper.map(tramiteDerivacion, TramiteDerivacionResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteDerivacionResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PutMapping("/derivacion-tramite-derivacion")
	public ResponseEntity<CommonResponse> derivarTramiteDerivacion(@Valid @RequestBody AtencionTramiteDerivacionBodyRequest atenciontramiteDerivacionBodyrequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {

			/*


			*/


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
