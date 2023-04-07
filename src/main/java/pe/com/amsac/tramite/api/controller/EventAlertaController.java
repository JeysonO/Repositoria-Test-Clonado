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
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.request.body.bean.EventAlertaBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.EventAlertaResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.EventAlerta;
import pe.com.amsac.tramite.bs.service.EventAlertaService;

import javax.validation.Valid;

@RestController
@RequestMapping("/eventos-alertas")
public class EventAlertaController {

	private static final Logger LOGGER = LogManager.getLogger(EventAlertaController.class);

	@Autowired
	private EventAlertaService eventAlertaService;

	@Autowired
	private Mapper mapper;

	@PostMapping
	public ResponseEntity<CommonResponse> registrarAlertaCorreoDestinatario(@Valid @RequestBody EventAlertaBodyRequest eventAlertaBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			EventAlerta eventAlerta = eventAlertaService.registrarEventAlerta(eventAlertaBodyrequest);

			EventAlertaResponse eventAlertaResponse = mapper.map(eventAlerta, EventAlertaResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(eventAlertaResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
