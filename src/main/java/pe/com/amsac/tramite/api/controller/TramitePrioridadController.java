package pe.com.amsac.tramite.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.amsac.tramite.api.request.body.bean.TramitePrioridadBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.TramitePrioridadResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.TramitePrioridad;
import pe.com.amsac.tramite.bs.service.TramitePrioridadService;

import javax.validation.Valid;

@RestController
@RequestMapping("/tramites-prioridades")
public class TramitePrioridadController {
	private static final Logger LOGGER = LogManager.getLogger(TramitePrioridadController.class);

	@Autowired
	private TramitePrioridadService tramitePrioridadService;

	@Autowired
	private Mapper mapper;

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