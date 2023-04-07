package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.body.bean.TramitePrioridadBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.TramitePrioridadResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.TramitePrioridad;
import pe.com.amsac.tramite.bs.service.TramitePrioridadService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tramites-prioridades")
public class TramitePrioridadController {
	private static final Logger LOGGER = LogManager.getLogger(TramitePrioridadController.class);

	@Autowired
	private TramitePrioridadService tramitePrioridadService;

	@Autowired
	private Mapper mapper;

	@GetMapping
	public ResponseEntity<CommonResponse> buscarTipoDocumento(){
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			List<TramitePrioridad> listaTramitePrioridad = tramitePrioridadService.findByAllTramitePrioridad();
			List<TramitePrioridadResponse> obtenerTramitePrioridadList =  new ArrayList<>();
			TramitePrioridadResponse tramitePrioridadResponse = null;
			for (TramitePrioridad temp : listaTramitePrioridad) {
				tramitePrioridadResponse = mapper.map(temp, TramitePrioridadResponse.class);
				obtenerTramitePrioridadList.add(tramitePrioridadResponse);
			}
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(obtenerTramitePrioridadList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}


	@PostMapping
	public ResponseEntity<CommonResponse> registrarTramitesPrioridad(@Valid @RequestBody TramitePrioridadBodyRequest tramitePrioridadBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			TramitePrioridad tramitePrioridad = tramitePrioridadService.registrarTramitePrioridad(tramitePrioridadBodyrequest);

			TramitePrioridadResponse tramitePrioridadResponse = mapper.map(tramitePrioridad, TramitePrioridadResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tramitePrioridadResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}