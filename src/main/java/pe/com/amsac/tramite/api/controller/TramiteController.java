package pe.com.amsac.tramite.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.SecurityHelper;
import pe.com.amsac.tramite.api.request.bean.TramiteRequest;
import pe.com.amsac.tramite.api.request.body.bean.TramiteBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.TramiteResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.Tramite;
import pe.com.amsac.tramite.bs.service.TramiteDerivacionService;
import pe.com.amsac.tramite.bs.service.TramiteService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tramites")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class TramiteController {
	//private static final Logger LOGGER = LogManager.getLogger(TramiteController.class);

	@Autowired
	private TramiteService tramiteService;

	@Autowired
	private Mapper mapper;

	@Autowired
	private SecurityHelper securityHelper;


	@GetMapping
	public ResponseEntity<CommonResponse> buscarTramiteParams(@Valid TramiteRequest tramiteRequest) throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			List<Tramite> listaTramite = tramiteService.buscarTramiteParams(tramiteRequest);
			List<TramiteResponse> obtenerTramiteList =  new ArrayList<>();
			TramiteResponse tramiteResponse = null;
			for (Tramite temp : listaTramite) {
				tramiteResponse = mapper.map(temp, TramiteResponse.class);
				obtenerTramiteList.add(tramiteResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramiteList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	//Para busqueda de los tramites iniciados por una persona
	@GetMapping("/tramites-by-usuario-id")
	public ResponseEntity<CommonResponse> buscarTramiteByUsuarioId() throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		String usuarioId = securityHelper.obtenerUserIdSession();
		List<Tramite> listaTramite = tramiteService.buscarTramiteParamsByUsuarioId(usuarioId);
		List<TramiteResponse> obtenerTramiteList =  new ArrayList<>();
		TramiteResponse tramiteResponse = null;
		for (Tramite temp : listaTramite) {
			tramiteResponse = mapper.map(temp, TramiteResponse.class);
			obtenerTramiteList.add(tramiteResponse);
		}
		commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramiteList).build();

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}

	@PostMapping
	public ResponseEntity<CommonResponse> registrarTramites(@Valid @RequestBody TramiteBodyRequest tramiteBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			Tramite tramite = tramiteService.registrarTramite(tramiteBodyrequest);

			TramiteResponse tramiteResponse = mapper.map(tramite, TramiteResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

	@PostMapping("/registrar-tramite-completo")
	public ResponseEntity<CommonResponse> registrarTramiteCompleto(@Valid @RequestBody TramiteBodyRequest tramiteBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			Tramite tramite = tramiteService.registrarTramite(tramiteBodyrequest);

			TramiteResponse tramiteResponse = mapper.map(tramite, TramiteResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramiteResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}

}
