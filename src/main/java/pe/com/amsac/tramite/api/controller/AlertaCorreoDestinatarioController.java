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
import pe.com.amsac.tramite.api.request.body.bean.AlertaCorreoDestinatarioBodyRequest;
import pe.com.amsac.tramite.api.response.bean.AlertaCorreoDestinatarioResponse;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.AlertaCorreoDestinatario;
import pe.com.amsac.tramite.bs.service.AlertaCorreoDestinatarioService;

import javax.validation.Valid;

@RestController
@RequestMapping("/alertas-correos-destinatarios")
public class AlertaCorreoDestinatarioController {

	private static final Logger LOGGER = LogManager.getLogger(AlertaCorreoDestinatarioController.class);

	@Autowired
	private AlertaCorreoDestinatarioService alertaCorreoDestinatarioService;

	@Autowired
	private Mapper mapper;

	@PostMapping
	public ResponseEntity<CommonResponse> registrarAlertaCorreoDestinatario(@Valid @RequestBody AlertaCorreoDestinatarioBodyRequest alertaCorreoDestinatarioBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			AlertaCorreoDestinatario alertaCorreoDestinatario = alertaCorreoDestinatarioService.registrarAlertaCorreoDestinatario(alertaCorreoDestinatarioBodyrequest);

			AlertaCorreoDestinatarioResponse alertaCorreoDestinatarioResponse = mapper.map(alertaCorreoDestinatario, AlertaCorreoDestinatarioResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(alertaCorreoDestinatarioResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
