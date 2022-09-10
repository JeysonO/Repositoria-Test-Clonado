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
import pe.com.amsac.tramite.api.request.body.bean.EstadoTramiteBodyRequest;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.EstadoTramiteResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.api.util.ServiceException;
import pe.com.amsac.tramite.bs.domain.EstadoTramite;
import pe.com.amsac.tramite.bs.service.EstadoTramiteService;

import javax.validation.Valid;

@RestController
@RequestMapping("/estados-tramites")
public class EstadoTramiteController {

	private static final Logger LOGGER = LogManager.getLogger(EstadoTramiteController.class);

	@Autowired
	private EstadoTramiteService estadoTramiteService;

	@Autowired
	private Mapper mapper;

	@PostMapping
	public ResponseEntity<CommonResponse> registrarEstadoTramite(@Valid @RequestBody EstadoTramiteBodyRequest estadoTramiteBodyrequest) throws Exception {

		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.CREATED;

		try {
			EstadoTramite estadoTramite = estadoTramiteService.registrarEstadoTramite(estadoTramiteBodyrequest);

			EstadoTramiteResponse estadoTramiteResponse = mapper.map(estadoTramite, EstadoTramiteResponse.class);

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(estadoTramiteResponse).build();


		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);

	}
}
