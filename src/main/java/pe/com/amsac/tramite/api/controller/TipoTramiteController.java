package pe.com.amsac.tramite.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.com.amsac.tramite.api.config.exceptions.ServiceException;
import pe.com.amsac.tramite.api.response.bean.CommonResponse;
import pe.com.amsac.tramite.api.response.bean.Meta;
import pe.com.amsac.tramite.api.response.bean.TipoTramiteResponse;
import pe.com.amsac.tramite.api.util.EstadoRespuestaConstant;
import pe.com.amsac.tramite.bs.domain.TipoTramite;
import pe.com.amsac.tramite.bs.service.TipoTramiteService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tipos-tramite")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class TipoTramiteController {
	private static final Logger LOGGER = LogManager.getLogger(TipoTramiteController.class);

	@Autowired
	private TipoTramiteService tipoTramiteService;

	@Autowired
	private Mapper mapper;

	@GetMapping
	public ResponseEntity<CommonResponse> obtenerTiposTramite() throws Exception {
		CommonResponse commonResponse = null;

		HttpStatus httpStatus = HttpStatus.OK;

		try {

			List<TipoTramite> tipoTramiteList =  tipoTramiteService.obtenerTiposTramiteActivos();
			List<TipoTramiteResponse> tipoTramiteResponseList = new ArrayList<>();
			tipoTramiteList.stream().forEach(x -> {
				tipoTramiteResponseList.add(mapper.map(x,TipoTramiteResponse.class));
			});

			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_OK, null)).data(tipoTramiteResponseList).build();

		} catch (ServiceException se) {
			commonResponse = CommonResponse.builder().meta(new Meta(EstadoRespuestaConstant.RESULTADO_ERROR, se.getMensajes())).build();
			httpStatus = HttpStatus.CONFLICT;
		}

		return new ResponseEntity<CommonResponse>(commonResponse, httpStatus);
	}


}
